package de.maltsev.gradle.semanticrelease

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.getOrElse
import arrow.core.toOption
import de.maltsev.gradle.semanticrelease.ci.CITool
import de.maltsev.gradle.semanticrelease.ci.detection.TravisDetection
import de.maltsev.gradle.semanticrelease.extensions.SemanticVersionPattern
import de.maltsev.gradle.semanticrelease.vcs.VCSSource
import de.maltsev.gradle.semanticrelease.vcs.github.Github
import de.maltsev.gradle.semanticrelease.versions.SemanticCommitStrategy
import de.maltsev.gradle.semanticrelease.versions.SemanticVersion
import de.maltsev.gradle.semanticrelease.versions.SemanticVersionParser
import de.maltsev.gradle.semanticrelease.versions.VersionParser
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author Evgeniy Maltsev
 */
class SemanticReleasePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val travis = TravisDetection().detectCI().orNull()
        if (travis != null) {
            val github = Github(travis)
            val semanticStrategy = SemanticCommitStrategy(DEFAULT_OTHER_CHANGE_TYPES)
            val pattern = SemanticVersionPattern()
            val versionParser = SemanticVersionParser(SemanticVersionPattern())
            val inferStaged = project.findProperty("semantic.infer.staged") as? Boolean ?: true
            inferVersion(project, travis, github, semanticStrategy, versionParser, inferStaged, pattern)
            registerPublishReleaseNotesTask(project, travis)
        }
    }

    private fun inferVersion(project: Project, travis: CITool, github: VCSSource, semanticCommitStrategy: SemanticCommitStrategy, versionParser: VersionParser, inferStaged: Boolean, pattern: SemanticVersionPattern) {
        val latestVersionContext = latestVersionContext(github, semanticCommitStrategy, versionParser)
        val nextVersion = nextVersion(latestVersionContext, inferStaged, travis, pattern)

        when (latestVersionContext) {
            is Some -> project.logger.lifecycle("Current version from vcs: ${latestVersionContext.t.latestVersion}")
            is None -> project.logger.lifecycle("No current version found in vcs. Next version will be set to default: $nextVersion")
        }

        val latestVersion = latestVersionContext.map { it.latestVersion }.getOrElse { null }

        if (latestVersion == nextVersion || (travis.isStage())) {
            project.logger.lifecycle("No next version.")
            if (latestVersion != null) {
                project.version = latestVersion
                project.subprojects {
                    it.version = latestVersion
                }
            }
        } else {
            project.logger.lifecycle("Next semantic version: $nextVersion.")
            project.version = nextVersion
            project.subprojects {
                it.version = nextVersion
            }
            project.extensions.extraProperties["latestVersionContext"] = latestVersionContext
            project.extensions.extraProperties["semanticVersion"] = nextVersion
        }
    }

    private fun latestVersionContext(vcsSource: VCSSource, semanticStrategy: SemanticCommitStrategy, versionParser: VersionParser): Option<VersionContext> {
        return vcsSource.latestRelease().map { latestRelease ->
            val commits = vcsSource.commitsBefore(latestRelease)
            val changes = commits.map(semanticStrategy::execute)
            val latestVersion = versionParser.parse(latestRelease.version)
            VersionContext(
                latestVersion = latestVersion,
                changes = changes,
                commits = commits
            )
        }
    }

    private fun nextVersion(latestVersionContext: Option<VersionContext>, inferStaged: Boolean, ciTool: CITool, pattern: SemanticVersionPattern): SemanticVersion = when (latestVersionContext) {
        is Some -> {
            val latestVersion = latestVersionContext.t.latestVersion
            val changes = latestVersionContext.t.changes
            when {
                inferStaged && ciTool.isStage() && !latestVersion.staged() -> latestVersion
                    .withStageAt(
                        SemanticVersion.Stage(
                            name = ciTool.currentBranchName(),
                            commit = latestVersionContext.t.commits.count())
                    )
                else -> latestVersion.nextVersion(changes)
            }
        }
        is None -> createFirstVersion(pattern, ciTool, inferStaged)
    }

    private fun createFirstVersion(pattern: SemanticVersionPattern, ciTool: CITool, inferStaged: Boolean) = SemanticVersion(
        prefix = pattern.versionPrefix,
        major = 0,
        minor = 1,
        patch = 0,
        stage = ciTool.isStage().toOption().filter { inferStaged && it }.map {
            SemanticVersion.Stage(
                name = ciTool.currentBranchName(),
                commit = 1)
        }
    )

    private fun registerPublishReleaseNotesTask(project: Project, travis: CITool) {
        project.tasks.register(SEMANTIC_PUBLISH, SemanticReleasePublish::class.java) { publishReleaseNotes ->
            publishReleaseNotes.onlyIf { project.version != "unspecified" && project.hasProperty("latestVersionContext") && project.hasProperty("semanticVersion") }
            publishReleaseNotes.group = "Semantic Version"
            publishReleaseNotes.description = "Creates release in GitHub with release notes"
            publishReleaseNotes.ciTool.set(travis)
            publishReleaseNotes.vcsSource.set(Github(travis))
        }
    }

    companion object {
        private const val SEMANTIC_PUBLISH = "semanticReleasePublish"
        private val DEFAULT_OTHER_CHANGE_TYPES = mapOf(
            "perf" to "Performance Improvements",
            "revert" to "Reverts",
            "docs" to "Documentation",
            "style" to "Style",
            "refactor" to "Code Refactoring",
            "test" to "Tests",
            "chore" to "Chores"
        )
    }
}

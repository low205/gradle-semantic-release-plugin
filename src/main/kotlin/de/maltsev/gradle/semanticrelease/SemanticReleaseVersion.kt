package de.maltsev.gradle.semanticrelease

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.getOrElse
import arrow.core.toOption
import de.maltsev.gradle.semanticrelease.ci.CITool
import de.maltsev.gradle.semanticrelease.extensions.SemanticVersionPattern
import de.maltsev.gradle.semanticrelease.extensions.lazy
import de.maltsev.gradle.semanticrelease.vcs.VCSSource
import de.maltsev.gradle.semanticrelease.vcs.VcsCommit
import de.maltsev.gradle.semanticrelease.versions.SemanticStrategy
import de.maltsev.gradle.semanticrelease.versions.SemanticVersion
import de.maltsev.gradle.semanticrelease.versions.VersionParser
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

open class SemanticReleaseVersion : DefaultTask() {
    @Input
    val vcsSource: Property<VCSSource> = project.lazy()

    @Input
    val ciTool: Property<CITool> = project.lazy()

    @Input
    val semanticCommitStrategy: Property<SemanticStrategy<VcsCommit>> = project.lazy()

    @Input
    val versionParser: Property<VersionParser> = project.lazy()

    @Input
    val inferStaged: Property<Boolean> = project.lazy()

    @Internal
    val semanticVersionPattern: Property<SemanticVersionPattern> = project.lazy()

    @Internal
    private var latestVersionContext: Option<VersionContext> = None

    @Internal
    private var nextVersion: SemanticVersion? = null

    fun getLatestVersionContext(): Option<VersionContext> {
        return latestVersionContext
    }

    fun getNextVersion(): Option<SemanticVersion> {
        return nextVersion.toOption()
    }

    @TaskAction
    fun action() {
        latestVersionContext = latestVersionContext()
        nextVersion = nextVersion(latestVersionContext)

        when (val context = latestVersionContext) {
            is Some -> logger.lifecycle("Current version from vcs: $${context.t.latestVersion}")
            is None -> logger.lifecycle("No current version found in vcs. Next version will be set to default: $nextVersion")
        }

        val latestVersion = latestVersionContext.map { it.latestVersion }.getOrElse { null }

        if (latestVersion == nextVersion || (ciTool.get().isStage() && !inferStaged.get())) {
            logger.lifecycle("No next version.")
            nextVersion = null
        } else {
            logger.lifecycle("Next semantic version: $nextVersion.")
            project.version = nextVersion.toString()
        }
    }

    private fun latestVersionContext(): Option<VersionContext> {
        return vcsSource.get().latestRelease().map { latestRelease ->
            val commits = vcsSource.get().commitsBefore(latestRelease)
            val changes = commits.map(semanticCommitStrategy.get()::execute)
            val latestVersion = versionParser.get().parse(latestRelease.version)
            VersionContext(
                latestVersion = latestVersion,
                changes = changes,
                commits = commits
            )
        }
    }

    private fun nextVersion(latestVersionContext: Option<VersionContext>): SemanticVersion = when (latestVersionContext) {
        is Some -> {
            val latestVersion = latestVersionContext.t.latestVersion
            val changes = latestVersionContext.t.changes
            when {
                inferStaged.get() && ciTool.get().isStage() && !latestVersion.staged() -> latestVersion
                    .withStageAt(
                        SemanticVersion.Stage(
                            name = ciTool.get().currentBranchName(),
                            commit = latestVersionContext.t.commits.count())
                    )
                else -> latestVersion.nextVersion(changes)
            }
        }
        is None -> createFirstVersion()
    }

    private fun createFirstVersion() = SemanticVersion(
        prefix = semanticVersionPattern.get().versionPrefix,
        major = 0,
        minor = 1,
        patch = 0,
        stage = ciTool.get().isStage().toOption().filter { inferStaged.get() && it }.map {
            SemanticVersion.Stage(
                name = ciTool.get().currentBranchName(),
                commit = 1)
        }
    )
}

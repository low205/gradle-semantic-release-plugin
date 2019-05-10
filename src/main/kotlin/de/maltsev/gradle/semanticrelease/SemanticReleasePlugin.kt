package de.maltsev.gradle.semanticrelease

import de.maltsev.gradle.semanticrelease.ci.CITool
import de.maltsev.gradle.semanticrelease.ci.detection.TravisDetection
import de.maltsev.gradle.semanticrelease.extensions.SemanticReleaseExtension
import de.maltsev.gradle.semanticrelease.extensions.SemanticVersionPattern
import de.maltsev.gradle.semanticrelease.vcs.github.Github
import de.maltsev.gradle.semanticrelease.versions.SemanticCommitStrategy
import de.maltsev.gradle.semanticrelease.versions.SemanticVersionParser
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.UnknownTaskException
import org.gradle.language.base.plugins.LifecycleBasePlugin

/**
 * @author Evgeniy Maltsev
 */
class SemanticReleasePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create(SEMANTIC_VERSION, SemanticReleaseExtension::class.java, project)
        val travis = TravisDetection().detectCI().orNull()
        if (travis != null) {
            registerSemanticReleaseTask(project, extension, travis)
            registerPublishReleaseNotesTask(project, travis)
        }
    }

    private fun registerSemanticReleaseTask(project: Project, extension: SemanticReleaseExtension, travis: CITool) {
        val buildTaskProvider = try {
            project.tasks.named(LifecycleBasePlugin.BUILD_TASK_NAME)
        } catch (ignored: UnknownTaskException) {
            null
        }

        project.tasks.register(SEMANTIC_VERSION, SemanticReleaseVersion::class.java) { semanticRelease ->
            semanticRelease.dependsOn(buildTaskProvider)
            semanticRelease.group = "Semantic Version"
            semanticRelease.description = "Infers version based on changes from last release in current branch"
            semanticRelease.ciTool.set(travis)
            semanticRelease.vcsSource.set(Github(travis))
            semanticRelease.inferStaged.set(extension.inferStaged)
            semanticRelease.semanticCommitStrategy.set(extension.otherChangeTypes.map { SemanticCommitStrategy(it) })
            semanticRelease.semanticVersionPattern.set(SemanticVersionPattern())
            semanticRelease.versionParser.set(semanticRelease.semanticVersionPattern.map { SemanticVersionParser(it) })
        }
    }

    private fun registerPublishReleaseNotesTask(project: Project, travis: CITool) {
        val releaseTaskProvider = try {
            project.tasks.named(SEMANTIC_VERSION)
        } catch (ignored: UnknownTaskException) {
            null
        }

        project.tasks.register(SEMANTIC_PUBLISH, SemanticReleasePublish::class.java) { publishReleaseNotes ->
            publishReleaseNotes.dependsOn(releaseTaskProvider)
            publishReleaseNotes.group = "Semantic Version"
            publishReleaseNotes.description = "Creates release in GitHub with release notes gathered by $SEMANTIC_VERSION task"
            publishReleaseNotes.ciTool.set(travis)
            publishReleaseNotes.vcsSource.set(Github(travis))
        }
    }

    companion object {
        private const val SEMANTIC_VERSION = "semanticReleaseVersion"
        private const val SEMANTIC_PUBLISH = "semanticReleasePublish"
    }
}

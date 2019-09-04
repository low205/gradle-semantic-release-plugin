package de.maltsev.gradle.semanticrelease

import de.maltsev.gradle.semanticrelease.ci.travis.Travis
import de.maltsev.gradle.semanticrelease.publish.PublishSemanticReleaseGitHubTask
import de.maltsev.gradle.semanticrelease.vcs.github.Github
import de.maltsev.gradle.semanticrelease.versions.VersionContext
import de.maltsev.gradle.semanticrelease.versions.VersionInferTask
import de.maltsev.gradle.semanticrelease.versions.VersionInferTaskOnBranch
import de.maltsev.gradle.semanticrelease.versions.VersionInferTaskOnTarget
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author Evgeniy Maltsev
 */
class SemanticReleasePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val env = SystemEnvironment()
        val extension = project.extensions.create(SEMANTIC_RELEASE, SemanticReleasePluginExtension::class.java, project.objects)
        val factory = DefaultFactory(env, extension)
        val ciTool = factory.travis ?: return
        val vscTool = factory.gitHub ?: return
        val inferTask = inferVersion(ciTool, vscTool, project, extension) ?: return
        val versionContext = inferTask.getVersionContext()
        setProjectVersion(project, versionContext)
        registerPublishReleaseNotesTask(project, ciTool.currentBranchName(), versionContext, factory, extension)
    }

    private fun inferVersion(
        ciTool: Travis,
        vscTool: Github,
        project: Project,
        extension: SemanticReleasePluginExtension
    ): VersionInferTask? {
        val onTargetBranch = ciTool.currentBranchName() == extension.targetBranch.get()
        val inferOnNotTargetBranch = extension.inferVersion.get() == VersionInference.ALWAYS
        return if (!onTargetBranch && inferOnNotTargetBranch) {
            project.extensions.extraProperties[IS_ON_TARGET] = false
            VersionInferTaskOnBranch(ciTool.currentBranchName(), vscTool)
        } else if (onTargetBranch) {
            project.extensions.extraProperties[IS_ON_TARGET] = true
            VersionInferTaskOnTarget(vscTool)
        } else {
            project.logger.lifecycle("No version could be inferred on branch ${ciTool.currentBranchName()}." +
                " If you are not on ${extension.targetBranch.get()} branch. Set inferVersion to ALWAYS")
            null
        }
    }

    private fun setProjectVersion(project: Project, versionContext: VersionContext) {
        val version = versionContext.version
        project.version = version.artifactVersion()
        project.extensions.extraProperties[HAS_NEW_SEMANTIC_VERSION] = versionContext.hasNewVersion
        project.subprojects {
            it.version = project.version
        }
        project.logger.lifecycle("Project Version: ${project.version}")
    }

    private fun registerPublishReleaseNotesTask(
        project: Project,
        currentBranch: String,
        versionContext: VersionContext,
        factory: DefaultFactory,
        extension: SemanticReleasePluginExtension
    ) {
        project.tasks.register(SEMANTIC_PUBLISH, PublishSemanticReleaseGitHubTask::class.java) { publishReleaseNotes ->
            publishReleaseNotes.group = "Semantic Version"
            publishReleaseNotes.description = "Creates release in GitHub with release notes"
            publishReleaseNotes.version.set(versionContext)
            publishReleaseNotes.versionPublisher.set(factory.gitHubPublisher)
            publishReleaseNotes.changesGroups.set(extension.releaseChanges.get())
            publishReleaseNotes.currentBranch.set(currentBranch)
        }
    }

    companion object {
        private const val SEMANTIC_PUBLISH = "semanticReleasePublish"
        private const val SEMANTIC_RELEASE = "semanticRelease"
        internal const val HAS_NEW_SEMANTIC_VERSION = "semanticReleaseHasNewSemanticVersion"
        internal const val IS_ON_TARGET = "semanticReleaseIsOnTarget"
    }
}

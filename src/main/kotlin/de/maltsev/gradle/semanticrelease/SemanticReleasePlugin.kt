package de.maltsev.gradle.semanticrelease

import de.maltsev.gradle.semanticrelease.publish.PublishSemanticReleaseGitHubTask
import de.maltsev.gradle.semanticrelease.versions.VersionContext
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

        val extension = project.extensions.create(SEMANTIC_RELEASE, SemanticReleasePluginExtension::class.java, project.objects, env)

        val factory = DefaultFactory(env, extension)

        val ciTool = factory.travis

        val vscTool = factory.gitHub

        val onTargetBranch = ciTool.currentBranchName() == extension.targetBranch.get()
        val inferOnNotTargetBranch = extension.inferVersion.get() == VersionInference.ALWAYS

        val inferTask = if (!onTargetBranch && inferOnNotTargetBranch) {
            VersionInferTaskOnBranch(ciTool.currentBranchName(), vscTool)
        } else if (onTargetBranch) {
            VersionInferTaskOnTarget(vscTool)
        } else {
            project.logger.lifecycle("No version could be inferred on branch ${ciTool.currentBranchName()}. If you are not on ${extension.targetBranch.get()} branch. Set inferVersion to ALWAYS")
            null
        }

        val versionContext = inferTask?.getVersionContext()

        if (versionContext != null) {
            setProjectVersion(project, versionContext)
            if (versionContext.hasNewVersion) {
                registerPublishReleaseNotesTask(project, ciTool.currentBranchName(), versionContext, factory, extension)
            } else {
                project.logger.lifecycle("No new version")
            }
        }
    }

    private fun setProjectVersion(project: Project, versionContext: VersionContext) {
        val version = versionContext.version
        project.version = version.toString()
        project.subprojects {
            it.version = project.version
        }
        project.logger.lifecycle("Project Version: ${project.version}")
    }

    private fun registerPublishReleaseNotesTask(project: Project, currentBranch: String, versionContext: VersionContext, factory: DefaultFactory, extension: SemanticReleasePluginExtension) {
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
    }
}

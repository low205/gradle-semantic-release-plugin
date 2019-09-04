package de.maltsev.gradle.semanticrelease.publish

import de.maltsev.gradle.semanticrelease.lazy
import de.maltsev.gradle.semanticrelease.lazyList
import de.maltsev.gradle.semanticrelease.project.isOnTargetBranch
import de.maltsev.gradle.semanticrelease.releasenotes.releaseNotes
import de.maltsev.gradle.semanticrelease.versions.VersionChangeGroup
import de.maltsev.gradle.semanticrelease.versions.VersionContext
import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

open class PublishSemanticReleaseGitHubTask : DefaultTask() {

    @Input
    val versionPublisher: Property<VersionPublisher> = project.lazy()

    @Input
    val version: Property<VersionContext> = project.lazy()

    @Input
    val changesGroups: ListProperty<VersionChangeGroup> = project.lazyList()

    @Input
    val currentBranch: Property<String> = project.lazy()

    @TaskAction
    fun action() {
        val versionContext = version.get()
        if (versionContext.hasNewVersion && project.isOnTargetBranch()) {
            logger.lifecycle("Publishing version ${versionContext.version.vscString()}.")
            val releaseNotes = versionContext.releaseNotes(changesGroups.get().toSet())
            logger.lifecycle("Release notes:\n$releaseNotes.")
            versionPublisher.get().publishRelease(currentBranch.get(), versionContext.version.vscString(), releaseNotes)
        } else {
            logger.lifecycle("Nothing to publish.")
        }
    }
}

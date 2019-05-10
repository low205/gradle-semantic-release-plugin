package de.maltsev.gradle.semanticrelease

import arrow.core.Some
import arrow.core.getOrElse
import de.maltsev.gradle.semanticrelease.ci.CITool
import de.maltsev.gradle.semanticrelease.extensions.lazy
import de.maltsev.gradle.semanticrelease.releasenotes.MarkdownReleaseNotesGenerator
import de.maltsev.gradle.semanticrelease.vcs.VCSSource
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

open class SemanticReleasePublish : DefaultTask() {

    @Input
    val vcsSource: Property<VCSSource> = project.lazy()

    @Input
    val ciTool: Property<CITool> = project.lazy()

    @TaskAction
    fun action() {
        val releaseTask = project.tasks.named("release", SemanticReleaseVersion::class.java).get()
        val latestVersionContext = releaseTask.getLatestVersionContext()
        val nextVersion = releaseTask.getNextVersion()
        if (ciTool.get().isStage()) {
            return
        }

        when (latestVersionContext) {
            is Some -> when (nextVersion) {
                is Some -> {
                    logger.lifecycle("Generating release notes for ${nextVersion.t}.")
                    val changes = latestVersionContext.map { it.changes }.getOrElse { emptyList() }
                    val releaseNotes = MarkdownReleaseNotesGenerator.generate(nextVersion.t, changes)
                    logger.quiet("Release notes:\n$releaseNotes.")
                    logger.lifecycle("Publishing release for $releaseNotes.")
                    vcsSource.get().publishRelease(nextVersion.t, releaseNotes)
                }
            }
        }
    }
}

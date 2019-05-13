package de.maltsev.gradle.semanticrelease

import arrow.core.Option
import arrow.core.Some
import arrow.core.getOrElse
import de.maltsev.gradle.semanticrelease.SemanticReleasePlugin.Companion.SEMANTIC_VERSION
import de.maltsev.gradle.semanticrelease.ci.CITool
import de.maltsev.gradle.semanticrelease.extensions.lazy
import de.maltsev.gradle.semanticrelease.releasenotes.MarkdownReleaseNotesGenerator
import de.maltsev.gradle.semanticrelease.vcs.VCSSource
import de.maltsev.gradle.semanticrelease.versions.SemanticVersion
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
        val releaseTask = project.tasks.named(SEMANTIC_VERSION, SemanticReleaseVersion::class.java).get()
        val latestVersionContext = releaseTask.getLatestVersionContext()
        val nextVersion = releaseTask.getNextVersion()
        if (ciTool.get().isStage()) {
            return
        }

        when (latestVersionContext) {
            is Some -> when (nextVersion) {
                is Some -> {
                    publishRelease(nextVersion, latestVersionContext)
                }
            }
        }
    }

    private fun publishRelease(nextVersion: Some<SemanticVersion>, latestVersionContext: Option<VersionContext>) {
        logger.lifecycle("Generating release notes for ${nextVersion.t}.")
        val changes = latestVersionContext.map { it.changes }.getOrElse { emptyList() }
        val releaseNotes = MarkdownReleaseNotesGenerator.generate(nextVersion.t, changes)
        logger.quiet("Release notes:\n$releaseNotes.")
        vcsSource.get().publishRelease(nextVersion.t, releaseNotes)
    }
}

package de.maltsev.gradle.semanticrelease

import arrow.core.Option
import arrow.core.getOrElse
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
        val latestVersionContext: Option<VersionContext> = project.property("latestVersionContext") as Option<VersionContext>
        val nextVersion: SemanticVersion = project.property("semanticVersion") as SemanticVersion
        if (ciTool.get().isStage()) {
            return
        }
        logger.lifecycle("Publishing version $nextVersion.")
        publishRelease(nextVersion, latestVersionContext)
    }

    private fun publishRelease(nextVersion: SemanticVersion, latestVersionContext: Option<VersionContext>) {
        val changes = latestVersionContext.map { it.changes }.getOrElse { emptyList() }
        val releaseNotes = MarkdownReleaseNotesGenerator.generate(nextVersion, changes)
        logger.lifecycle("Release notes:\n$releaseNotes.")
        vcsSource.get().publishRelease(nextVersion, releaseNotes)
    }
}

package de.maltsev.gradle.semanticrelease.releasenotes

import de.maltsev.gradle.semanticrelease.versions.SemanticVersion
import de.maltsev.gradle.semanticrelease.versions.VersionChange
import java.time.LocalDate

object MarkdownReleaseNotesGenerator {
    fun generate(version: SemanticVersion, changes: List<VersionChange>): String {
        val releaseNotes = StringBuilder("## $version (${LocalDate.now()})")

        val changesText = changes
            .groupBy { it.name }
            .map { (changeName, commits) ->
                "#### $changeName \n\n${commits.joinToString("\n", transform = GenericMarkdownFormatter::asMarkdown)}"
            }.joinToString("\n\n")

        return if (changesText.isEmpty()) {
            releaseNotes.toString()
        } else {
            releaseNotes.append("\n\n").append(changesText).toString()
        }
    }
}

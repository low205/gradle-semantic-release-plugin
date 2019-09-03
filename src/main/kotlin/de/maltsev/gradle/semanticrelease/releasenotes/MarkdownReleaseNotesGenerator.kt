package de.maltsev.gradle.semanticrelease.releasenotes

import de.maltsev.gradle.semanticrelease.versions.VersionChangeGroup
import de.maltsev.gradle.semanticrelease.versions.VersionContext
import java.time.LocalDate

fun VersionContext.releaseNotes(includedGroups: Set<VersionChangeGroup>): String {
    val releaseNotes = StringBuilder("## $version (${LocalDate.now()})")
    val changesText = changes
        .groupBy { it.group }
        .toSortedMap(compareBy { it.priority })
        .filter { (group, _) ->
            group in includedGroups
        }
        .map { (changeGroup, commits) ->
            "#### ${changeGroup.groupName}\n\n${commits.joinToString("\n", transform = { it.asMarkdown() })}"
        }
        .joinToString("\n\n")
    return when {
        changesText.isEmpty() -> releaseNotes.toString()
        else -> releaseNotes.append("\n\n").append(changesText).toString()
    }
}

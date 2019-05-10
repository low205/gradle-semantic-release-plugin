package de.maltsev.gradle.semanticrelease.releasenotes

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import de.maltsev.gradle.semanticrelease.versions.VersionChange

object GenericMarkdownFormatter {
    fun asMarkdown(versionChange: VersionChange): String {
        val descriptor = versionChange.descriptor
        val subType = when (val optionalSubtype: Option<String> = descriptor.subType.map { "**$it:**" }) {
            is Some -> optionalSubtype.t
            is None -> ""
        }
        val breakingChangeMessage = when (val optionalBreakingMessage: Option<String> = descriptor.breakingChangeMessage.map { "\n\t BREAKING CHANGE: $it" }) {
            is Some -> optionalBreakingMessage.t
            is None -> ""
        }
        return "* $subType ${descriptor.message} (${versionChange.commitId})$breakingChangeMessage"
    }
}

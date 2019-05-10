package de.maltsev.gradle.semanticrelease.vcs

import arrow.core.Option
import arrow.core.getOrElse
import arrow.core.toOption
import java.util.regex.Pattern

object CommitMessageParser {
    private val changeMessagePattern = Pattern.compile("^(?<type>\\w*)(?<subType>\\((.*)\\))?: (?<message>.*)$").toRegex()
    private val breakingMessagePattern = Pattern.compile("BREAKING CHANGE(S?):?").toRegex()

    fun parse(commitMessage: String): CommitMessageDescriptor {
        val messageParts: List<String> = commitMessage.split(breakingMessagePattern).map { it.trim() }
        val changeMessage = messageParts[0]

        val matcher: Option<MatchGroupCollection> = changeMessagePattern.matchEntire(changeMessage).toOption().map { it.groups }
        val type: Option<String> = matcher.flatMap { it["type"].toOption() }.map { it.value }
        val subType: Option<String> = matcher.flatMap { it["subType"].toOption() }.map { it.value }
        val message: String = matcher.flatMap { it["message"].toOption() }.map { it.value }.getOrElse { "" }

        val breakingChangeMessage = Option.fromNullable(messageParts.getOrNull(1))

        return CommitMessageDescriptor(
            message = message,
            type = type,
            subType = subType,
            breakingChangeMessage = breakingChangeMessage
        )
    }
}

package de.maltsev.gradle.semanticrelease.versions

import arrow.core.Option
import arrow.core.getOrElse
import arrow.core.toOption
import java.util.regex.Pattern
import java.util.regex.Pattern.DOTALL

private val changeMessagePattern = Pattern.compile("^((?<type>\\w*)(\\((?<subType>.*)\\))?:)?(?<message>.*)$", DOTALL).toRegex()
private val breakingMessagePattern = Pattern.compile("BREAKING CHANGE(S?):?").toRegex()

fun String.asSemanticCommitMessage(): SemanticCommitMessage {
    val messageParts: List<String> = this.split(breakingMessagePattern).map { it.trim() }
    val changeMessage = messageParts[0]

    val matcher: Option<MatchGroupCollection> = changeMessagePattern.matchEntire(changeMessage).toOption().map { it.groups }
    val type: Option<String> = matcher.flatMap { it["type"].toOption() }.map { it.value.trim() }
    val subType: Option<String> = matcher.flatMap { it["subType"].toOption() }.map { it.value.trim() }
    val message: String = matcher.flatMap { it["message"].toOption() }.map { it.value.trim() }.getOrElse { "" }

    val breakingChangeMessage = Option.fromNullable(messageParts.getOrNull(1))

    return SemanticCommitMessage(
        message = message,
        type = type,
        subType = subType,
        breakingChangeMessage = breakingChangeMessage
    )
}

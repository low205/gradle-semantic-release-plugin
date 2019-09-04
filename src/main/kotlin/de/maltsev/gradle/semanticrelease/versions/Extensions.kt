package de.maltsev.gradle.semanticrelease.versions

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.getOrElse
import arrow.core.toOption
import java.util.regex.Pattern
import java.util.regex.Pattern.DOTALL

internal val changeMessagePattern = Pattern.compile("^((?<type>[^()]+)(\\((?<subType>.+)\\))?.*:)?(?<message>.*)$", DOTALL).toRegex()
internal val breakingMessagePattern = Pattern.compile("BREAKING CHANGE(S?):?").toRegex()

fun String.asSemanticCommitMessage(): SemanticCommitMessage {
    val messageParts: List<String> = this.split(breakingMessagePattern).map { it.trim() }
    val changeMessage = messageParts[0]

    val matcher: Option<MatchGroupCollection> = changeMessagePattern.matchEntire(changeMessage).toOption().map { it.groups }
    val type: Option<String> = matcher.flatMap { it["type"].toOption() }.map { it.value.trim() }
    val subType: Option<String> = matcher.flatMap { it["subType"].toOption() }.map { it.value.trim() }
    val message: String = matcher.flatMap { it["message"].toOption() }.map { it.value.trim() }.getOrElse { "" }

    val breakingChangeMessage = when {
        messageParts.size == 1 -> None
        else -> Some(messageParts.drop(1).map(String::trim).filter(String::isNotEmpty).joinToString("\n"))
    }

    return SemanticCommitMessage(
        message = message,
        type = type,
        subType = subType,
        breakingChangeMessage = breakingChangeMessage
    )
}

internal const val VERSION_PREFIX: String = "v"
internal const val PREFIX_GROUP_NAME: String = "prefix"
internal const val MAJOR_GROUP_NAME: String = "major"
internal const val MINOR_GROUP_NAME: String = "minor"
internal const val PATCH_GROUP_NAME: String = "patch"

private val versionPattern: Regex = """(?<$PREFIX_GROUP_NAME>\w*)(?<$MAJOR_GROUP_NAME>\d+)\.(?<$MINOR_GROUP_NAME>\d+)\.(?<$PATCH_GROUP_NAME>\d+)""".toRegex()

fun String.asSemanticVersion(): MasterSemanticVersion {
    val match = versionPattern.matchEntire(this)
    check(match != null) {
        "Version $this doesn't match semantic format v<major>.<minor>.<patch>. Please remove it from repository."
    }
    return with(match) {
        MasterSemanticVersion(
            prefix = VERSION_PREFIX,
            major = checkNotNull(groups[MAJOR_GROUP_NAME]).value.toInt(),
            minor = checkNotNull(groups[MINOR_GROUP_NAME]).value.toInt(),
            patch = checkNotNull(groups[PATCH_GROUP_NAME]).value.toInt()
        )
    }
}

val firstVersion = MasterSemanticVersion(
    prefix = VERSION_PREFIX,
    major = 0,
    minor = 1,
    patch = 0
)

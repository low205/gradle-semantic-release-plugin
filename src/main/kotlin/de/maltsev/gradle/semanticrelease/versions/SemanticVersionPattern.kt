package de.maltsev.gradle.semanticrelease.versions

import de.maltsev.gradle.semanticrelease.versions.SemanticVersionPattern.majorGroupName
import de.maltsev.gradle.semanticrelease.versions.SemanticVersionPattern.minorGroupName
import de.maltsev.gradle.semanticrelease.versions.SemanticVersionPattern.pathGroupName
import de.maltsev.gradle.semanticrelease.versions.SemanticVersionPattern.versionPattern
import de.maltsev.gradle.semanticrelease.versions.SemanticVersionPattern.versionPrefix

private object SemanticVersionPattern {
    internal const val versionPrefix: String = "v"
    private const val prefixGroupName: String = "prefix"
    internal const val majorGroupName: String = "major"
    internal const val minorGroupName: String = "minor"
    internal const val pathGroupName: String = "patch"

    internal val versionPattern: Regex = """(?<$prefixGroupName>\w*)(?<$majorGroupName>\d+)\.(?<$minorGroupName>\d+)\.(?<$pathGroupName>\d+)""".toRegex()
}

fun String.asSemanticVersion(): MasterSemanticVersion {
    return checkNotNull(versionPattern.matchEntire(this)) {
        "Version $this doesn't match semantic format v<major>.<minor>.<patch>. Please remove it from repository "
    }.run {
        MasterSemanticVersion(
            prefix = versionPrefix,
            major = checkNotNull(groups[majorGroupName]).value.toInt(),
            minor = checkNotNull(groups[minorGroupName]).value.toInt(),
            patch = checkNotNull(groups[pathGroupName]).value.toInt()
        )
    }
}

val firstVersion = MasterSemanticVersion(
    prefix = versionPrefix,
    major = 0,
    minor = 1,
    patch = 0
)

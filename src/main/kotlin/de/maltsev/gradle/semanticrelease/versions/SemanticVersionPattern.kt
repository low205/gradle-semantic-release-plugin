package de.maltsev.gradle.semanticrelease.versions

private const val versionPrefix: String = "v"
private const val prefixGroupName: String = "prefix"
private const val majorGroupName: String = "major"
private const val minorGroupName: String = "minor"
private const val pathGroupName: String = "patch"

private val versionPattern: Regex = """(?<$prefixGroupName>\w*)(?<$majorGroupName>\d+)\.(?<$minorGroupName>\d+)\.(?<$pathGroupName>\d+)""".toRegex()

fun String.asSemanticVersion(): MasterSemanticVersion {
    return checkNotNull(versionPattern.matchEntire(this)) { "Version $this doesn't match semantic format v<major>.<minor>.<patch>. Please remove it from repository " }
        .run {
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

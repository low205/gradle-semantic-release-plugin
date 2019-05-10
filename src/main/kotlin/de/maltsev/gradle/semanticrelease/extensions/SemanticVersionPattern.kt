package de.maltsev.gradle.semanticrelease.extensions

data class SemanticVersionPattern(
    var versionPrefix: String = "v",
    var prefixGroupName: String = "prefix",
    var majorGroupName: String = "major",
    var minorGroupName: String = "minor",
    var pathGroupName: String = "patch",
    var stageGroupName: String = "stage",
    var commitGroupName: String = "commit",
    var versionPattern: Regex = """(?<$prefixGroupName>\w*)(?<$majorGroupName>\d+)\.(?<$minorGroupName>\d+)\.(?<$pathGroupName>\d+)""".toRegex(),
    var stageVersionPattern: Regex = """$versionPattern-(?<$stageGroupName>.+)\.(?<$commitGroupName>\d+)""".toRegex()
)

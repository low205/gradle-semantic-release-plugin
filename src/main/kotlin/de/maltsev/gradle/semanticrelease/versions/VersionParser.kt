package de.maltsev.gradle.semanticrelease.versions

interface VersionParser {
    fun parse(version: String): SemanticVersion
}

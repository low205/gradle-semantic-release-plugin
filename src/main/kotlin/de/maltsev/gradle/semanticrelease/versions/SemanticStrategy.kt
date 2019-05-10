package de.maltsev.gradle.semanticrelease.versions

interface SemanticStrategy<T> {
    fun execute(source: T): VersionChange
}

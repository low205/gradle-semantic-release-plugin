package de.maltsev.gradle.semanticrelease.versions

interface VersionInferTask {
    fun getVersionContext(): VersionContext
}

package de.maltsev.gradle.semanticrelease.publish

interface VersionPublisher {
    fun publishRelease(branch: String, version: String, releaseNotes: String)
}

package de.maltsev.gradle.semanticrelease.publish.github

import de.maltsev.gradle.semanticrelease.publish.VersionPublisher
import de.maltsev.gradle.semanticrelease.vcs.github.GithubClient

class GitHubPublisher(
    private val client: GithubClient
) : VersionPublisher {
    override fun publishRelease(branch: String, version: String, releaseNotes: String) {
        client.publishRelease(branch, version, releaseNotes)
    }
}

package de.maltsev.gradle.semanticrelease.vcs.github

import com.fasterxml.jackson.annotation.JsonProperty

data class GitHubRelease(
    @JsonProperty("tag_name")
    val tagName: String,
    @JsonProperty("target_commitish")
    val branch: String,
    val name: String,
    val body: String,
    val draft: Boolean = false,
    val prerelease: Boolean = false
)

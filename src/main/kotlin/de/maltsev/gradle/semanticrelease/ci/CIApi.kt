package de.maltsev.gradle.semanticrelease.ci

interface CIApi {
    fun currentBranchName(): String
    fun repositorySlug(): String
}

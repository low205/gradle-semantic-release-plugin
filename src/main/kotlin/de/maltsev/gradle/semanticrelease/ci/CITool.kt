package de.maltsev.gradle.semanticrelease.ci

interface CITool {
    fun isMaster(): Boolean
    fun currentBranchName(): String
    fun isStage(): Boolean
    fun repositorySlug(): String
}

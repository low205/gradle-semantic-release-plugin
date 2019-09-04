package de.maltsev.gradle.semanticrelease.versions

import de.maltsev.gradle.semanticrelease.vcs.VCSSource

class VersionInferTaskOnBranch(
    private val branch: String,
    private val vscTool: VCSSource
) : VersionInferTask {
    override fun getVersionContext(): VersionContext {
        val lastCommit = vscTool.lastCommit()

        val version = BranchSemanticVersion(branch, lastCommit.id.shortId)

        return VersionContext(
            hasNewVersion = true,
            version = version,
            changes = emptyList()
        )
    }
}

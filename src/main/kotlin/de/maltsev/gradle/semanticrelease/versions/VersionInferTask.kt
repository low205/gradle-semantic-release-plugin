package de.maltsev.gradle.semanticrelease.versions

import arrow.core.None
import arrow.core.Some
import arrow.core.getOrElse
import de.maltsev.gradle.semanticrelease.vcs.VCSSource
import de.maltsev.gradle.semanticrelease.vcs.VcsCommit

interface VersionInferTask {
    fun getVersionContext(): VersionContext
}

class VersionInferTaskOnBranch(
    private val branch: String,
    private val vscTool: VCSSource
) : VersionInferTask {
    override fun getVersionContext(): VersionContext {
        val lastCommit = vscTool.lastCommit()

        val version = BranchSemanticVersion(branch, lastCommit.id.shortId)

        return VersionContext(
            hasNewVersion = false,
            version = version,
            changes = emptyList()
        )
    }
}

class VersionInferTaskOnTarget(
    private val vscTool: VCSSource
) : VersionInferTask {

    override fun getVersionContext(): VersionContext {
        val latestRelease = vscTool.latestRelease()
        val maybeLatestVersion = latestRelease.map { it.version.asSemanticVersion() }
        val changes = latestRelease.map { vscTool.commitsBefore(it.commitId).map(VcsCommit::asVersionChange) }.getOrElse { emptyList() }
        val maybeNextVersion = maybeLatestVersion.flatMap { version -> version.nextVersion(changes) }
        val version =
            when (maybeLatestVersion) {
                is None -> firstVersion
                is Some -> when (maybeNextVersion) {
                    is None -> maybeLatestVersion.t
                    is Some -> maybeNextVersion.t
                }
            }
        return VersionContext(
            hasNewVersion = maybeLatestVersion.isEmpty() || maybeNextVersion.isDefined(),
            version = version,
            changes = changes
        )
    }
}

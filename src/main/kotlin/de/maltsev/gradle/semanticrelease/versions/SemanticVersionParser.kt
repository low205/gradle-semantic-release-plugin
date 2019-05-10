package de.maltsev.gradle.semanticrelease.versions

import arrow.core.Some
import de.maltsev.gradle.semanticrelease.extensions.SemanticVersionPattern

class SemanticVersionParser(private val semanticPattern: SemanticVersionPattern) : VersionParser {
    override fun parse(version: String): SemanticVersion {
        return when (val stagedMatch = semanticPattern.stageVersionPattern.matchEntire(version)) {
            null -> {
                val semanticMatch = semanticPattern.versionPattern.matchEntire(version)
                    ?: throw IllegalStateException("Cannot match version to semantic pattern. Please remove release/tag $version from vcs")

                semanticVersion(semanticMatch)
            }
            else -> semanticVersionStaged(stagedMatch)
        }
    }

    private fun semanticVersionStaged(stagedMatch: MatchResult): SemanticVersion {
        return SemanticVersion(
            prefix = semanticPattern.versionPrefix,
            major = checkNotNull(stagedMatch.groups[semanticPattern.majorGroupName]).value.toInt(),
            minor = checkNotNull(stagedMatch.groups[semanticPattern.minorGroupName]).value.toInt(),
            patch = checkNotNull(stagedMatch.groups[semanticPattern.pathGroupName]).value.toInt(),
            stage = Some(
                SemanticVersion.Stage(
                    name = checkNotNull(stagedMatch.groups[semanticPattern.stageGroupName]).value,
                    commit = checkNotNull(stagedMatch.groups[semanticPattern.commitGroupName]).value.toInt()
                )
            )
        )
    }

    private fun semanticVersion(semanticMatch: MatchResult): SemanticVersion {
        return SemanticVersion(
            prefix = semanticPattern.versionPrefix,
            major = checkNotNull(semanticMatch.groups[semanticPattern.majorGroupName]).value.toInt(),
            minor = checkNotNull(semanticMatch.groups[semanticPattern.minorGroupName]).value.toInt(),
            patch = checkNotNull(semanticMatch.groups[semanticPattern.pathGroupName]).value.toInt()
        )
    }
}

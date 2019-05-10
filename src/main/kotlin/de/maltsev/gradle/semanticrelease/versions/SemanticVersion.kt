package de.maltsev.gradle.semanticrelease.versions

import arrow.core.None
import arrow.core.Option
import arrow.core.Some

data class SemanticVersion(
    val prefix: String,
    val major: Int,
    val minor: Int,
    val patch: Int,
    val stage: Option<Stage> = None
) {
    private fun nextMajor(): SemanticVersion = this.copy(major = major + 1)
    private fun nextMinor(): SemanticVersion = this.copy(minor = minor + 1)
    private fun nextPatch(): SemanticVersion = this.copy(patch = patch + 1)
    private fun nextStage(): SemanticVersion = this.copy(stage = stage.map { it.copy(commit = it.commit + 1) })

    fun nextVersion(changes: List<VersionChange>): SemanticVersion {
        return when {
            staged() -> nextStage()
            else -> {
                val semanticChanges = changes.filter { it is SemanticChange }
                when {
                    semanticChanges.any { it is MajorChange } -> nextMajor()
                    semanticChanges.any { it is MinorChange } -> nextMinor()
                    semanticChanges.any { it is PatchChange } -> nextPatch()
                    else -> this
                }
            }
        }
    }

    data class Stage(
        val name: String,
        val commit: Int
    )

    fun staged() = stage is Some

    override fun toString() = when (stage) {
        is Some -> "$prefix$major.$minor.$patch-${stage.t.name}.${stage.t.commit}"
        is None -> "$prefix$major.$minor.$patch"
    }

    fun withStageAt(stage: Stage): SemanticVersion {
        return this.copy(stage = Some(stage))
    }
}

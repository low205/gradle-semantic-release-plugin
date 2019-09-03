package de.maltsev.gradle.semanticrelease.versions

import arrow.core.None
import arrow.core.Option

data class SemanticCommitMessage(
    val message: String = "",
    val type: Option<String> = None,
    val subType: Option<String> = None,
    val breakingChangeMessage: Option<String> = None
)

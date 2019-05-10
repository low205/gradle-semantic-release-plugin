package de.maltsev.gradle.semanticrelease.vcs

import arrow.core.None
import arrow.core.Option

data class CommitMessageDescriptor(
    val message: String = "",
    val type: Option<String> = None,
    val subType: Option<String> = None,
    val breakingChangeMessage: Option<String> = None
)

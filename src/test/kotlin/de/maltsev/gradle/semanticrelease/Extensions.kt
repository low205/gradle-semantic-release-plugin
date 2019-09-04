package de.maltsev.gradle.semanticrelease

import de.maltsev.gradle.semanticrelease.vcs.VcsCommit
import de.maltsev.gradle.semanticrelease.vcs.VcsCommitId
import de.maltsev.gradle.semanticrelease.versions.SemanticCommitMessage
import de.maltsev.gradle.semanticrelease.versions.VersionChange
import de.maltsev.gradle.semanticrelease.versions.VersionChangeGroup
import org.apache.commons.lang3.RandomStringUtils

fun randomCommitId(): VcsCommitId {
    val id = RandomStringUtils.randomAlphanumeric(40)
    return VcsCommitId(
        id = id,
        shortId = id.take(7)
    )
}

fun randomMajorMessage(): String {
    val message = RandomStringUtils.randomAlphanumeric(20)
    val breakingChange = RandomStringUtils.randomAlphanumeric(20)
    return "feat: $message BREAKING CHANGE: $breakingChange"
}

fun randomCommitMessage(group: VersionChangeGroup): String {
    val message = RandomStringUtils.randomAlphanumeric(20)
    return "${group.type}: $message"
}

fun VersionChangeGroup.asCommit() = VcsCommit(randomCommitId(), randomCommitMessage(this))
fun VersionChangeGroup.asChange() = VersionChange(SemanticCommitMessage(), VcsCommitId("", ""), this)

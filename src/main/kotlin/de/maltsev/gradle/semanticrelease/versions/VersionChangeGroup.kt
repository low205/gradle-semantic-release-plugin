package de.maltsev.gradle.semanticrelease.versions

enum class VersionChangeGroup(val type: String, val groupName: String, val priority: Int) {
    MAJOR("Breaking Changes", "Breaking Changes", priority = 0),
    MINOR("feat", "Features", priority = 1),
    PATCH("fix", "Bug Fixes", priority = 2),
    PERFORMANCE("perf", "Performance Improvements", priority = 3),
    REVERT("revert", "Reverts", priority = 4),
    DOCS("docs", "Documentation", priority = 5),
    STYLE("style", "Style", priority = 6),
    REFACTOR("refactor", "Code Refactoring", priority = 7),
    TEST("test", "Tests", priority = 8),
    CHORE("chore", "Chores", priority = 9),
    OTHER("Others", "Others", priority = 99);

    companion object {
        fun of(type: String): VersionChangeGroup = values().find { it.type == type } ?: OTHER
    }
}

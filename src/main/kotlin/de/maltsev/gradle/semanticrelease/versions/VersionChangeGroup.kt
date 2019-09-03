package de.maltsev.gradle.semanticrelease.versions;

enum class VersionChangeGroup(val type: String, val groupName: String, val priority: Int) {
    MAJOR("Breaking Changes", "Breaking Changes", 0),
    MINOR("feat", "Features", 1),
    PATCH("fix", "Bug Fixes", 2),
    PERFORMANCE("perf", "Performance Improvements", 3),
    REVERT("revert", "Reverts", 4),
    DOCS("docs", "Documentation", 5),
    STYLE("style", "Style", 6),
    REFACTOR("refactor", "Code Refactoring", 7),
    TEST("test", "Tests", 8),
    CHORE("chore", "Chores", 9),
    OTHER("Others", "Others", 99);

    companion object {
        fun of(type: String): VersionChangeGroup = values().find { it.type == type } ?: OTHER
    }
}

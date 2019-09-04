package de.maltsev.gradle.semanticrelease

internal interface Environment {
    fun getEnv(name: String): String?
    fun hasTravis(): Boolean = this.getEnv("TRAVIS") == "true"
    fun hasGitHub(): Boolean = this.getEnv("GITHUB_TOKEN") != null
    val gitHubToken: String
        get() = checkNotNull(this.getEnv("GITHUB_TOKEN")) { "GITHUB_TOKEN not set" }
}

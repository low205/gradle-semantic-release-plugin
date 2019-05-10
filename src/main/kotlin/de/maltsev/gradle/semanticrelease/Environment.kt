package de.maltsev.gradle.semanticrelease

interface Environment {
    fun getEnv(name: String): String?
}

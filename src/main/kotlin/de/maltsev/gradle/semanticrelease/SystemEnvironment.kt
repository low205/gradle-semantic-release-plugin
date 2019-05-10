package de.maltsev.gradle.semanticrelease

class SystemEnvironment : Environment {
    override fun getEnv(name: String): String? {
        return System.getenv(name)
    }
}

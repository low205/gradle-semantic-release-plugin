package de.maltsev.gradle.semanticrelease

class MockEnvironment(private val env: Map<String, String> = emptyMap()) : Environment {
    override fun getEnv(name: String): String? {
        return env[name]
    }
}

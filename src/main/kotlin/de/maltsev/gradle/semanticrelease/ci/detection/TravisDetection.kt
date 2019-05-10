package de.maltsev.gradle.semanticrelease.ci.detection

import arrow.core.Option
import de.maltsev.gradle.semanticrelease.Environment
import de.maltsev.gradle.semanticrelease.SystemEnvironment
import de.maltsev.gradle.semanticrelease.ci.CITool
import de.maltsev.gradle.semanticrelease.ci.Travis

class TravisDetection(private val environment: Environment = SystemEnvironment()) : CIToolDetection {
    override fun detectCI(): Option<CITool> {
        return when {
            environment.getEnv("TRAVIS") == "true" -> Option.just(Travis())
            else -> super.detectCI()
        }
    }
}

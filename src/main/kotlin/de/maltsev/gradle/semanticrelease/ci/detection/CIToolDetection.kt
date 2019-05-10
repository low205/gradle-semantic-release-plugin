package de.maltsev.gradle.semanticrelease.ci.detection

import arrow.core.Option
import de.maltsev.gradle.semanticrelease.ci.CITool

interface CIToolDetection {
    fun detectCI(): Option<CITool> = Option.empty()
}

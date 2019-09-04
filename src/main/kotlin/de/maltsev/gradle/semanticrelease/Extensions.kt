package de.maltsev.gradle.semanticrelease

import de.maltsev.gradle.semanticrelease.SemanticReleasePlugin.Companion.HAS_NEW_SEMANTIC_VERSION
import de.maltsev.gradle.semanticrelease.SemanticReleasePlugin.Companion.IS_ON_TARGET
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

internal inline fun <reified T : Any> Project.lazyWith(default: T): Property<T> {
    return this.objects.lazyWith(default)
}

internal inline fun <reified T : Any> Project.lazy(): Property<T> {
    return this.objects.lazy()
}

internal inline fun <reified T : Any> Project.lazyList(): ListProperty<T> {
    return this.objects.lazyList()
}

internal inline fun <reified T : Any> ObjectFactory.lazyWith(default: T): Property<T> {
    return this.property(T::class.java).value(default)
}

internal inline fun <reified T : Any> ObjectFactory.lazyWith(default: List<T>): ListProperty<T> {
    return this.lazyList<T>().apply {
        addAll(default)
    }
}

internal inline fun <reified T : Any> ObjectFactory.lazyList(): ListProperty<T> {
    return this.listProperty(T::class.java)
}

internal inline fun <reified T : Any> ObjectFactory.lazy(): Property<T> {
    return this.property(T::class.java)
}

internal fun Environment.hasTravis(): Boolean = this.getEnv("TRAVIS") == "true"
internal fun Environment.hasGitHub(): Boolean = this.getEnv("GITHUB_TOKEN") != null
internal val Environment.gitHubToken: String
    get() = checkNotNull(this.getEnv("GITHUB_TOKEN")) { "GITHUB_TOKEN not set" }

fun Project.semanticRelease(configure: SemanticReleasePluginExtension.() -> Unit) =
    extensions.configure(SemanticReleasePluginExtension::class.java, configure)

fun Project.hasNewSemanticVersion() = this.version != "unspecified" && this.findProperty(HAS_NEW_SEMANTIC_VERSION) ?: false == true

fun Project.isOnTargetBranch() = this.findProperty(IS_ON_TARGET) ?: false == true

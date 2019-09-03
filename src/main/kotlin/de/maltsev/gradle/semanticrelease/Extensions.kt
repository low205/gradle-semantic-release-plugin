package de.maltsev.gradle.semanticrelease

import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

inline fun <reified T : Any> Project.lazyWith(default: T): Property<T> {
    return this.objects.lazyWith(default)
}

inline fun <reified T : Any> Project.lazy(): Property<T> {
    return this.objects.lazy()
}

inline fun <reified T : Any> Project.lazyList(): ListProperty<T> {
    return this.objects.lazyList()
}

inline fun <reified T : Any> ObjectFactory.lazyWith(default: T): Property<T> {
    return this.property(T::class.java).value(default)
}

inline fun <reified T : Any> ObjectFactory.lazyWith(default: List<T>): ListProperty<T> {
    return this.lazyList<T>().apply {
        addAll(default)
    }
}

inline fun <reified T : Any> ObjectFactory.lazyList(): ListProperty<T> {
    return this.listProperty(T::class.java)
}

inline fun <reified T : Any> ObjectFactory.lazy(): Property<T> {
    return this.property(T::class.java)
}

fun Environment.hasTravis(): Boolean = this.getEnv("TRAVIS") == "true"
fun Environment.hasGitHub(): Boolean = this.getEnv("GITHUB_TOKEN") != null
val Environment.gitHubToken: String
    get() = checkNotNull(this.getEnv("GITHUB_TOKEN")) { "GITHUB_TOKEN not set" }

package de.maltsev.gradle.semanticrelease.extensions

import org.gradle.api.Project
import org.gradle.api.provider.Property

inline fun <reified T : Any> Project.lazyWith(default: T): Property<T> {
    return this.objects.property(T::class.java).value(default)
}

inline fun <reified T : Any> Project.lazy(): Property<T> {
    return this.objects.property(T::class.java)
}

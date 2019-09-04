package de.maltsev.gradle.semanticrelease

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

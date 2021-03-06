package de.maltsev.gradle.semanticrelease

import de.maltsev.gradle.semanticrelease.versions.VersionChangeGroup
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

open class SemanticReleasePluginExtension(
    objectFactory: ObjectFactory
) {
    val targetBranch: Property<String> = objectFactory.lazyWith("master")
    val inferVersion: Property<VersionInference> = objectFactory.lazyWith(VersionInference.ONLY_ON_TARGET)
    val releaseChanges: ListProperty<VersionChangeGroup> = objectFactory.lazyWith(VersionChangeGroup.values().asList())
}

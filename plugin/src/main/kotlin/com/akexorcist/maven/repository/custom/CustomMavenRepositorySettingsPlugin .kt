package com.akexorcist.maven.repository.custom

import org.gradle.api.Plugin
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.getByType
import pt.davidafsilva.apple.OSXKeychain
import kotlin.jvm.optionals.getOrNull

@Suppress("unused")
class CustomMavenRepositorySettingsPlugin : Plugin<Settings> {
    override fun apply(target: Settings) {
        with(target) {
            extensions.create("customMavenRepository", CustomMavenRepositoryExtension::class.java)
            gradle.settingsEvaluated {
                dependencyResolutionManagement {
                    repositories {
                        customMaven(target)
                    }
                }
            }
        }
    }
}

fun RepositoryHandler.customMaven(
    target: Settings,
    name: String,
    extension: CustomMavenRepositoryExtension,
): MavenArtifactRepository = maven {
    val (username, password) = target.getCredential(extension)
    setName(name)
    setUrl(extension.repositoryUrl)
    authentication(extension.authentication)
    credentials { extension.credentials(this, username, password) }
}

fun RepositoryHandler.customMaven(target: Settings, name: String = "CustomMaven") {
    val extension: CustomMavenRepositoryExtension = target.extensions.getByType()
    customMaven(
        target = target,
        name = name,
        extension = extension,
    )
}

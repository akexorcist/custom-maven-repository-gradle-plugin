package com.akexorcist.maven.repository.custom

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.repositories

@Suppress("unused")
class CustomMavenRepositoryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            extensions.create("customMavenRepository", CustomMavenRepositoryExtension::class.java)
            afterEvaluate {
                repositories {
                    mavenLocal()
                    customMaven(target)
                }
            }
        }
    }
}

fun RepositoryHandler.customMaven(
    target: Project,
    name: String,
    extension: CustomMavenRepositoryExtension,
): MavenArtifactRepository = maven {
    val (username, password) = target.getCredential(extension)
    setName(name)
    setUrl(extension.repositoryUrl)
    authentication(extension.authentication)
    credentials { extension.credentials(this, username, password) }
}

fun RepositoryHandler.customMaven(target: Project, name: String = "CustomMaven") {
    val extension: CustomMavenRepositoryExtension = target.extensions.getByType()
    customMaven(
        target = target,
        name = name,
        extension = extension,
    )
}

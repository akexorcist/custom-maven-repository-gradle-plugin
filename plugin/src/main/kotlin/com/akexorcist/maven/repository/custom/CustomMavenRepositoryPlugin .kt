package com.akexorcist.maven.repository.custom

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.repositories
import pt.davidafsilva.apple.OSXKeychain
import kotlin.jvm.optionals.getOrNull

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


private fun Project.getCredential(extension: CustomMavenRepositoryExtension): Pair<String?, String?> {
    val macOsKeyChain = extension.macOsKeyChain
    return getProjectVariable(extension.usernameKey) to when {
        macOsKeyChain != null && isMacOs() && !isGitLabRunner() -> getPasswordFromKeychain(macOsKeyChain)
        else -> getProjectVariable(extension.passwordKey)
    }
}

private fun isMacOs() = System.getProperty("os.name").lowercase().contains("mac")
private fun isGitLabRunner() = System.getProperty("GITLAB_CI") != null

private fun getPasswordFromKeychain(keyChain: CustomMavenRepositoryExtension.MacOsKeyChain): String? {
    return runCatching {
        val chain = OSXKeychain.getInstance()
        chain.findGenericPassword(keyChain.key, keyChain.accountName).getOrNull()
    }.getOrNull()
}

fun Project.getProjectVariable(key: String) = System.getenv(key)
    ?.takeIf { it.isNotBlank() } ?: properties[key]?.toString().takeIf { !it.isNullOrBlank() }

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

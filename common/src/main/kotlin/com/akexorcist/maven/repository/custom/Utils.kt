package com.akexorcist.maven.repository.custom

import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import pt.davidafsilva.apple.OSXKeychain
import kotlin.jvm.optionals.getOrNull

internal fun isMacOs() = System.getProperty("os.name").lowercase().contains("mac")
internal fun isGitLabRunner() = System.getProperty("GITLAB_CI") != null

internal fun getPasswordFromKeychain(keyChain: CustomMavenRepositoryExtension.MacOsKeyChain): String? {
    return runCatching {
        val chain = OSXKeychain.getInstance()
        chain.findGenericPassword(keyChain.key, keyChain.accountName).getOrNull()
    }.getOrNull()
}

fun Settings.getCredential(extension: CustomMavenRepositoryExtension): Pair<String?, String?> {
    val macOsKeyChain = extension.macOsKeyChain
    return getSettingsVariable(extension.usernameKey) to when {
        macOsKeyChain != null && isMacOs() && !isGitLabRunner() -> getPasswordFromKeychain(macOsKeyChain)
        else -> getSettingsVariable(extension.passwordKey)
    }
}

fun Settings.getSettingsVariable(key: String) = System.getenv(key)
    ?.takeIf { it.isNotBlank() } ?: providers.gradleProperty(key).get()

fun Project.getCredential(extension: CustomMavenRepositoryExtension): Pair<String?, String?> {
    val macOsKeyChain = extension.macOsKeyChain
    return getProjectVariable(extension.usernameKey) to when {
        macOsKeyChain != null && isMacOs() && !isGitLabRunner() -> getPasswordFromKeychain(macOsKeyChain)
        else -> getProjectVariable(extension.passwordKey)
    }
}

fun Project.getProjectVariable(key: String) = System.getenv(key)
    ?.takeIf { it.isNotBlank() } ?: properties[key]?.toString().takeIf { !it.isNullOrBlank() }

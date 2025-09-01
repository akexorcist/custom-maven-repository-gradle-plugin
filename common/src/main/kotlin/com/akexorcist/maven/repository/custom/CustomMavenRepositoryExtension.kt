package com.akexorcist.maven.repository.custom

import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.AuthenticationContainer
import org.gradle.api.credentials.PasswordCredentials
import org.gradle.authentication.http.BasicAuthentication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create

open class CustomMavenRepositoryExtension {
    lateinit var usernameKey: String
    lateinit var passwordKey: String
    var macOsKeyChain: MacOsKeyChain? = null

    lateinit var repositoryUrl: String
    var authentication: AuthenticationContainer.() -> Unit = { create<BasicAuthentication>("basic") }
    var credentials: PasswordCredentials.(username: String?, password: String?) -> Unit = { username, password ->
        setUsername(username)
        setPassword(password)
    }

    data class MacOsKeyChain(
        val key: String,
        val accountName: String,
    )
}

@Suppress("unused")
fun Project.customMavenRepository(block: CustomMavenRepositoryExtension.() -> Unit) {
    extensions.configure<CustomMavenRepositoryExtension> {
        block()
    }
}

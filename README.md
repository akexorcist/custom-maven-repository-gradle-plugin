# Custom Maven Repository Gradle Plugin
Gradle plugin for custom Maven repository configuration that supports macOS Keychain 

## Installation

### Version Catalog

```toml
# gradle/libs.versions.toml
custom-maven-repository = { id = "com.akexorcist.maven.repository.custom", version = "1.0.0" }
```

### Plugin declaration

There are two ways to declare the plugin in your Gradle project: using the build script DSL or the settings script DSL.

#### Build script DSL

```kotlin
// Top-level build.gradle.kts
plugins {
    id("com.akexorcist.maven.repository.custom") version "1.0.0"
}
```

#### Settings script DSL

```kotlin
// Top-level settings.gradle.kts
plugins {
    id("com.akexorcist.maven.repository.custom.settings") version "1.0.0"
}

extensions.configure<com.akexorcist.maven.repository.custom.CustomMavenRepositoryExtension> { // configure the custom Maven repository extension used by the plugin
    repositoryUrl = "Your Maven repository URL"
    usernameKey = "Variable key for Maven repository username"
    passwordKey = "Variable key for Maven repository password"
    macOsKeyChain = CustomMavenRepositoryExtension.MacOsKeyChain(
        accountName = "macOS Keychain's account name",
        key = "macOS Keychain's key",
    )
}

dependencyResolutionManagement { // optional
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS) // Someone may want to use `RepositoriesMode.PREFER_SETTINGS` instead
}
```

### Dependency declaration

```kotlin
// buildSrc/build.gradle.kts
implementation("com.akexorcist.maven.repository.custom:com.akexorcist.maven.repository.custom.gradle.plugin:1.0.0")
```

## Usage

### Apply custom Maven repository configuration for all modules

```kotlin
// Top-level build.gradle.kts
plugins {
    id("com.akexorcist.gradle.plugin.publishing")
}

customMavenRepository {
    repositoryUrl = "Your Maven repository URL"
    usernameKey = "Variable key for Maven repository username"
    passwordKey = "Variable key for Maven repository password"
    macOsKeyChain = CustomMavenRepositoryExtension.MacOsKeyChain(
        accountName = "macOS Keychain's account name",
        key = "macOS Keychain's key",
    )
}

allprojects {
    repositories {
        customMaven(project.rootProject)
    }
}
```

### Apply custom Maven repository configuration for library publishing
```kotlin
plugins {
    /* ... */
    `maven-publish`
    // or id("com.akexorcist.gradle.plugin.publishing")
}

publishing {
    /* ... */
    repositories {
        customMaven(
            target = project,
            name = "Your custom Maven repository name",
            extension = CustomMavenRepositoryExtension().apply {
                repositoryUrl = "Your custom Maven repository URL"
                usernameKey = "Variable key for Maven repository username"
                passwordKey = "Variable key for Maven repository password"
                macOsKeyChain = CustomMavenRepositoryExtension.MacOsKeyChain(
                    accountName = "macOS Keychain's account name",
                    key = "macOS Keychain's key",
                )
            }
        )
    }
}
```

## License

Licensed under the Apache License, Version 2.0. Copyright (c) 2025 Akexorcist
See [License](./LICENSE)

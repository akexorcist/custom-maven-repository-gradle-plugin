import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    id("com.vanniktech.maven.publish") version "0.34.0"
}

repositories {
    mavenLocal()
    gradlePluginPortal()
    google()
    mavenCentral()
}

val libraryGroup = "com.akexorcist.maven.repository.custom"
val libraryArtifact = "com.akexorcist.maven.repository.custom.gradle.plugin"
val libraryVersion = properties["library.version"].toString()

group = libraryGroup
version = libraryVersion

val buildVariant: String
    get() = project.findProperty("buildVariant") as? String ?: "debug"
val debugImplementation: Configuration by configurations.creating
val releaseImplementation: Configuration by configurations.creating

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    if (buildVariant == "debug") {
        configurations.implementation.get().extendsFrom(debugImplementation)
    } else {
        configurations.implementation.get().extendsFrom(releaseImplementation)
    }

    implementation(libs.dokka)
    implementation(files("../buildSrc/build/classes/kotlin/main"))

    debugImplementation(project(":common"))
    releaseImplementation("com.akexorcist.maven.repository.custom:common:$libraryVersion")
}

gradlePlugin {
    plugins {
        register("customMavenRepositoryPlugin") {
            id = "com.akexorcist.maven.repository.custom"
            implementationClass = "com.akexorcist.maven.repository.custom.CustomMavenRepositoryPlugin"
        }
    }
}

mavenPublishing {
    publishToMavenCentral()
    coordinates(
        groupId = libraryGroup,
        artifactId = libraryArtifact,
        version = libraryVersion,
    )
    signAllPublications()

    pom {
        name.set("Custom Maven Repository Gradle Plugin")
        description.set("Custom Maven Repository Configuration")
        inceptionYear.set("2025")
        url.set("https://github.com/akexorcist/custom-maven-repository-gradle-plugin")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("akexorcist")
                name.set("Akexorcist")
                email.set("akexorcist@gmail.com")
            }
        }
        scm {
            url.set("https://github.com/akexorcist/custom-maven-repository-gradle-plugin")
            connection.set("scm:git:git://github.com/akexorcist/custom-maven-repository-gradle-plugin.git")
            developerConnection.set("scm:git:ssh://git@github.com/akexorcist/custom-maven-repository-gradle-plugin.git")
        }
    }
}
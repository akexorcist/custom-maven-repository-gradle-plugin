import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    id("com.vanniktech.maven.publish") version "0.31.0"
}

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
}

val libraryGroup = properties["library.group"].toString()
val libraryArtifact = properties["library.artifact"].toString()
val libraryVersion = properties["library.version"].toString()

group = libraryGroup
version = libraryVersion

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
    }
}

dependencies {
    implementation(libs.osx.keychain)
    implementation(libs.dokka)
    implementation(files("../buildSrc/build/classes/kotlin/main"))
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
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
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

fun Project.getProjectVariable(key: String) = System.getenv(key)
    ?.takeIf { it.isNotBlank() } ?: properties[key]?.toString().takeIf { !it.isNullOrBlank() }
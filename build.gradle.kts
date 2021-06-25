/*
 *
 * Treexl (Tree extensible expression language).
 * Copyright Ted Colvin (tedcolvin@outlook.com).
 *
 * Licensed under Apache License 2.0
 * (http://www.apache.org/licenses/LICENSE-2.0).
 *
 * See the LICENSE file distributed with this work for
 *  additional information regarding copyright ownership.
 *
 */
import java.util.Properties

plugins {
    kotlin("multiplatform") version "1.4.31"
    id("maven-publish")
    id("org.jetbrains.dokka") version "1.4.20"
    signing
}

group = "org.treexl"
version = "0.4"

repositories {
    mavenCentral()
    jcenter()
}

signing {
    useGpgCmd()
    sign(configurations.archives.get())
}

val dokkaHtml by tasks.getting(org.jetbrains.dokka.gradle.DokkaTask::class)

val javadocJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
    dependsOn(dokkaHtml)
    archiveClassifier.set("javadoc")
    from(dokkaHtml.outputDirectory)
}

project.file("local.properties").takeIf { it.exists() }?.inputStream()?.use {
    Properties().apply { load(it) }
}?.forEach { (k, v) ->
    project.setProperty(k.toString(), v.toString())
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
    }
    js(LEGACY) {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = true
                }
            }
        }
    }
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    
    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
        val jsMain by getting
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
        val nativeMain by getting
        val nativeTest by getting
    }
}

tasks.withType<Sign> {
    //sign only if keyName is set
    onlyIf { !(project.property("signing.gnupg.keyName") as String?).isNullOrEmpty() }
}

publishing {
    publications.withType<MavenPublication> {
        artifact(javadocJar)
        pom {
            val projectGitUrl = "https://github.com/tedcolvin/treexl"
            name.set(rootProject.name)
            description.set("Treexl - Tree Extensible EXpression Language")
            url.set(projectGitUrl)
            inceptionYear.set("2021")
            licenses {
                license {
                    name.set("Apache 2.0")
                    url.set("http://www.apache.org/licenses/")
                }
            }
            developers {
                developer {
                    id.set("tedcolvin")
                    name.set("Ted Colvin")
                    email.set("tedcolvin@outlook.com")
                    url.set("https://github.io/tedcolvin")
                }
            }
            issueManagement {
                system.set("GitHub")
                url.set("$projectGitUrl/issues")
            }
            scm {
                connection.set("scm:git:$projectGitUrl")
                developerConnection.set("scm:git:$projectGitUrl")
                url.set(projectGitUrl)
            }
        }
        the<SigningExtension>().sign(this)
    }
    repositories {
        maven {
            name = "sonatypeStaging"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2")
            credentials {
                username = project.property("ossrhUsername") as? String ?: error("Unknown user")
                password = project.property("ossrhPassword") as? String ?: error("Unknown password")
            }
        }
    }
}

plugins {
    kotlin("jvm") version "1.5.31"
    id("maven-publish")
    id("org.jetbrains.dokka") version "1.4.32"
}

group = "de.eternalwings.bukkit"
version = "1.0.0"
val gitUrl = "https://github.com/kumpelblase2/konversation"
val repoUrl = "https://repo.eternalwings.de/" + if (project.version.toString().endsWith("-SNAPSHOT")) "snapshots" else "releases"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.spigotmc:spigot-api:1.17.1-R0.1-SNAPSHOT")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.3.1")
    testImplementation("org.mockito.kotlin:mockito-kotlin:3.2.0")
}

val sources by tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

val javadocJar by tasks.register<Jar>("javadocJar") {
    dependsOn(tasks.dokkaJavadoc)
    archiveClassifier.set("javadoc")
    from(tasks.dokkaJavadoc.flatMap { it.outputDirectory })
}

val reposiliteUser: String by project
val reposiliteToken: String by project

publishing {
    publications {
        create<MavenPublication>("library") {
            from(components["java"])
            artifact(javadocJar)
            artifact(sources)
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            pom {
                description.set("DSL for creating a chain of prompts of the conversations API of Bukkit.")
                url.set(gitUrl)
                licenses {
                    license {
                        name.set("MIT")
                        url.set("$gitUrl/blob/master/LICENSE")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        id.set("kumpelblase2")
                        name.set("Tim Hagemann")
                        email.set("tim+github@eternalwings.de")
                    }
                }
                scm {
                    url.set(gitUrl)
                }
            }
        }
    }
    repositories {
        maven {
            url = uri(repoUrl)
            credentials {
                username = reposiliteUser
                password = reposiliteToken
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
}

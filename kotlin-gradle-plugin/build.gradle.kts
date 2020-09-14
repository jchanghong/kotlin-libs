/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Gradle plugin project to get you started.
 * For more details take a look at the Writing Custom Plugins chapter in the Gradle
 * User Manual available at https://docs.gradle.org/6.6.1/userguide/custom_plugins.html
 */

plugins {
    // Apply the Java Gradle plugin development plugin to add support for developing Gradle plugins
    `java-gradle-plugin`

    kotlin("jvm")
    `java-library`
    signing
    `maven-publish`
    id("name.remal.maven-publish-nexus-staging") version "1.0.211"
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            groupId="com.github.jchanghong"
            version="1.0"
            artifactId="testplugin"
            pom {
                name.set("kotlin-lib")
                description.set("kotlin java tools")
                url.set("http://www.example.com/library")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        email.set("3200601e@qq.com")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/jchanghong/utils.git")
                    developerConnection.set("scm:git:git@github.com:jchanghong/utils.git")
                    url.set("git@github.com:jchanghong/utils.git")
                }
            }
        }
    }
    repositories {
        maven {
            name="sona"
            val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
            val snapshotsRepoUrl = uri("$buildDir/repos/snapshots")
//            val releasesRepoUrl = uri("$buildDir/repos/releases")

            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            if (url.toString().startsWith("http")) {
                this.isAllowInsecureProtocol=true
                this.credentials {
                    this.username="jchanghong"
                    this.password="!b58r5gsHu*0"
                }
            }
        }
    }
}
signing {

    sign(publishing.publications["maven"])
}
java{
    withJavadocJar()
    withSourcesJar()
}
tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}
tasks.named("releaseNexusRepositories"){
    this.dependsOn("publish")
}
dependencies {
    // Align versions of all Kotlin components
//    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin")

    // Use the Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

gradlePlugin {
    // Define the plugin
    val testplugin by plugins.creating {
        id = "com.github.jchanghong.testplugin"
        version="1.0"
        implementationClass = "com.github.jchanghong.KotlinGradlePluginPlugin"
    }
}

// Add a source set for the functional test suite
val functionalTestSourceSet = sourceSets.create("functionalTest") {
}

gradlePlugin.testSourceSets(functionalTestSourceSet)
configurations.getByName("functionalTestImplementation").extendsFrom(configurations.getByName("testImplementation"))

// Add a task to run the functional tests
val functionalTest by tasks.registering(Test::class) {
    testClassesDirs = functionalTestSourceSet.output.classesDirs
    classpath = functionalTestSourceSet.runtimeClasspath
}

val check by tasks.getting(Task::class) {
    // Run the functional tests as part of `check`
    dependsOn(functionalTest)
}

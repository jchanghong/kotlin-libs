/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Kotlin library project to get you started.
 */
import name.remal.gradle_plugins.dsl.extensions.*
plugins {
    id("com.github.jchanghong.testplugin") apply true

//    id("org.springframework.boot")  apply false
//    kotlin("jvm")
//    kotlin("plugin.jpa")
//    kotlin("plugin.spring")
//    id("org.jetbrains.dokka")
//    `java-library`
    signing
    `maven-publish`
//    id("name.remal.maven-publish-nexus-staging")
//    id("io.spring.dependency-management")
    `java-library`

}
//dependencyManagement {
//    imports {
//        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
//    }
//}
configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.jetbrains.kotlin") {
            useVersion("1.4.10")
        }
//        if (requested.group == "org.jetbrains.kotlin") {
//            useVersion("4.9.0")
//        }
    }
}
//publishing {
//    publications {
//        create<MavenPublication>("maven") {
//            from(components["java"])
////            groupId="com.github.jchanghong"
////            version="2.3.3.2"
////            artifactId="kotlin-lib"
//            pom {
//                name.set("kotlin-lib")
//                description.set("kotlin java tools")
//                url.set("http://www.example.com/library")
//                licenses {
//                    license {
//                        name.set("The Apache License, Version 2.0")
//                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
//                    }
//                }
//                developers {
//                    developer {
//                        email.set("3200601e@qq.com")
//                    }
//                }
//                scm {
//                    connection.set("scm:git:https://github.com/jchanghong/utils.git")
//                    developerConnection.set("scm:git:git@github.com:jchanghong/utils.git")
//                    url.set("git@github.com:jchanghong/utils.git")
//                }
//            }
//        }
//    }
//    repositories {
//        maven {
//            name="sona"
//			val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
//            val snapshotsRepoUrl = uri("$buildDir/repos/snapshots")
////            val releasesRepoUrl = uri("$buildDir/repos/releases")
//
//            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
//            if (url.toString().startsWith("http")) {
//                this.isAllowInsecureProtocol=true
//                this.credentials {
//                    this.username="jchanghong"
//                    this.password="!b58r5gsHu*0"
//                }
//            }
//        }
//    }
//}
//signing {
//
//    sign(publishing.publications["maven"])
//}
dependencies {

}
kotlin{
    sourceSets {
        val test by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
        val main by getting {
            dependencies {
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.3.9")
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")

                api("io.springfox:springfox-swagger2:2.9.2")
                api("io.springfox:springfox-swagger-ui:2.9.2")
                api("com.squareup.okhttp3:okhttp:4.9.0")
                api("com.baomidou:mybatis-plus-boot-starter:3.4.0")
                api("com.github.liaochong:myexcel:3.9.4")
                api("cn.hutool:hutool-all:5.4.1")
                api("com.oracle.ojdbc:ojdbc8")
                api("com.oracle.ojdbc:orai18n")
                api("mysql:mysql-connector-java")
                api("org.mariadb.jdbc:mariadb-java-client")
                api("org.postgresql:postgresql")
                implementation("org.springframework.boot:spring-boot-starter-data-redis")
                implementation("org.springframework.boot:spring-boot-starter-web")
                api("com.fasterxml.jackson.module:jackson-module-kotlin")
                api("com.fasterxml.jackson.module:jackson-module-parameter-names")
                api("com.fasterxml.jackson.datatype:jackson-datatype-jdk8")
                api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
                implementation("org.springframework.kafka:spring-kafka")
            }
        }
    }
}

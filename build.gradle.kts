/*
 * This file was generated by the Gradle 'init' task.
 *
 * This is a general purpose Gradle build.
 * Learn how to create Gradle builds at https://guides.gradle.org/creating-new-gradle-builds
 */
plugins {
    id("com.github.jchanghong.jchtest") apply false
//    kotlin("jvm")  apply false
//    kotlin("plugin.spring")  apply false
//    kotlin("plugin.jpa")  apply false
//    id("org.jetbrains.dokka")  apply false
//    id("org.springframework.boot")  apply false
//    id("io.spring.dependency-management")  apply false
}

allprojects {
    group="com.github.jchanghong"
    version="2.3.3.4"
}
//allprojects {
//    repositories {
//        mavenLocal()
//        maven("http://maven.aliyun.com/nexus/content/groups/public")
//        jcenter()
//        maven("http://af.hikvision.com.cn:80/artifactory/maven-down/")
//        // Use jcenter for resolving dependencies.
//        // You can declare any Maven/Ivy/file repository here.
//    }
//    tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java).configureEach {
////        kotlinOptions.suppressWarnings=false
//        kotlinOptions.jvmTarget="1.8"
////        kotlinOptions.verbose=true
////        kotlinOptions.javaParameters=true
////        kotlinOptions.useIR=true
//        kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict")
//    }
//}
/*
 * This file was generated by the Gradle 'init' task.
 *
 * The settings file is used to specify which projects to include in your build.
 *
 * Detailed information about configuring a multi-project build in Gradle can be found
 * in the user manual at https://docs.gradle.org/6.6.1/userguide/multi_project_builds.html
 */

//rootProject.name = "kotlin-lib"
//include("kotlin-lib")
//include("kotlin-app")
//include("kotlin-gradle-plugin")
pluginManagement {
    repositories {
        mavenLocal()
        maven("http://maven.aliyun.com/nexus/content/groups/public")
        // Use jcenter for resolving dependencies.
        // You can declare any Maven/Ivy/file repository here.
        gradlePluginPortal()
        jcenter()
    }
}

buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.0")
    }
}

plugins {
    //trick: for the same plugin versions in all sub-modules
    id("com.android.application").version("8.0.2").apply(false)
    id("com.android.library").version("8.0.2").apply(false)
    kotlin("android").version("1.8.21").apply(false)
    kotlin("multiplatform").version("1.8.21").apply(false)

    id("io.realm.kotlin").version("1.10.1").apply(false)
    id("com.google.firebase.crashlytics") version "2.9.6" apply false
    id("com.google.gms.google-services") version "4.3.15" apply false
    kotlin("plugin.serialization").version("1.8.21")
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

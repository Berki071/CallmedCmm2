buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.1")
    }
}

plugins {
    //trick: for the same plugin versions in all sub-modules
    id("com.android.application").version("8.1.1").apply(false)
    id("com.android.library").version("8.1.1").apply(false)
    kotlin("android").version("1.9.10").apply(false)
    kotlin("multiplatform").version("1.9.10").apply(false)

    id("io.realm.kotlin").version("1.11.0").apply(false)
    id("com.google.firebase.crashlytics") version "2.9.9" apply false
    id("com.google.gms.google-services") version "4.3.15" apply false
    kotlin("plugin.serialization").version("1.9.10")
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

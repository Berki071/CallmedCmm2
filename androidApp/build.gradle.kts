plugins {
    id("com.android.application")
    kotlin("android")
    
    id("kotlinx-serialization")
    id("io.realm.kotlin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.medhelp.callmed2"
    compileSdk = 33
    defaultConfig {
        applicationId = "com.medhelp.callmed2"
        minSdk = 24
        targetSdk = 33
        versionCode = 93
        versionName = "93"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(":shared"))

    compileOnly("io.realm.kotlin:library-base:1.10.1")

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.media:media:1.6.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.test:monitor:1.6.1")

    implementation("androidx.vectordrawable:vectordrawable-animated:1.1.0")
    // implementation("androidx.exifinterface:exifinterface:1.3.6")

    implementation("com.squareup.picasso:picasso:2.71828")

    //    Network
    implementation("com.amitshekhar.android:rx2-android-networking:1.0.2")
    implementation("com.google.code.gson:gson:2.10.1")

    //    RxJava
    implementation("io.reactivex.rxjava2:rxjava:2.2.21")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation("io.reactivex.rxjava2:rxkotlin:2.4.0")

    //    Android
    implementation("androidx.recyclerview:recyclerview:1.3.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.vectordrawable:vectordrawable:1.1.0")

    //    Logger
    implementation("com.jakewharton.timber:timber:5.0.1")

    implementation("com.github.aykuttasil:CallRecorder:1.2.9")

    //    Crash Reporting
    // Add the Firebase Crashlytics SDK.
    implementation (platform("com.google.firebase:firebase-bom:32.1.1"))
    implementation ("com.google.firebase:firebase-crashlytics-ktx")
    implementation ("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.android.gms:play-services-vision:20.1.3")

    //изображения с зумом
    implementation("com.davemorrissey.labs:subsampling-scale-image-view:3.10.0")
    //круглое изображение
    implementation("de.hdodenhof:circleimageview:3.1.0")

    implementation("com.prolificinteractive:material-calendarview:1.4.3")
    implementation("io.github.luizgrp.sectionedrecyclerviewadapter:sectionedrecyclerviewadapter:1.1.3")

    //запуск фоновых процессов
    implementation("androidx.work:work-runtime:2.8.1")

    implementation("ru.egslava:MaskedEditText:1.0.5")

    implementation("com.github.sundeepk:compact-calendar-view:3.0.0")

    implementation("com.thoughtbot:expandablerecyclerview:1.4")

    //всплывающие подсказки
    implementation("it.sephiroth.android.library.targettooltip:target-tooltip-library:2.0.3")

    implementation("com.google.android.play:core:1.10.3")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")

    implementation("io.realm.kotlin:library-base:1.10.1")

    implementation("com.github.barteksc:android-pdf-viewer:3.2.0-beta.1")
}
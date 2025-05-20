val kotlin_version = "1.9.10" // Define la versión de Kotlin directamente aquí

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("kapt")
}

android {
    namespace = "com.example.profit"
    compileSdk = 35 // ⚠ Usa uno solo, no repitas compileSdk

    packaging {
        resources {
            excludes += setOf(
                "META-INF/INDEX.LIST",
                "META-INF/DEPENDENCIES",
                "META-INF/io.netty.versions.properties"
            )
        }
    }

    defaultConfig {
        applicationId = "com.example.profit"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version") // Usando la variable de Kotlin

    // Jetpack Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Core Android y Jetpack
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Coil para imágenes
    implementation("io.coil-kt:coil-compose:2.4.0")

    // Retrofit + Gson + Logging
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")

    // Corrutinas
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    // Lifecycle y ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")

    // AppCompat y ConstraintLayout (opcional si solo usas Compose)
    implementation("androidx.appcompat:appcompat:1.6.1") // ✅ versión actualizada
    implementation("androidx.constraintlayout:constraintlayout:2.1.4") // ✅ versión actualizada

    // Room (base de datos local)
    implementation("androidx.room:room-runtime:2.5.0")
    implementation(libs.firebase.appdistribution.gradle)
    implementation(libs.androidx.navigation.runtime.android)
    kapt("androidx.room:room-compiler:2.5.0") // ⚠ Cambiado de annotationProcessor a kapt
    implementation("androidx.room:room-ktx:2.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

    // Ktor
    implementation("io.ktor:ktor-client-core:2.3.5")
    implementation("io.ktor:ktor-client-cio:2.3.5")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.5")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.5")
    implementation("io.ktor:ktor-client-logging:2.3.5")

    implementation ("androidx.compose.runtime:runtime-livedata:1.6.0")
    implementation ("com.jakewharton.threetenabp:threetenabp:1.4.5")

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.material3:material3:1.1.0")


    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Material Desing
    implementation("com.google.android.material:material:1.12.0")
    }
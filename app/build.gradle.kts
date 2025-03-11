import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.jetbrainsKotlinSerialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.safeArgs)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.example.irecipefinder"
    compileSdk = 34

    val localProperties = Properties().apply {
        val localPropertiesFile = project.rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            load(localPropertiesFile.inputStream())
        }
    }

    defaultConfig {
        applicationId = "com.example.irecipefinder"
        minSdk = 34
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        val apiKey =
            localProperties.getProperty("OPENAI_API_KEY") ?: System.getenv("OPENAI_API_KEY") ?: ""
        if (apiKey.isEmpty()) {
            throw GradleException("API Key is missing. Please provide it in local.properties or via environment variables.")
        }
        buildConfigField("String", "OPENAI_API_KEY", "\"$apiKey\"")
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
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.6"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.core.splash)
    // Navigation
    implementation(libs.androidx.navigation.compose)
    // Kotlin serialization
    implementation(libs.jetbrains.kotlin.serialization)
    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel)
    // LiveData
    implementation(libs.androidx.lifecycle.livedata)
    // Saved state module for ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.androidx.datastore.core.android)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.firebase.crashlytics)
    implementation(libs.androidx.monitor)
    implementation(libs.androidx.junit.ktx)
    androidTestImplementation("junit:junit:4.12")
//    implementation(libs.maps)
//    implementation(libs.play.services.maps)
    // Annotation processor
    annotationProcessor(libs.androidx.lifecycle.compiler)
    // Integration Compose with ViewModels
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    // Integration Compose with LiveData
    implementation(libs.androidx.lifecycle.livedata.compose)
    // ConstraintLayout in Jetpack Compose
    implementation(libs.androidx.constraint.layout)
    // Hilt
    ksp(libs.androidx.hilt.android.compiler)
    implementation(libs.androidx.hilt)
    implementation(libs.androidx.hilt.navigation.compose)
    // Retrofit
    implementation(libs.androidx.retrofit)
    // Gson converter
    implementation(libs.androidx.gson.converter)
    implementation(libs.androidx.logging.interceptor)
    // Room
    implementation(libs.androidx.room)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.kotlin)
    // Coroutines
    implementation(libs.androidx.coroutines.core)
    implementation(libs.androidx.coroutines.kotlin)
    //Permissions
    implementation(libs.accompanist.permissions)
    // Firebase
    implementation(platform(libs.google.firebase.bom))
    implementation(libs.google.firebase.messaging)
    //Permissions
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")

    implementation("io.coil-kt:coil-compose:2.3.0")
    implementation("io.coil-kt.coil3:coil-compose:3.0.4")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.0.4")
    implementation("commons-io:commons-io:2.13.0")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

    implementation("androidx.activity:activity-ktx:1.9.3")
}
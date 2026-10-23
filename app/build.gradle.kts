
// App-level build.gradle.kts
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.room.compiler.plugin)
}

android {
    namespace = "com.example.tracklayoff"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.tracklayoff"
        minSdk = 26
        targetSdk = 35
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

    buildFeatures {
        compose = true
        buildConfig = true
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    room {
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {
    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.hilt.work)
    implementation(project(":core:favorites"))
    // Project Sub-Modules
    implementation(project(":core:designsystem"))
    implementation(project(":core:common"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":feature:feed"))
    implementation(project(":feature:chat-ai"))
    implementation(project(":feature:auth"))
    implementation(project(":feature:tracker"))
    implementation(project(":feature:reporting"))
    implementation(project(":feature:notifications"))

    // AndroidX Core and Android Runtime Lifecycle Primitives
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Jetpack Compose Framework Integration BOM
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.googleid)
    implementation(libs.kotlinx.collections.immutable)

    // Asynchronous Concurrent Processing (Coroutines)
    implementation(libs.kotlinx.coroutines.android)

    // Networking Components (Retrofit + OkHttp)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.logging.interceptor)

    // Offline-First Local Data Persistence (Room Cache Engine)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Coil Image Rendering Engine
    implementation(libs.coil.compose)

    // Firebase Diagnostics
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.auth)

    // Hilt Dependency Injection
    implementation(libs.dagger.hilt.android)
    ksp(libs.dagger.hilt.compiler)

    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.process)

    // Testing Suite Configurations
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
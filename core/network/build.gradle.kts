
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.kotlin.ksp)
}

val isCiBuild = providers.gradleProperty("ciBuild").map(String::toBoolean).getOrElse(false)

android {
    namespace = "com.example.tracklayoff.core.network"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
    }

    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", if (isCiBuild) "\"http://127.0.0.1:9/api/v1/\"" else "\"https://layoff-chat-dev-241672723896.us-east4.run.app/api/v1/\"")
            buildConfigField("String", "FAVORITES_BASE_URL", if (isCiBuild) "\"http://127.0.0.1:9/api/v1/\"" else "\"https://layoff-api-dev-241672723896.us-east4.run.app/api/v1/\"")
        }
        release {
            buildConfigField("String", "BASE_URL", "\"\"")
            buildConfigField("String", "FAVORITES_BASE_URL", "\"\"")
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
        buildConfig = true
    }
}

dependencies {
    api(project(":core:common"))

    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.logging.interceptor)

    implementation(libs.dagger.hilt.android)
    ksp(libs.dagger.hilt.compiler)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.messaging)
}

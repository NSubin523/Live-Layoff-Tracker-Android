import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.kotlin.ksp)
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if(file.exists()) {
        load(file.inputStream())
    }
}
val physicalMacIp = localProperties.getProperty("MAC_IP")

android {
    namespace = "com.example.tracklayoff.core.network"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
        buildConfigField("String", "MAC_IP", "$physicalMacIp")
    }

    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"http://192.168.1.149:8000/api/v1/\"")
            buildConfigField("String", "FAVORITES_BASE_URL", "\"http://192.168.1.149:8081/api/v1/\"")
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

plugins {
    id("com.android.library")
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.uikit"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 26
        targetSdk = 36
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
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.material3)
    implementation(libs.material)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.compose.ui.text)
    debugImplementation(libs.androidx.ui.tooling)
}
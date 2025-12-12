import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hiltAndroid)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
}

android {
    namespace = "com.todoapp.mobile"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.todoapp.mobile"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
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
    }
}

configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    android.set(true)
    ignoreFailures.set(false)
    additionalEditorconfig.set( // not supported until ktlint 0.49
        mapOf(
            "ktlint_standard_function-naming" to "disabled",
        ),
    )
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
    }
}
detekt {
    autoCorrect = true // Ktlint ile otomatik olarak formatlama işleminin yapılması
    buildUponDefaultConfig = true // Default olarak yapılandırma
    allRules = false // Bütün kuralların aktif edilip edilmemesi
    config.setFrom("$projectDir/detekt.yml") // Kuralların bulunduğu detekt.yml dosyasını çalışması
    baseline = file("$projectDir/config/baseline.xml") // Sorunların reportlandığı baseline.xml dosyası
}
// Adım-3
tasks.withType<Detekt>().configureEach {
    jvmTarget = "17"
}
tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = "17"
}

fun Project.getGitStagedFiles(rootDir: File): Provider<List<File>> {
    return providers.exec {
        commandLine("git", "--no-pager", "diff", "--name-only", "--cached")
    }.standardOutput.asText
        .map { outputText ->
            outputText.trim()
                .split("\n")
                .filter { it.isNotBlank() }
                .map { File(rootDir, it) }
        }
}
tasks.withType<Detekt>().configureEach {
    if (project.hasProperty("precommit")) {
        val rootDir = project.rootDir
        val projectDir = projectDir

        val fileCollection = files()

        setSource(
            getGitStagedFiles(rootDir)
                .map { stagedFiles ->
                    val stagedFilesFromThisProject = stagedFiles
                        .filter { it.startsWith(projectDir) }

                    fileCollection.setFrom(*stagedFilesFromThisProject.toTypedArray())

                    fileCollection.asFileTree
                }
        )
    }
}

dependencies {
    implementation(project(":uikit"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Dependency Injection (Hilt)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Network (Retrofit & Serialization)
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlinx.serialization.json)

    // Local Storage (Room)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Utils
    implementation(libs.timber)
    detektPlugins(libs.detekt.formatting)
}

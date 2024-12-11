import java.io.ByteArrayOutputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.symbol.processing)
}

val appVersion: String = "1.1.8"

val gitCommitId: String = try {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine("git", "rev-parse", "--short", "HEAD")
        standardOutput = stdout
    }
    stdout.toString().trim()
} catch (_: Exception) {
    "0"
}

android {
    namespace = "io.github.skydynamic.maiproberplus"
    compileSdk = 34

    defaultConfig {
        applicationId = "io.github.skydynamic.maiproberplus"
        minSdk = 31
        targetSdk = 34
        versionName = appVersion
        versionCode = appVersion.replace(".", "").toInt()

        versionNameSuffix = "-$gitCommitId"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters += listOf("x86_64", "arm64-v8a")
        }

        splits {
            abi {
                isEnable = true
                include(
                    "arm64-v8a",
                    "x86_64",
                )
                exclude(
                    "armeabi-v7a",
                    "x86",
                    "riscv64",
                    "mips",
                    "mips64",
                    "armeabi"
                )
                isUniversalApk = true
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        getByName("debug") {
            isDebuggable = true
        }

        create("snapshot") {
            initWith(getByName("release"))
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    applicationVariants.all {
        val variant = this
        val versionCodes =
            mapOf( "arm64-v8a" to 4, "x86_64" to 4, "universal" to 4)

        variant.outputs
            .map { it as com.android.build.gradle.internal.api.ApkVariantOutputImpl }
            .forEach { output ->
                val abi = if (output.getFilter("ABI") != null)
                    output.getFilter("ABI")
                else
                    "universal"

                output.outputFileName = "MaiProberPlus-${versionName}-${abi}-${variant.buildType.name}.apk"
                if (versionCodes.containsKey(abi)) {
                    output.versionCodeOverride =
                        (1000000 * versionCodes[abi]!!).plus(variant.versionCode)
                } else {
                    return@forEach
                }
            }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.animation.graphics)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Android room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Jsoup
    implementation(libs.jsoup)

    implementation(libs.nanohttpd.nanohttpd)

    // Ktor Client
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    // Ktor Serialization
    implementation(libs.ktor.serialization.kotlinx.json)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.coil.compose)
    implementation(libs.coil.network.ktor3)
}
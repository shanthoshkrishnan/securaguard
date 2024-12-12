plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.serialization")
}

kotlin {
    androidTarget()


    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.2")
                implementation("org.jetbrains.kotlin:kotlin-stdlib")
            }
        }
        val androidMain by getting
        val iosMain by creating {
            dependsOn(commonMain)
        }
    }
}

android {
    namespace = "com.example.securaguardsharedmodule"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

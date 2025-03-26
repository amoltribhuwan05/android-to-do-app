plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt") // Required for Room
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" //  COMPOSE COMPILER PLUGIN!
}

android {
    namespace = "com.rusouz.rutodo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.rusouz.rutodo"
        minSdk = 24
        targetSdk = 34
        versionCode = 3
        versionName = "1.1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1" //  MATCH COMPILER PLUGIN VERSION!
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
    implementation(platform(libs.androidx.compose.bom.v20250300)) // CORRECT BOM VERSION!
    implementation(libs.androidx.compose.ui.ui2)
    implementation(libs.androidx.compose.ui.ui.graphics2)
    implementation(libs.androidx.compose.ui.ui.tooling.preview2)
    implementation(libs.material3) // Material 3!
    //implementation("androidx.compose.material:material:1.5.4") // REMOVE Material 2
    implementation(libs.material.icons.extended) // Keep icons-extended
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt("androidx.room:room-compiler:2.6.1")


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom.v20250300)) // CORRECT BOM!
    androidTestImplementation(libs.androidx.compose.ui.ui.test.junit4) // NO VERSION NEEDED
    debugImplementation(libs.androidx.compose.ui.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.ui.test.manifest)
}
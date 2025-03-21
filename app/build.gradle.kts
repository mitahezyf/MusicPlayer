plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.musicplayer"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.musicplayer"
        minSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.6.0" // Dodane dla Compose
    }
}

dependencies {
    // Podstawowe biblioteki AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Jetpack Compose
    implementation(platform(libs.androidx.compose.bom))
//    implementation(libs.androidx.ui)
//    implementation(libs.androidx.ui.graphics)
//    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // ExoPlayer
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)

    // Uprawnienia (do dostępu do plików)
    implementation(libs.accompanist.permissions)
//    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.navigation.compose)

    // Testy
    testImplementation(libs.junit)
//    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
//    androidTestImplementation(libs.androidx.ui.test.junit4)
//    debugImplementation(libs.androidx.ui.tooling)
//    debugImplementation(libs.androidx.ui.test.manifest)
//    implementation(libs.androidx.material.icons.extended)


//    implementation(libs.activity.compose.v182)
//    implementation(libs.androidx.ui.v161)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

//    implementation(libs.androidx.hilt.navigation.compose)

//    implementation(libs.androidx.activity.compose.v180)

//    implementation(libs.androidx.media3.exoplayer.v120)
//    implementation(libs.androidx.media3.ui.v120)

//    implementation(libs.material3)

    //implementation platform("androidx.compose:compose-bom:2025.03.00") // Użyj swojej wersji BOM
    implementation(libs.androidx.material.icons.extended)
}

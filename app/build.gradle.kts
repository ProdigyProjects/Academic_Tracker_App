plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android) version "1.9.10"  // Specify Kotlin version explicitly
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.academictrackerapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.academictrackerapp"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    // Enable Compose in the project
    buildFeatures {
        compose = true
        viewBinding = true
    }

    // Set Compose options
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"  // Replace with the latest stable version
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.firebase.auth)
    implementation("androidx.work:work-runtime-ktx:2.7.1")
    implementation("com.google.ai.client.generativeai:generativeai:0.7.0")
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.activity)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.firebase.database.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("com.github.yukuku:ambilwarna:2.0.1")  // Color picker dependency
    implementation("com.github.yukuku:ambilwarna:2.0.1")
    implementation("com.google.ai.client.generativeai:generativeai:0.4.0")

    // Jetpack Compose dependencies
    implementation("androidx.compose.ui:ui:1.5.3")
    implementation("androidx.compose.material:material:1.5.3")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.3")
    implementation("androidx.activity:activity-compose:1.7.2")

    // Optional: Compose tooling for preview and debug
    debugImplementation("androidx.compose.ui:ui-tooling:1.5.3")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.5.3")

    implementation("androidx.compose.material3:material3:1.1.0")

    implementation ("com.github.bumptech.glide:glide:4.13.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.13.0")  // for annotation processing

    implementation("com.google.android.gms:play-services-auth:21.2.0")

}

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.myrecipeapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myrecipeapp"
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

    buildFeatures {
        viewBinding = true
    }

    packagingOptions {
        exclude("META-INF/LICENSE.md")
        exclude("META-INF/LICENSE-notice.md")
    }

    // Force the version of androidx.test:core to avoid version conflicts
    configurations.all {
        resolutionStrategy {
            force("androidx.test:core:1.6.0")  // Or you can use 1.5.0 if preferred
        }
    }
}

dependencies {
    implementation("com.google.firebase:firebase-database:21.0.0")
    implementation("com.google.ar:core:1.47.0")

    val nav_version = "2.7.7"
    val room_version = "2.6.1"

    // Room
    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")

    // Lifecycle and Fragment
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.1")
    implementation("androidx.fragment:fragment-ktx:1.7.1")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")

    // Core and UI
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")

    implementation("org.mockito:mockito-inline:4.0.0")
    implementation("org.mockito.kotlin:mockito-kotlin:4.0.0")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.12.0")
    kapt("com.github.bumptech.glide:compiler:4.12.0")

    // Unit Testing
    testImplementation("androidx.test.ext:junit:1.1.5")
    testImplementation("androidx.test:core-ktx:1.6.0")  // Updated to match the forced version
    testImplementation("org.robolectric:robolectric:4.10")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("org.mockito:mockito-inline:4.0.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.7.1")
    testImplementation("org.hamcrest:hamcrest:2.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")

    debugImplementation("androidx.fragment:fragment-testing:1.5.7")

    // Fragment testing dependencies
    testImplementation("io.mockk:mockk:1.13.7")

    // AndroidX Test libraries for Instrumented tests
    androidTestImplementation("androidx.test:core:1.6.0")  // Ensure the same version here
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test:runner:1.6.0")

    // Robolectric for running Android tests outside of an emulator (for unit tests)
    androidTestImplementation("org.robolectric:robolectric:4.10.3")

    // Mockito for mocking dependencies in tests
    androidTestImplementation("org.mockito:mockito-core:4.0.0")
    androidTestImplementation("org.mockito:mockito-inline:4.0.0")
    androidTestImplementation("org.mockito:mockito-android:4.0.0")
    androidTestImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
    androidTestImplementation("io.mockk:mockk:1.13.7")

    // ShadowToast for verifying Toast messages in Robolectric
    androidTestImplementation("org.robolectric:shadows-framework:4.10.3")

    // For Android ViewModel and LiveData (used in the fragment tests)
    androidTestImplementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.0")
    androidTestImplementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.0")

    // For FragmentScenario
    debugImplementation("androidx.fragment:fragment-testing:1.6.0")

    // If using Kotlin, ensure you have the following for Kotlin specific testing
    androidTestImplementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.20")

    // AndroidX Fragment
    androidTestImplementation("androidx.fragment:fragment-ktx:1.6.0")
}

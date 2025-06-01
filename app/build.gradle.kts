plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.mcassignment"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mcassignment"
        minSdk = 24
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("org.json:json:20210307")
    implementation ("com.github.skydoves:colorpickerview:2.2.4")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("io.socket:socket.io-client:2.1.0") {
        exclude(group="org.json", module= "json")
    }
    implementation("com.google.code.gson:gson:2.10.1")// For JSON handling
    implementation("androidx.recyclerview:recyclerview:1.3.2") // For message list
}

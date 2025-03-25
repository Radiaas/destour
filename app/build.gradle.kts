plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.ksp)
    id("kotlin-kapt")
    alias(libs.plugins.daggerHilt)

    alias(libs.plugins.firebaseGms)
    alias(libs.plugins.firebaseCrashlytic)
    alias(libs.plugins.firebasePref)
}

android {
    namespace = "com.example.destour"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.destour"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
            }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.databinding.runtime)
    implementation(libs.mediation.test.suite)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation (libs.androidx.core.ktx.v190)
    implementation (libs.androidx.appcompat.v151)
    implementation (libs.material.v161)
    implementation (libs.androidx.constraintlayout)
    implementation (libs.androidx.lifecycle.livedata.ktx.v251)
    implementation (libs.androidx.lifecycle.viewmodel.ktx.v251)
    implementation (libs.androidx.room.runtime.v250)
    ksp(libs.room.compiler)
    implementation (libs.room.ktx)
    implementation (libs.sdp.android)
    implementation (libs.ssp.android)
    implementation (libs.glide)
    annotationProcessor (libs.compiler)

    implementation (libs.imageslideshow.v010)

    implementation (libs.androidx.biometric.v110)

    implementation(libs.androidx.fragment.ktx)
    implementation(libs.hilt.android.v2511)
    kapt(libs.hilt.android.compiler.v2511)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.perf)
    implementation(libs.coreCrocodic)

    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation (libs.logging.interceptor)

    implementation ("androidx.security:security-crypto:1.1.0-alpha06")

    implementation ("androidx.recyclerview:recyclerview:1.3.2")

    implementation("io.coil-kt:coil:2.5.0")

    implementation ("com.google.android.gms:play-services-ads:22.6.0")

}

kapt {
    correctErrorTypes = true
}
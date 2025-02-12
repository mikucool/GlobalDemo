plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
//    id("applovin-quality-service")
}
/*applovin {
    apiKey = "qWSEfQRhBblNPZKpX0Ikm5pk8K3XiUFmAdTwLLitAgT-nZdIMIqoN2-RpCdO0qTocL5Nd3KL04gddZQnszhiH-"
}*/

android {
    namespace = "com.example.globaldemo"
    compileSdk = 35

    defaultConfig {
        applicationId = "org.cocos2dx.emo"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            keyAlias = project.properties["RELEASE_KEY_ALIAS"] as String
            keyPassword = project.properties["RELEASE_KEY_PASSWORD"] as String
            storeFile = file(project.properties["RELEASE_STORE_FILE"] as String)
            storePassword = project.properties["RELEASE_STORE_PASSWORD"] as String
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            isDebuggable = false
            signingConfig = signingConfigs["release"]
        }

        debug {
            isMinifyEnabled = false
//            signingConfig = signingConfigs["release"]
            isDebuggable = true
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
        buildConfig = true
    }

    packagingOptions.doNotStrip("*/arm64-v8a/libdu.so")
    packagingOptions.doNotStrip("*/armeabi-v7a/libdu.so")
    packagingOptions.doNotStrip("*/x86/libdu.so")

}

dependencies {
    val dependencies = listOf(
        fileTree(mapOf("dir" to "/libs", "include" to listOf("*.jar", "*.aar")))
    )
    dependencies.forEach {
        implementation(it)
    }

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    // compose view-model
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    // kotlin coroutines
    runtimeOnly(libs.kotlinx.coroutines.core)
    // retrofit2
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.gson)
    implementation(libs.logging.interceptor)
    // ktx serialization
    implementation(libs.kotlinx.serialization.json)
    // datastore
    implementation(libs.androidx.datastore.preferences)
    // kwai
    implementation(libs.adapi)
    implementation(libs.adimpl)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.appcompat)
    // bigo
    implementation(libs.bigo.ads)
    // max
    implementation(libs.applovin.sdk)
    // thinking data
    implementation(libs.thinkinganalyticssdk)
    // 数盟
    implementation(libs.dusdk)
    // adjust
    implementation(libs.adjust.android)
    implementation(libs.installreferrer)
    implementation(libs.play.services.ads.identifier)
    implementation(libs.play.services.appset)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
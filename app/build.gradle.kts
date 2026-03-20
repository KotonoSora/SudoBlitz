import java.net.URL
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.devtools.ksp)
}

android {
    namespace = "com.kotonosora.sudoblitz"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.kotonosora.sudoblitz"
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
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.coil.compose)
    implementation(libs.retrofit)
    implementation(libs.converter.moshi)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.accompanist.permissions)
    implementation(libs.play.services.location)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.core)
    implementation(libs.logging.interceptor)
    implementation(libs.okhttp)
    implementation(libs.moshi.kotlin)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.material)
    implementation(libs.billing.ktx)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.core)
    testImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.runner)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    "ksp"(libs.androidx.room.compiler)
    "ksp"(libs.moshi.kotlin.codegen)
}

tasks.register("generateAssets") {
    doLast {
        val fontDir = file("src/main/res/font")
        fontDir.mkdirs()
        val fontUrl = URL("https://raw.githubusercontent.com/google/fonts/main/ofl/pressstart2p/PressStart2P-Regular.ttf")
        fontUrl.openStream().use { input ->
            file("src/main/res/font/press_start_2p.ttf").outputStream().use { output ->
                input.copyTo(output)
            }
        }
        
        val rawDir = file("src/main/res/raw")
        rawDir.mkdirs()
        
        fun writeWav(name: String, freqFunc: (Double) -> Double, durationMs: Int) {
            val sampleRate = 44100
            val numSamples = (sampleRate * durationMs) / 1000
            val wavFile = file("src/main/res/raw/$name.wav")
            FileOutputStream(wavFile).use { out ->
                val dataSize = numSamples * 2
                val byteRate = sampleRate * 2
                
                val header = ByteBuffer.allocate(44).apply {
                    order(ByteOrder.LITTLE_ENDIAN)
                    put("RIFF".toByteArray())
                    putInt(36 + dataSize)
                    put("WAVE".toByteArray())
                    put("fmt ".toByteArray())
                    putInt(16)
                    putShort(1.toShort()) // PCM
                    putShort(1.toShort()) // Channels
                    putInt(sampleRate)
                    putInt(byteRate)
                    putShort(2.toShort()) // Block align
                    putShort(16.toShort()) // Bits per sample
                    put("data".toByteArray())
                    putInt(dataSize)
                }.array()
                
                out.write(header)
                
                val data = ByteBuffer.allocate(dataSize).apply {
                    order(ByteOrder.LITTLE_ENDIAN)
                    for (i in 0 until numSamples) {
                        val t = i.toDouble() / sampleRate
                        val freq = freqFunc(t)
                        // Simple sine wave
                        val value = (Math.sin(2.0 * Math.PI * freq * t) * 32767.0 * 0.5).toInt().toShort()
                        putShort(value)
                    }
                }.array()
                
                out.write(data)
            }
        }
        
        // Short beep for tap
        writeWav("tap", { 800.0 }, 100)
        // Low buzz for error
        writeWav("error", { 150.0 }, 300)
        // Ascending tone for win
        writeWav("win", { t -> 400.0 + 800.0 * t }, 600)
        // Descending tone for lose
        writeWav("lose", { t -> 400.0 - 400.0 * t }, 600)
    }
}

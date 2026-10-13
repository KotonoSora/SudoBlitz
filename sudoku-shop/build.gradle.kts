import com.android.build.api.dsl.LibraryExtension

plugins {
    alias(libs.plugins.android.library)
}

configure<LibraryExtension> {
    namespace = "local.sudoku.shop"
    compileSdk = 37

    defaultConfig {
        minSdk = 24
        consumerProguardFiles("consumer-rules.pro")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(project(":core-billing"))
    implementation(project(":core-shop-coins"))
    implementation(project(":core-ui"))
    implementation(libs.androidx.datastore.preferences)

    testImplementation(libs.junit)
    testImplementation(project(":sudoku-engine"))
}

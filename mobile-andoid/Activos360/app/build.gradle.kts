plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.openapi.generator") version "7.20.0"
}

android {
    namespace = "com.example.activos360"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.activos360"
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

openApiGenerate {
    generatorName.set("kotlin")
    // 1. Ruta a tu archivo generado con IntelliJ
    inputSpec.set("$projectDir/src/main/resources/api-docs.yaml")

    // 2. Donde se guardará el código (no lo toques, déjalo en build)
    outputDir.set("$buildDir/generated/openapi")

    // 3. Nombres de tus paquetes
    apiPackage.set("com.tuapp.network.api")
    modelPackage.set("com.tuapp.network.model")

    configOptions.set(mapOf(
        "library" to "jvm-retrofit2", // Usa Retrofit 2
        "serializationLibrary" to "moshi", // O "gson" / "kotlinx_serialization"
        "useCoroutines" to "true", // Genera funciones 'suspend'
        "omitGradleWrapper" to "true"
    ))
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

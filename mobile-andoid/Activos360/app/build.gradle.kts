plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.openapi.generator") version "7.5.0"
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
        freeCompilerArgs = listOf("-XXLanguage:+PropertyParamAnnotationDefaultTargetMode")
    }
    buildFeatures {
        compose = true
    }

    //uso del openapi
    sourceSets {
        getByName("main") {
            java.srcDir("${project.buildDir}/generated/openapi/src/main/kotlin")
        }
    }
}

openApiGenerate {
    generatorName.set("kotlin")
    /*// 1. Ruta a tu archivo generado con IntelliJ
    inputSpec.set("$projectDir/src/main/resources/api-docs.yaml")

    // 2. Donde se guardará el código (no lo toques, déjalo en build)
    outputDir.set("$buildDir/generated/openapi")
*/
    // Carpeta para usar el acrhivo api
    inputSpec.set("$rootDir/specs/api-docs.json")

    outputDir.set("${project.buildDir}/generated/openapi")

    // 3. Nombres de tus paquetes
    apiPackage.set("com.example.activos360.api")
    modelPackage.set("com.example.activos360.model")

    configOptions.set(mapOf(
        "library" to "jvm-retrofit2", // Usa Retrofit 2
        "serializationLibrary" to "moshi", // O "gson" / "kotlinx_serialization"
        "useCoroutines" to "true", // Genera funciones 'suspend'
        "omitGradleWrapper" to "true"
    ))
}

dependencies {
    //RETROFIT PARA LAS PETICIONES
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    //VIEWMODEL
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    //moshi para usar los json
    implementation("com.squareup.moshi:moshi-kotlin:1.15.2")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")

    // Librería para el interceptor de logs (líneas rojas de okhttp3)
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Librería para el ScalarsConverter (líneas rojas de scalars)
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")

    //escaner parame el qr
    implementation("com.google.android.gms:play-services-code-scanner:16.1.0")

    //para las img
    implementation("io.coil-kt:coil-compose:2.7.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
tasks.whenTaskAdded {
    if (name == "preBuild") {
        dependsOn("openApiGenerate")
    }
}
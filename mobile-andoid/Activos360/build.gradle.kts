// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("org.openapi.generator") version "7.20.0"
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
package com.example.activos360.back.api

import org.openapitools.client.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json


interface QrControllerApi {
    /**
     * GET api/qr/descargar-pdf
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param texto 
     * @param nombreArchivo  (optional, default to "mi_codigo_qr")
     * @return [kotlin.ByteArray]
     */
    @GET("api/qr/descargar-pdf")
    suspend fun descargarPdf(@Query("texto") texto: kotlin.String, @Query("nombreArchivo") nombreArchivo: kotlin.String? = "mi_codigo_qr"): Response<kotlin.ByteArray>

    /**
     * GET api/qr/generar-qr
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param texto 
     * @param ancho  (optional, default to 100)
     * @param alto  (optional, default to 100)
     * @return [kotlin.ByteArray]
     */
    @GET("api/qr/generar-qr")
    suspend fun obtenerQr(@Query("texto") texto: kotlin.String, @Query("ancho") ancho: kotlin.Int? = 100, @Query("alto") alto: kotlin.Int? = 100): Response<kotlin.ByteArray>

}

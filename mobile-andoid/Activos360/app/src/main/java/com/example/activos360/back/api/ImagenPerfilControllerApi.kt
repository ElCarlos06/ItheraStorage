package com.example.activos360.back.api

import com.example.activos360.back.model.ImagenPerfilResponse
import org.openapitools.client.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.example.activos360.back.model.ModelApiResponse
import com.example.activos360.back.model.Save11Request

interface ImagenPerfilControllerApi {
    /**
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [ModelApiResponse]
     */
    @DELETE("api/imagen-perfil/{id}")
    suspend fun delete1(@Path("id") id: Long): Response<ModelApiResponse>

    /**
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [ModelApiResponse]
     */
    @GET("api/imagen-perfil/{id}")
    suspend fun findByCorreo(@Path("id") id: String): Response<ImagenPerfilResponse>

    /**
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @param save11Request  (optional)
     * @return [ModelApiResponse]
     */
    @POST("api/imagen-perfil/{id}")
    suspend fun save12(@Path("id") id: String, @Body save11Request: Save11Request? = null): Response<ModelApiResponse>

}

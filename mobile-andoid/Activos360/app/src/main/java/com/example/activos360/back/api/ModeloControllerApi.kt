package com.example.activos360.back.api

import org.openapitools.client.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.example.activos360.model.ModelApiResponse
import com.example.activos360.model.ModeloDTO

interface ModeloControllerApi {
    /**
     * DELETE api/modelos/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [ModelApiResponse]
     */
    @DELETE("api/modelos/{id}")
    suspend fun deleteById3(@Path("id") id: kotlin.Long): Response<ModelApiResponse>

    /**
     * GET api/modelos
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @return [ModelApiResponse]
     */
    @GET("api/modelos")
    suspend fun findAll8(): Response<ModelApiResponse>

    /**
     * GET api/modelos/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [ModelApiResponse]
     */
    @GET("api/modelos/{id}")
    suspend fun findById8(@Path("id") id: kotlin.Long): Response<ModelApiResponse>

    /**
     * GET api/modelos/marca/{marcaId}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param marcaId 
     * @return [ModelApiResponse]
     */
    @GET("api/modelos/marca/{marcaId}")
    suspend fun findByMarca(@Path("marcaId") marcaId: kotlin.Long): Response<ModelApiResponse>

    /**
     * POST api/modelos
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param modeloDTO 
     * @return [ModelApiResponse]
     */
    @POST("api/modelos")
    suspend fun save8(@Body modeloDTO: ModeloDTO): Response<ModelApiResponse>

    /**
     * PUT api/modelos/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @param modeloDTO 
     * @return [ModelApiResponse]
     */
    @PUT("api/modelos/{id}")
    suspend fun update8(@Path("id") id: kotlin.Long, @Body modeloDTO: ModeloDTO): Response<ModelApiResponse>

}

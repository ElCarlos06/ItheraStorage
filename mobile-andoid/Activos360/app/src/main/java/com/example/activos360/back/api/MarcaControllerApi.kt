package com.example.activos360.back.api

import org.openapitools.client.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.example.activos360.model.MarcaDTO
import com.example.activos360.model.ModelApiResponse

interface MarcaControllerApi {
    /**
     * DELETE api/marcas/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [ModelApiResponse]
     */
    @DELETE("api/marcas/{id}")
    suspend fun deleteById4(@Path("id") id: kotlin.Long): Response<ModelApiResponse>

    /**
     * GET api/marcas
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @return [ModelApiResponse]
     */
    @GET("api/marcas")
    suspend fun findAll9(): Response<ModelApiResponse>

    /**
     * GET api/marcas/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [ModelApiResponse]
     */
    @GET("api/marcas/{id}")
    suspend fun findById9(@Path("id") id: kotlin.Long): Response<ModelApiResponse>

    /**
     * POST api/marcas
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param marcaDTO 
     * @return [ModelApiResponse]
     */
    @POST("api/marcas")
    suspend fun save9(@Body marcaDTO: MarcaDTO): Response<ModelApiResponse>

    /**
     * PUT api/marcas/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @param marcaDTO 
     * @return [ModelApiResponse]
     */
    @PUT("api/marcas/{id}")
    suspend fun update9(@Path("id") id: kotlin.Long, @Body marcaDTO: MarcaDTO): Response<ModelApiResponse>

}

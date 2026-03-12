package com.example.activos360.back.api

import org.openapitools.client.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.example.activos360.model.ModelApiResponse
import com.example.activos360.model.TipoActivoDTO

interface TipoActivoControllerApi {
    /**
     * GET api/tipo-activos
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @return [ModelApiResponse]
     */
    @GET("api/tipo-activos")
    suspend fun findAll2(): Response<ModelApiResponse>

    /**
     * GET api/tipo-activos/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [ModelApiResponse]
     */
    @GET("api/tipo-activos/{id}")
    suspend fun findById2(@Path("id") id: kotlin.Long): Response<ModelApiResponse>

    /**
     * POST api/tipo-activos
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param tipoActivoDTO 
     * @return [ModelApiResponse]
     */
    @POST("api/tipo-activos")
    suspend fun save2(@Body tipoActivoDTO: TipoActivoDTO): Response<ModelApiResponse>

    /**
     * PATCH api/tipo-activos/{id}/status
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [ModelApiResponse]
     */
    @PATCH("api/tipo-activos/{id}/status")
    suspend fun toggleStatus1(@Path("id") id: kotlin.Long): Response<ModelApiResponse>

    /**
     * PUT api/tipo-activos/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @param tipoActivoDTO 
     * @return [ModelApiResponse]
     */
    @PUT("api/tipo-activos/{id}")
    suspend fun update2(@Path("id") id: kotlin.Long, @Body tipoActivoDTO: TipoActivoDTO): Response<ModelApiResponse>

}

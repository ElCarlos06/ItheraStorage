package com.example.activos360.back.api

import org.openapitools.client.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.example.activos360.model.EspacioDTO
import com.example.activos360.model.ModelApiResponse

interface EspacioControllerApi {
    /**
     * DELETE api/espacios/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [ModelApiResponse]
     */
    @DELETE("api/espacios/{id}")
    suspend fun deleteById5(@Path("id") id: kotlin.Long): Response<ModelApiResponse>

    /**
     * GET api/espacios
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @return [ModelApiResponse]
     */
    @GET("api/espacios")
    suspend fun findAll11(): Response<ModelApiResponse>

    /**
     * GET api/espacios/edificio/{edificioId}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param edificioId 
     * @return [ModelApiResponse]
     */
    @GET("api/espacios/edificio/{edificioId}")
    suspend fun findByEdificio(@Path("edificioId") edificioId: kotlin.Long): Response<ModelApiResponse>

    /**
     * GET api/espacios/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [ModelApiResponse]
     */
    @GET("api/espacios/{id}")
    suspend fun findById11(@Path("id") id: kotlin.Long): Response<ModelApiResponse>

    /**
     * POST api/espacios
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param espacioDTO 
     * @return [ModelApiResponse]
     */
    @POST("api/espacios")
    suspend fun save11(@Body espacioDTO: EspacioDTO): Response<ModelApiResponse>

    /**
     * PATCH api/espacios/{id}/status
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [ModelApiResponse]
     */
    @PATCH("api/espacios/{id}/status")
    suspend fun toggleStatus2(@Path("id") id: kotlin.Long): Response<ModelApiResponse>

    /**
     * PUT api/espacios/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @param espacioDTO 
     * @return [ModelApiResponse]
     */
    @PUT("api/espacios/{id}")
    suspend fun update11(@Path("id") id: kotlin.Long, @Body espacioDTO: EspacioDTO): Response<ModelApiResponse>

}

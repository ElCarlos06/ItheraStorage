package com.example.activos360.back.api

import org.openapitools.client.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.example.activos360.model.EdificioDTO
import com.example.activos360.model.ModelApiResponse

interface EdificioControllerApi {
    /**
     * DELETE api/edificios/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [ModelApiResponse]
     */
    @DELETE("api/edificios/{id}")
    suspend fun deleteById6(@Path("id") id: kotlin.Long): Response<ModelApiResponse>

    /**
     * GET api/edificios
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @return [ModelApiResponse]
     */
    @GET("api/edificios")
    suspend fun findAll12(): Response<ModelApiResponse>

    /**
     * GET api/edificios/campus/{campusId}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param campusId 
     * @return [ModelApiResponse]
     */
    @GET("api/edificios/campus/{campusId}")
    suspend fun findByCampus(@Path("campusId") campusId: kotlin.Long): Response<ModelApiResponse>

    /**
     * GET api/edificios/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [ModelApiResponse]
     */
    @GET("api/edificios/{id}")
    suspend fun findById12(@Path("id") id: kotlin.Long): Response<ModelApiResponse>

    /**
     * POST api/edificios
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param edificioDTO 
     * @return [ModelApiResponse]
     */
    @POST("api/edificios")
    suspend fun save12(@Body edificioDTO: EdificioDTO): Response<ModelApiResponse>

    /**
     * PATCH api/edificios/{id}/status
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [ModelApiResponse]
     */
    @PATCH("api/edificios/{id}/status")
    suspend fun toggleStatus3(@Path("id") id: kotlin.Long): Response<ModelApiResponse>

    /**
     * PUT api/edificios/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @param edificioDTO 
     * @return [ModelApiResponse]
     */
    @PUT("api/edificios/{id}")
    suspend fun update12(@Path("id") id: kotlin.Long, @Body edificioDTO: EdificioDTO): Response<ModelApiResponse>

}

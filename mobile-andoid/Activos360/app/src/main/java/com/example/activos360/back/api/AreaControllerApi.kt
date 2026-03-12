package com.example.activos360.back.api

import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.example.activos360.back.model.AreaDTO
import com.example.activos360.back.model.ModelApiResponse

interface AreaControllerApi {
    /**
     * DELETE api/areas/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [ModelApiResponse]
     */
    @DELETE("api/areas/{id}")
    suspend fun deleteById8(@Path("id") id: kotlin.Long): Response<ModelApiResponse>

    /**
     * GET api/areas
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @return [ModelApiResponse]
     */
    @GET("api/areas")
    suspend fun findAll15(): Response<ModelApiResponse>

    /**
     * GET api/areas/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [ModelApiResponse]
     */
    @GET("api/areas/{id}")
    suspend fun findById14(@Path("id") id: kotlin.Long): Response<ModelApiResponse>

    /**
     * POST api/areas
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param areaDTO 
     * @return [ModelApiResponse]
     */
    @POST("api/areas")
    suspend fun save15(@Body areaDTO: AreaDTO): Response<ModelApiResponse>

    /**
     * PUT api/areas/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @param areaDTO 
     * @return [ModelApiResponse]
     */
    @PUT("api/areas/{id}")
    suspend fun update14(@Path("id") id: kotlin.Long, @Body areaDTO: AreaDTO): Response<ModelApiResponse>

}

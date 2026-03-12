package com.example.activos360.back.api

import com.example.activos360.back.model.ModelApiResponse
import com.example.activos360.back.model.PrioridadDTO

import retrofit2.http.*
import retrofit2.Response

interface PrioridadControllerApi {
    /**
     * DELETE api/prioridades/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [ModelApiResponse]
     */
    @DELETE("api/prioridades/{id}")
    suspend fun deleteById2(@Path("id") id: kotlin.Long): Response<ModelApiResponse>

    /**
     * GET api/prioridades
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @return [ModelApiResponse]
     */
    @GET("api/prioridades")
    suspend fun findAll7(): Response<ModelApiResponse>

    /**
     * GET api/prioridades/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [ModelApiResponse]
     */
    @GET("api/prioridades/{id}")
    suspend fun findById7(@Path("id") id: kotlin.Long): Response<ModelApiResponse>

    /**
     * POST api/prioridades
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param prioridadDTO 
     * @return [ModelApiResponse]
     */
    @POST("api/prioridades")
    suspend fun save7(@Body prioridadDTO: PrioridadDTO): Response<ModelApiResponse>

    /**
     * PUT api/prioridades/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @param prioridadDTO 
     * @return [ModelApiResponse]
     */
    @PUT("api/prioridades/{id}")
    suspend fun update7(@Path("id") id: kotlin.Long, @Body prioridadDTO: PrioridadDTO): Response<ModelApiResponse>

}

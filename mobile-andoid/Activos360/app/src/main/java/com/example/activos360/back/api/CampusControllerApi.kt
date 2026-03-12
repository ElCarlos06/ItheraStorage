package com.example.activos360.back.api

import com.example.activos360.back.model.CampusDTO
import com.example.activos360.back.model.ModelApiResponse
import retrofit2.http.*
import retrofit2.Response

interface CampusControllerApi {
    /**
     * DELETE api/campus/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [ModelApiResponse]
     */
    @DELETE("api/campus/{id}")
    suspend fun deleteById7(@Path("id") id: kotlin.Long): Response<ModelApiResponse>

    /**
     * GET api/campus
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @return [ModelApiResponse]
     */
    @GET("api/campus")
    suspend fun findAll13(): Response<ModelApiResponse>

    /**
     * GET api/campus/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [ModelApiResponse]
     */
    @GET("api/campus/{id}")
    suspend fun findById13(@Path("id") id: kotlin.Long): Response<ModelApiResponse>

    /**
     * POST api/campus
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param campusDTO 
     * @return [ModelApiResponse]
     */
    @POST("api/campus")
    suspend fun save13(@Body campusDTO: CampusDTO): Response<ModelApiResponse>

    /**
     * PATCH api/campus/{id}/status
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [ModelApiResponse]
     */
    @PATCH("api/campus/{id}/status")
    suspend fun toggleStatus4(@Path("id") id: kotlin.Long): Response<ModelApiResponse>

    /**
     * PUT api/campus/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @param campusDTO 
     * @return [ModelApiResponse]
     */
    @PUT("api/campus/{id}")
    suspend fun update13(@Path("id") id: kotlin.Long, @Body campusDTO: CampusDTO): Response<ModelApiResponse>

}

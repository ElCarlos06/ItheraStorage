package com.example.activos360.back.api

import com.example.activos360.back.model.ModelApiResponse
import com.example.activos360.back.model.RegisterDTO

import retrofit2.http.*
import retrofit2.Response


interface RegisterControllerApi {
    /**
     * POST api/register/
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param registerDTO 
     * @return [ModelApiResponse]
     */
    @POST("api/register/")
    suspend fun register(@Body registerDTO: RegisterDTO): Response<ModelApiResponse>

    /**
     * POST api/register
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param registerDTO 
     * @return [ModelApiResponse]
     */
    @POST("api/register")
    suspend fun register1(@Body registerDTO: RegisterDTO): Response<ModelApiResponse>

}

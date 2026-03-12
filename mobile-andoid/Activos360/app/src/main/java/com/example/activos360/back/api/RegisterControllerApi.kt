package com.example.activos360.back.api

import org.openapitools.client.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.example.activos360.model.ModelApiResponse
import com.example.activos360.model.RegisterDTO

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

package com.example.activos360.back.api

import org.openapitools.client.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.example.activos360.model.ModelApiResponse
import com.example.activos360.model.RoleDTO

interface RoleControllerApi {
    /**
     * DELETE api/roles/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [ModelApiResponse]
     */
    @DELETE("api/roles/{id}")
    suspend fun deleteById1(@Path("id") id: kotlin.Long): Response<ModelApiResponse>

    /**
     * GET api/roles
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @return [ModelApiResponse]
     */
    @GET("api/roles")
    suspend fun findAll4(): Response<ModelApiResponse>

    /**
     * GET api/roles/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [ModelApiResponse]
     */
    @GET("api/roles/{id}")
    suspend fun findById4(@Path("id") id: kotlin.Long): Response<ModelApiResponse>

    /**
     * POST api/roles
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param roleDTO 
     * @return [ModelApiResponse]
     */
    @POST("api/roles")
    suspend fun save4(@Body roleDTO: RoleDTO): Response<ModelApiResponse>

    /**
     * PUT api/roles/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @param roleDTO 
     * @return [ModelApiResponse]
     */
    @PUT("api/roles/{id}")
    suspend fun update4(@Path("id") id: kotlin.Long, @Body roleDTO: RoleDTO): Response<ModelApiResponse>

}

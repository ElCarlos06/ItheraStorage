package com.example.activos360.back.api

import org.openapitools.client.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.example.activos360.model.ModelApiResponse
import com.example.activos360.model.UserDTO

interface UserControllerApi {
    /**
     * DELETE api/users/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [ModelApiResponse]
     */
    @DELETE("api/users/{id}")
    suspend fun delete(@Path("id") id: kotlin.Long): Response<ModelApiResponse>

    /**
     * GET api/users
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @return [ModelApiResponse]
     */
    @GET("api/users")
    suspend fun findAll(): Response<ModelApiResponse>

    /**
     * GET api/users/by-email
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param correo 
     * @return [ModelApiResponse]
     */
    @GET("api/users/by-email")
    suspend fun findByCorreo(@Query("correo") correo: kotlin.String): Response<ModelApiResponse>

    /**
     * GET api/users/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [ModelApiResponse]
     */
    @GET("api/users/{id}")
    suspend fun findById(@Path("id") id: kotlin.Long): Response<ModelApiResponse>

    /**
     * POST api/users
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param userDTO 
     * @return [ModelApiResponse]
     */
    @POST("api/users")
    suspend fun save(@Body userDTO: UserDTO): Response<ModelApiResponse>

    /**
     * PATCH api/users/{id}/status
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [ModelApiResponse]
     */
    @PATCH("api/users/{id}/status")
    suspend fun toggleStatus(@Path("id") id: kotlin.Long): Response<ModelApiResponse>

    /**
     * PUT api/users/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @param userDTO 
     * @return [ModelApiResponse]
     */
    @PUT("api/users/{id}")
    suspend fun update(@Path("id") id: kotlin.Long, @Body userDTO: UserDTO): Response<ModelApiResponse>

}

package com.example.activos360.back.api

import com.example.activos360.back.model.ModelApiResponse
import com.example.activos360.back.model.ResguardoDTO

import retrofit2.http.*
import retrofit2.Response


interface ResguardoControllerApi {
    /**
     * GET api/resguardos
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @return [ModelApiResponse]
     */
    @GET("api/resguardos")
    suspend fun findAll5(): Response<ModelApiResponse>

    /**
     * GET api/resguardos/activo/{activoId}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param activoId 
     * @return [ModelApiResponse]
     */
    @GET("api/resguardos/activo/{activoId}")
    suspend fun findByActivo(@Path("activoId") activoId: kotlin.Long): Response<ModelApiResponse>

    /**
     * GET api/resguardos/empleado/{userId}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param userId 
     * @return [ModelApiResponse]
     */
    @GET("api/resguardos/empleado/{userId}")
    suspend fun findByEmpleado(@Path("userId") userId: kotlin.Long): Response<ModelApiResponse>

    /**
     * GET api/resguardos/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [ModelApiResponse]
     */
    @GET("api/resguardos/{id}")
    suspend fun findById5(@Path("id") id: kotlin.Long): Response<ModelApiResponse>

    /**
     * POST api/resguardos
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param resguardoDTO 
     * @return [ModelApiResponse]
     */
    @POST("api/resguardos")
    suspend fun save5(@Body resguardoDTO: ResguardoDTO): Response<ModelApiResponse>

    /**
     * PUT api/resguardos/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @param resguardoDTO 
     * @return [ModelApiResponse]
     */
    @PUT("api/resguardos/{id}")
    suspend fun update5(@Path("id") id: kotlin.Long, @Body resguardoDTO: ResguardoDTO): Response<ModelApiResponse>

}

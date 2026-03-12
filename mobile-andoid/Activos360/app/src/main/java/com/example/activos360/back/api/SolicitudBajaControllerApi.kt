package com.example.activos360.back.api

import com.example.activos360.back.model.ModelApiResponse
import com.example.activos360.back.model.SolicitudBajaDTO

import retrofit2.http.*
import retrofit2.Response


interface SolicitudBajaControllerApi {
    /**
     * GET api/solicitudes-baja
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @return [ModelApiResponse]
     */
    @GET("api/solicitudes-baja")
    suspend fun findAll3(): Response<ModelApiResponse>

    /**
     * GET api/solicitudes-baja/estado/{estado}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param estado 
     * @return [ModelApiResponse]
     */
    @GET("api/solicitudes-baja/estado/{estado}")
    suspend fun findByEstado(@Path("estado") estado: kotlin.String): Response<ModelApiResponse>

    /**
     * GET api/solicitudes-baja/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [ModelApiResponse]
     */
    @GET("api/solicitudes-baja/{id}")
    suspend fun findById3(@Path("id") id: kotlin.Long): Response<ModelApiResponse>

    /**
     * POST api/solicitudes-baja
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param solicitudBajaDTO 
     * @return [ModelApiResponse]
     */
    @POST("api/solicitudes-baja")
    suspend fun save3(@Body solicitudBajaDTO: SolicitudBajaDTO): Response<ModelApiResponse>

    /**
     * PUT api/solicitudes-baja/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @param solicitudBajaDTO 
     * @return [ModelApiResponse]
     */
    @PUT("api/solicitudes-baja/{id}")
    suspend fun update3(@Path("id") id: kotlin.Long, @Body solicitudBajaDTO: SolicitudBajaDTO): Response<ModelApiResponse>

}

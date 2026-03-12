package com.example.activos360.back.api

import com.example.activos360.back.model.ModelApiResponse
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.example.activos360.back.model.BitacoraDTO


interface BitacoraControllerApi {
    /**
     * GET api/bitacora
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @return [ModelApiResponse]
     */
    @GET("api/bitacora")
    suspend fun findAll14(): Response<ModelApiResponse>

    /**
     * GET api/bitacora/activo/{activoId}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param activoId 
     * @return [ModelApiResponse]
     */
    @GET("api/bitacora/activo/{activoId}")
    suspend fun findByActivo3(@Path("activoId") activoId: kotlin.Long): Response<ModelApiResponse>

    /**
     * GET api/bitacora/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [ModelApiResponse]
     */
    @GET("api/bitacora/{id}")
    suspend fun findById16(@Path("id") id: kotlin.Long): Response<ModelApiResponse>

    /**
     * GET api/bitacora/usuario/{usuarioId}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param usuarioId 
     * @return [ModelApiResponse]
     */
    @GET("api/bitacora/usuario/{usuarioId}")
    suspend fun findByUsuario(@Path("usuarioId") usuarioId: kotlin.Long): Response<ModelApiResponse>

    /**
     * POST api/bitacora
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param bitacoraDTO 
     * @return [ModelApiResponse]
     */
    @POST("api/bitacora")
    suspend fun save14(@Body bitacoraDTO: BitacoraDTO): Response<ModelApiResponse>

}

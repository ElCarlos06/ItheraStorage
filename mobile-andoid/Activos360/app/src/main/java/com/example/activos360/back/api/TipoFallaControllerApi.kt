package com.example.activos360.back.api

import com.example.activos360.back.model.ModelApiResponse
import com.example.activos360.back.model.TipoFallaDTO
import retrofit2.http.*
import retrofit2.Response


interface TipoFallaControllerApi {
    /**
     * DELETE api/tipo-fallas/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [ModelApiResponse]
     */
    @DELETE("api/tipo-fallas/{id}")
    suspend fun deleteById(@Path("id") id: kotlin.Long): Response<ModelApiResponse>

    /**
     * GET api/tipo-fallas
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @return [ModelApiResponse]
     */
    @GET("api/tipo-fallas")
    suspend fun findAll1(): Response<ModelApiResponse>

    /**
     * GET api/tipo-fallas/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [ModelApiResponse]
     */
    @GET("api/tipo-fallas/{id}")
    suspend fun findById1(@Path("id") id: kotlin.Long): Response<ModelApiResponse>

    /**
     * POST api/tipo-fallas
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param tipoFallaDTO 
     * @return [ModelApiResponse]
     */
    @POST("api/tipo-fallas")
    suspend fun save1(@Body tipoFallaDTO: TipoFallaDTO): Response<ModelApiResponse>

    /**
     * PUT api/tipo-fallas/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @param tipoFallaDTO 
     * @return [ModelApiResponse]
     */
    @PUT("api/tipo-fallas/{id}")
    suspend fun update1(@Path("id") id: kotlin.Long, @Body tipoFallaDTO: TipoFallaDTO): Response<ModelApiResponse>

}

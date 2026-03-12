package com.example.activos360.back.api

import com.example.activos360.back.model.ModelApiResponse

import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.example.activos360.back.model.MantenimientoDTO

import com.example.activos360.back.model.SubirImagenRequest

interface MantenimientoControllerApi {
    /**
     * DELETE api/mantenimientos/imagenes/{imagenId}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param imagenId 
     * @return [ModelApiResponse]
     */
    @DELETE("api/mantenimientos/imagenes/{imagenId}")
    suspend fun eliminarImagen1(@Path("imagenId") imagenId: kotlin.Long): Response<ModelApiResponse>

    /**
     * GET api/mantenimientos
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @return [ModelApiResponse]
     */
    @GET("api/mantenimientos")
    suspend fun findAll10(): Response<ModelApiResponse>

    /**
     * GET api/mantenimientos/activo/{activoId}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param activoId 
     * @return [ModelApiResponse]
     */
    @GET("api/mantenimientos/activo/{activoId}")
    suspend fun findByActivo2(@Path("activoId") activoId: kotlin.Long): Response<ModelApiResponse>

    /**
     * GET api/mantenimientos/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [ModelApiResponse]
     */
    @GET("api/mantenimientos/{id}")
    suspend fun findById10(@Path("id") id: kotlin.Long): Response<ModelApiResponse>

    /**
     * GET api/mantenimientos/tecnico/{tecnicoId}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param tecnicoId 
     * @return [ModelApiResponse]
     */
    @GET("api/mantenimientos/tecnico/{tecnicoId}")
    suspend fun findByTecnico(@Path("tecnicoId") tecnicoId: kotlin.Long): Response<ModelApiResponse>

    /**
     * GET api/mantenimientos/{id}/imagenes
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [ModelApiResponse]
     */
    @GET("api/mantenimientos/{id}/imagenes")
    suspend fun listarImagenes1(@Path("id") id: kotlin.Long): Response<ModelApiResponse>

    /**
     * POST api/mantenimientos
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param mantenimientoDTO 
     * @return [ModelApiResponse]
     */
    @POST("api/mantenimientos")
    suspend fun save10(@Body mantenimientoDTO: MantenimientoDTO): Response<ModelApiResponse>

    /**
     * POST api/mantenimientos/{id}/imagenes
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @param subirImagenRequest  (optional)
     * @return [ModelApiResponse]
     */
    @POST("api/mantenimientos/{id}/imagenes")
    suspend fun subirImagen1(@Path("id") id: kotlin.Long, @Body subirImagenRequest: SubirImagenRequest? = null): Response<ModelApiResponse>

    /**
     * PUT api/mantenimientos/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @param mantenimientoDTO 
     * @return [ModelApiResponse]
     */
    @PUT("api/mantenimientos/{id}")
    suspend fun update10(@Path("id") id: kotlin.Long, @Body mantenimientoDTO: MantenimientoDTO): Response<ModelApiResponse>

}

package com.example.activos360.back.api

import com.example.activos360.back.model.ModelApiResponse
import com.example.activos360.back.model.ReporteDTO
import com.example.activos360.back.model.SubirImagenRequest

import retrofit2.http.*
import retrofit2.Response

interface ReporteControllerApi {
    /**
     * DELETE api/reportes/imagenes/{imagenId}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param imagenId 
     * @return [ModelApiResponse]
     */
    @DELETE("api/reportes/imagenes/{imagenId}")
    suspend fun eliminarImagen(@Path("imagenId") imagenId: kotlin.Long): Response<ModelApiResponse>

    /**
     * GET api/reportes
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @return [ModelApiResponse]
     */
    @GET("api/reportes")
    suspend fun findAll6(): Response<ModelApiResponse>

    /**
     * GET api/reportes/activo/{activoId}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param activoId 
     * @return [ModelApiResponse]
     */
    @GET("api/reportes/activo/{activoId}")
    suspend fun findByActivo1(@Path("activoId") activoId: kotlin.Long): Response<ModelApiResponse>

    /**
     * GET api/reportes/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [ModelApiResponse]
     */
    @GET("api/reportes/{id}")
    suspend fun findById6(@Path("id") id: kotlin.Long): Response<ModelApiResponse>

    /**
     * GET api/reportes/{id}/imagenes
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [ModelApiResponse]
     */
    @GET("api/reportes/{id}/imagenes")
    suspend fun listarImagenes(@Path("id") id: kotlin.Long): Response<ModelApiResponse>

    /**
     * POST api/reportes
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param reporteDTO 
     * @return [ModelApiResponse]
     */
    @POST("api/reportes")
    suspend fun save6(@Body reporteDTO: ReporteDTO): Response<ModelApiResponse>

    /**
     * POST api/reportes/{id}/imagenes
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @param subirImagenRequest  (optional)
     * @return [ModelApiResponse]
     */
    @POST("api/reportes/{id}/imagenes")
    suspend fun subirImagen(@Path("id") id: kotlin.Long, @Body subirImagenRequest: SubirImagenRequest? = null): Response<ModelApiResponse>

    /**
     * PUT api/reportes/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @param reporteDTO 
     * @return [ModelApiResponse]
     */
    @PUT("api/reportes/{id}")
    suspend fun update6(@Path("id") id: kotlin.Long, @Body reporteDTO: ReporteDTO): Response<ModelApiResponse>

}

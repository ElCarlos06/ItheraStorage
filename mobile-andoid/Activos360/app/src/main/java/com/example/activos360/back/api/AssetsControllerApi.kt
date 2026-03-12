package com.example.activos360.back.api

import org.openapitools.client.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.squareup.moshi.Json

import com.example.activos360.model.AssetsDTO
import com.example.activos360.model.ModelApiResponse
import com.example.activos360.model.SubirImagenRequest

interface AssetsControllerApi {
    /**
     * DELETE api/activos/imagenes/{imagenId}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param imagenId 
     * @return [ModelApiResponse]
     */
    @DELETE("api/activos/imagenes/{imagenId}")
    suspend fun eliminarImagen2(@Path("imagenId") imagenId: kotlin.Long): Response<ModelApiResponse>

    /**
     * GET api/activos
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @return [ModelApiResponse]
     */
    @GET("api/activos")
    suspend fun findAll16(): Response<ModelApiResponse>

    /**
     * GET api/activos/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [ModelApiResponse]
     */
    @GET("api/activos/{id}")
    suspend fun findById15(@Path("id") id: kotlin.Long): Response<ModelApiResponse>

    /**
     * GET api/activos/{id}/imagenes
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [ModelApiResponse]
     */
    @GET("api/activos/{id}/imagenes")
    suspend fun listarImagenes2(@Path("id") id: kotlin.Long): Response<ModelApiResponse>

    /**
     * POST api/activos
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param assetsDTO 
     * @return [ModelApiResponse]
     */
    @POST("api/activos")
    suspend fun save16(@Body assetsDTO: AssetsDTO): Response<ModelApiResponse>

    /**
     * POST api/activos/{id}/imagenes
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @param subirImagenRequest  (optional)
     * @return [ModelApiResponse]
     */
    @POST("api/activos/{id}/imagenes")
    suspend fun subirImagen2(@Path("id") id: kotlin.Long, @Body subirImagenRequest: SubirImagenRequest? = null): Response<ModelApiResponse>

    /**
     * PATCH api/activos/{id}/status
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @return [ModelApiResponse]
     */
    @PATCH("api/activos/{id}/status")
    suspend fun toggleStatus5(@Path("id") id: kotlin.Long): Response<ModelApiResponse>

    /**
     * PUT api/activos/{id}
     * 
     * 
     * Responses:
     *  - 200: OK
     *
     * @param id 
     * @param assetsDTO 
     * @return [ModelApiResponse]
     */
    @PUT("api/activos/{id}")
    suspend fun update15(@Path("id") id: kotlin.Long, @Body assetsDTO: AssetsDTO): Response<ModelApiResponse>

}

package com.example.activos360.back.api

import com.example.activos360.back.model.ModelApiResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ImagenPerfilControllerApi {

    @GET("api/imagen-perfil/{correo}")
    suspend fun obtenerImagen(
        @Path("correo") correo: String
    ): Response<ModelApiResponse>

    @Multipart
    @POST("api/imagen-perfil/{correo}")
    suspend fun subirImagen(
        @Path("correo") correo: String,
        @Part file: MultipartBody.Part
    ): Response<ModelApiResponse>

    @DELETE("api/imagen-perfil/{id}")
    suspend fun eliminarImagen(
        @Path("id") id: Long
    ): Response<ModelApiResponse>
}

package com.example.activos360.back.api

import com.example.activos360.back.model.ModelApiResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ImagenActivoControllerApi {
    @Multipart
    @POST("api/imagen-activo/{id}")
    suspend fun subirImagen(
        @Path("id") id: Long,
        @Part file: MultipartBody.Part
    ): Response<ModelApiResponse>
}

package com.example.activos360.core.repository

import android.content.Context
import android.net.Uri
import com.example.activos360.core.network.ApiProvider
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

object PerfilRepository {

    fun urlFromData(data: Any?): String? = when (data) {
        is Map<*, *> -> (data["urlCloudinary"] as? String)
            ?: (data["url"] as? String)
            ?: (data["urlImagen"] as? String)
        is String    -> data.takeIf { it.startsWith("http") }
        else         -> null
    }

    suspend fun obtenerFotoUrl(correo: String): String? = try {
        val resp = ApiProvider.imagenPerfilApi.obtenerImagen(correo)
        if (resp.isSuccessful) urlFromData(resp.body()?.data) else null
    } catch (_: Exception) { null }

    suspend fun subirFoto(correo: String, uri: Uri, context: Context): String? = try {
        val stream = context.contentResolver.openInputStream(uri) ?: return null
        val bytes = stream.readBytes(); stream.close()
        val body = bytes.toRequestBody("image/*".toMediaType())
        val part = MultipartBody.Part.createFormData("file", "perfil.jpg", body)
        val resp = ApiProvider.imagenPerfilApi.subirImagen(correo, part)
        if (resp.isSuccessful) urlFromData(resp.body()?.data) else null
    } catch (_: Exception) { null }
}

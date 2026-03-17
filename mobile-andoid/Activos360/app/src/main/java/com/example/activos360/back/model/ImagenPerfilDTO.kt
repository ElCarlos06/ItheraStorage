package com.example.activos360.back.model

// Este es el ApiResponse que te manda el backend
data class ImagenPerfilResponse(
    val message: String,
    val data: ImagenCloudinary?, // Aquí viene la info de la imagen
    val error: Boolean
)

// Esta es la clase que representa a "ImagenPerfil" / "BaseImagen"
data class ImagenCloudinary(
    val id: Long,
    val url: String // <-- ¡ESTO ES LO QUE NECESITAS PARA TU VISTA!
)
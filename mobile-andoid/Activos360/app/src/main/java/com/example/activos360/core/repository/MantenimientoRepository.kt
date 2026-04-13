package com.example.activos360.core.repository

import com.example.activos360.back.model.MantenimientoDTO
import com.example.activos360.core.network.ApiProvider
import com.example.activos360.core.util.asMap
import com.example.activos360.core.util.long
import com.example.activos360.core.util.string

object MantenimientoRepository {

    private fun unwrapList(data: Any?): List<Map<String, Any?>> = when (data) {
        is List<*>   -> data.filterIsInstance<Map<String, Any?>>()
        is Map<*, *> -> (data["content"] as? List<*>)
            ?.filterIsInstance<Map<String, Any?>>() ?: emptyList()
        else         -> emptyList()
    }

    suspend fun findByActivo(activoId: Long): List<Map<String, Any?>> {
        val resp = ApiProvider.mantenimientoApi.findByActivo2(activoId)
        return if (resp.isSuccessful) unwrapList(resp.body()?.data) else emptyList()
    }

    suspend fun findById(mantenimientoId: Long): Map<String, Any?>? {
        val resp = ApiProvider.mantenimientoApi.findById10(mantenimientoId)
        return if (resp.isSuccessful) resp.body()?.data.asMap() else null
    }

    suspend fun findByTecnico(tecnicoId: Long): List<Map<String, Any?>> {
        val resp = ApiProvider.mantenimientoApi.findByTecnico(tecnicoId)
        return if (resp.isSuccessful) unwrapList(resp.body()?.data) else emptyList()
    }

    /** Primer mantenimiento activo del técnico sobre el activo dado. */
    suspend fun findActivoParaTecnico(activoId: Long, userId: Long): Map<String, Any?>? =
        findByActivo(activoId).firstOrNull { m ->
            val tecnicoId = (m["usuarioTecnico"].asMap())?.long("id")
                ?: m.long("idUsuarioTecnico")
            val estado = m.string("estadoMantenimiento")?.lowercase() ?: ""
            val activo = estado !in listOf("finalizado", "completado", "cerrado", "cancelado")
            tecnicoId == userId && activo
        }

    suspend fun update(mantenimientoId: Long, dto: MantenimientoDTO) {
        val resp = ApiProvider.mantenimientoApi.update10(mantenimientoId, dto)
        if (!resp.isSuccessful)
            throw IllegalStateException("Error al actualizar mantenimiento (${resp.code()})")
    }
}

package com.example.activos360.core.repository

import com.example.activos360.back.model.ResguardoDTO
import com.example.activos360.core.network.ApiProvider
import com.example.activos360.core.util.asMap
import com.example.activos360.core.util.long
import com.example.activos360.core.util.string

object ResguardoRepository {

    private fun unwrapList(data: Any?): List<Map<String, Any?>> = when (data) {
        is List<*>   -> data.filterIsInstance<Map<String, Any?>>()
        is Map<*, *> -> (data["content"] as? List<*>)
            ?.filterIsInstance<Map<String, Any?>>() ?: emptyList()
        else         -> emptyList()
    }

    suspend fun findByActivo(activoId: Long): List<Map<String, Any?>> {
        val resp = ApiProvider.resguardoApi.findByActivo(activoId)
        return if (resp.isSuccessful) unwrapList(resp.body()?.data) else emptyList()
    }

    suspend fun findByEmpleado(userId: Long): List<Map<String, Any?>> {
        val resp = ApiProvider.resguardoApi.findByEmpleado(userId)
        return if (resp.isSuccessful) unwrapList(resp.body()?.data) else emptyList()
    }

    suspend fun findPendienteParaUsuario(activoId: Long, userId: Long): Map<String, Any?>? =
        findByActivo(activoId).firstOrNull { r ->
            val estado     = r.string("estadoResguardo")?.trim()?.lowercase()
            val empleadoId = (r["usuarioEmpleado"].asMap())?.long("id")
            estado == "pendiente" && empleadoId == userId
        }

    suspend fun findConfirmadoParaUsuario(activoId: Long, userId: Long): Map<String, Any?>? =
        findByActivo(activoId).firstOrNull { r ->
            val estado     = r.string("estadoResguardo")?.trim()?.lowercase()
            val empleadoId = (r["usuarioEmpleado"].asMap())?.long("id")
            (estado == "confirmado" || estado == "resguardado") && empleadoId == userId
        }

    suspend fun confirmar(resguardoId: Long, dto: ResguardoDTO) {
        val resp = ApiProvider.resguardoApi.update5(resguardoId, dto)
        if (!resp.isSuccessful)
            throw IllegalStateException("No se pudo confirmar el resguardo (${resp.code()})")
    }

    suspend fun devolver(resguardoId: Long, dto: ResguardoDTO) {
        val resp = ApiProvider.resguardoApi.update5(resguardoId, dto)
        if (!resp.isSuccessful)
            throw IllegalStateException("No se pudo procesar la devolución (${resp.code()})")
    }
}

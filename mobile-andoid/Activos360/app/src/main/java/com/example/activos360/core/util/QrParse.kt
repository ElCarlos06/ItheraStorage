package com.example.activos360.core.util

import com.example.activos360.core.repository.ActivoRepository
import org.json.JSONObject

object QrParse {

    /**
     * Comprueba si el texto escaneado tiene la estructura reconocida de un QR de activo
     * sin hacer ninguna llamada de red.
     *
     * Formatos válidos:
     *  - `{"v":2,"p":"<token>"}` — formato actual (v2 opaco)
     *  - `{"id": N}`             — formato legado
     *  - número puro             — formato legado simplificado
     */
    fun isActivoQrFormat(raw: String): Boolean {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return false
        if (trimmed.toLongOrNull() != null) return true
        return try {
            val json = JSONObject(trimmed)
            (json.optInt("v", 0) == 2 && json.has("p")) || json.has("id")
        } catch (_: Exception) { false }
    }

    /**
     * Extrae el id del activo de forma local (sin red) para formatos legados.
     * Devuelve null para el formato v2 opaco.
     */
    fun extractActivoId(raw: String): Long? {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return null
        try {
            val json = JSONObject(trimmed)
            if (json.optInt("v", 0) == 2 && json.has("p")) return null
            val id = json.optString("id", "").trim()
            if (id.isNotEmpty()) return id.toLongOrNull()
        } catch (_: Exception) { }
        trimmed.toLongOrNull()?.let { return it }
        return trimmed.filter { it.isDigit() }.toLongOrNull()
    }

    /**
     * Resuelve el id del activo: legado en cliente o llamando a ActivoRepository
     * para el token opaco v2.
     */
    suspend fun resolveActivoId(raw: String): Long? {
        extractActivoId(raw)?.let { return it }
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return null
        return try {
            val json = JSONObject(trimmed)
            if (json.optInt("v", 0) != 2) return null
            val p = json.optString("p", "").trim()
            if (p.isEmpty()) return null
            ActivoRepository.resolveQrPayload(p)
        } catch (_: Exception) { null }
    }
}

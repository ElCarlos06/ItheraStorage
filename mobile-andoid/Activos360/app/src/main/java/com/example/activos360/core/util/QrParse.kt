package com.example.activos360.core.util

import com.example.activos360.core.network.ApiProvider
import org.json.JSONObject

object QrParse {
    /**
     * Formato legado: JSON con [id], número solo, o texto con dígitos.
     * Formato seguro v2 (`{"v":2,"p":"..."}`) no devuelve id aquí; usa [resolveActivoId].
     */
    fun extractActivoId(raw: String): Long? {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return null

        try {
            val json = JSONObject(trimmed)
            if (json.optInt("v", 0) == 2 && json.has("p")) return null
            val id = json.optString("id", "").trim()
            if (id.isNotEmpty()) return id.toLongOrNull()
        } catch (_: Exception) {
        }

        trimmed.toLongOrNull()?.let { return it }

        val digits = trimmed.filter { it.isDigit() }
        return digits.toLongOrNull()
    }

    /**
     * Resuelve el id del activo: legado en cliente o llamando a [GET /api/activos/qr/resolver] para el token opaco.
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
            val resp = ApiProvider.assetsApi.resolveQrPayload(p)
            if (!resp.isSuccessful) return null
            val data = resp.body()?.`data` ?: return null
            data.asMap()?.long("id")
        } catch (_: Exception) {
            null
        }
    }
}

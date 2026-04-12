package com.example.activos360.core.util

import com.example.activos360.core.network.ApiProvider
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
     *
     * Cualquier otro texto (URL, texto libre, QR de otro sistema) devuelve false.
     */
    fun isActivoQrFormat(raw: String): Boolean {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return false
        if (trimmed.toLongOrNull() != null) return true      // número puro
        return try {
            val json = JSONObject(trimmed)
            (json.optInt("v", 0) == 2 && json.has("p"))     // v2: {"v":2,"p":"..."}
                    || json.has("id")                         // legado: {"id": N}
        } catch (_: Exception) {
            false
        }
    }

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

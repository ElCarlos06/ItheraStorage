package com.example.activos360.core.util

import org.json.JSONObject

object QrParse {
    /**
     * Algunos QR llegan como JSON (ej {"id":123}) o como texto con dígitos.
     * Regresa el id del activo si se puede extraer.
     */
    fun extractActivoId(raw: String): Long? {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return null

        // Caso 1: viene JSON
        try {
            val json = JSONObject(trimmed)
            val id = json.optString("id", "").trim()
            if (id.isNotEmpty()) return id.toLongOrNull()
        } catch (_: Exception) {
            // ignore
        }

        // Caso 2: viene directo como número
        trimmed.toLongOrNull()?.let { return it }

        // Caso 3: viene con texto, extraemos dígitos
        val digits = trimmed.filter { it.isDigit() }
        return digits.toLongOrNull()
    }
}


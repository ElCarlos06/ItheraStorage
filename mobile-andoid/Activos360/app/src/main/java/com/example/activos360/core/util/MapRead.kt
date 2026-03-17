package com.example.activos360.core.util

@Suppress("UNCHECKED_CAST")
fun Any?.asMap(): Map<String, Any?>? = this as? Map<String, Any?>

@Suppress("UNCHECKED_CAST")
fun Any?.asListOfMaps(): List<Map<String, Any?>>? = this as? List<Map<String, Any?>>

fun Map<String, Any?>.string(key: String): String? = this[key] as? String

fun Map<String, Any?>.long(key: String): Long? {
    val v = this[key] ?: return null
    return when (v) {
        is Number -> v.toLong()
        is String -> v.toLongOrNull()
        else -> null
    }
}

fun Map<String, Any?>.bool(key: String): Boolean? {
    val v = this[key] ?: return null
    return when (v) {
        is Boolean -> v
        is String -> v.toBooleanStrictOrNull()
        else -> null
    }
}


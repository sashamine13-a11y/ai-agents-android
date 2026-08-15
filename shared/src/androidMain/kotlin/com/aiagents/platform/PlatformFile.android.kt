package com.aiagents.platform

import java.io.File
import android.content.Context

// Simple Android file ops using app filesDir; requires passing Context where used.
actual object PlatformFile {
    lateinit var appContext: Context

    actual fun readText(path: String): String? = try {
        val file = File(appContext.filesDir, path)
        if (!file.exists()) return null
        file.readText()
    } catch (e: Exception) { null }

    actual fun writeText(path: String, content: String): Boolean = try {
        val file = File(appContext.filesDir, path)
        file.parentFile?.mkdirs()
        file.writeText(content); true
    } catch (e: Exception) { false }

    actual fun listDir(path: String): List<String> = try {
        val dir = File(appContext.filesDir, path)
        dir.list()?.toList() ?: emptyList()
    } catch (e: Exception) { emptyList() }
}

package com.aiagents.platform

import java.io.File

actual object PlatformFile {
    actual fun readText(path: String): String? = try { File(path).readText() } catch (e: Exception) { null }
    actual fun writeText(path: String, content: String): Boolean = try { File(path).writeText(content); true } catch (e: Exception) { false }
    actual fun listDir(path: String): List<String> = try { File(path).list()?.toList() ?: emptyList() } catch (e: Exception) { emptyList() }
}

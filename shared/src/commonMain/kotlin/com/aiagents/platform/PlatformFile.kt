package com.aiagents.platform

expect object PlatformFile {
    fun readText(path: String): String?
    fun writeText(path: String, content: String): Boolean
    fun listDir(path: String): List<String>
}

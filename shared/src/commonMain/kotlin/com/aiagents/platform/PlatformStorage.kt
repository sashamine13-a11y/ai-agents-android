package com.aiagents.platform

import com.aiagents.data.model.ApiKeys

expect object PlatformStorage {
    fun init(context: Any?)
    suspend fun loadApiKeys(): ApiKeys
    suspend fun saveApiKeys(keys: ApiKeys)
}

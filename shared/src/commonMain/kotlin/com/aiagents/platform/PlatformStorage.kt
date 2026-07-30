package com.aiagents.platform

import com.aiagents.data.model.ApiKeys

expect object PlatformStorage {
    fun loadApiKeys(): ApiKeys
    fun saveApiKeys(keys: ApiKeys)
}

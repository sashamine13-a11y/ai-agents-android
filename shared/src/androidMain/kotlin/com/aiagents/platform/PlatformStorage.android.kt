package com.aiagents.platform

import com.aiagents.data.model.ApiKeys

actual object PlatformStorage {
    actual fun loadApiKeys(): ApiKeys = ApiKeys()
    actual fun saveApiKeys(keys: ApiKeys) { /* no-op for now */ }
}

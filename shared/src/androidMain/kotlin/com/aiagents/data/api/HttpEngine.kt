package com.aiagents.data.api

import io.ktor.client.engine.okhttp.*
import io.ktor.client.engine.*

actual val httpClientEngineFactory: HttpClientEngineFactory<*> = OkHttp

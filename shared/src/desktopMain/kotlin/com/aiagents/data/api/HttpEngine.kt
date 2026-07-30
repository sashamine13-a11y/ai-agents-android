package com.aiagents.data.api

import io.ktor.client.engine.cio.*
import io.ktor.client.engine.*

actual val httpClientEngineFactory: HttpClientEngineFactory<*> = CIO

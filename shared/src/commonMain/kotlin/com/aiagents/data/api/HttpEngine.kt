package com.aiagents.data.api

import io.ktor.client.engine.*

expect val httpClientEngineFactory: HttpClientEngineFactory<*>

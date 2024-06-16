package com.bidyut.tech.bhandar.example

import com.bidyut.tech.bhandar.Bhandar
import com.bidyut.tech.bhandar.DataFetcher
import com.bidyut.tech.bhandar.Storage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.flow.MutableStateFlow

class IpRepository(
    httpClient: HttpClient,
    memoryStore: MutableStateFlow<String?>
) : Bhandar<Int, String>(
    fetcher = DataFetcher.of(
        fetch = { request ->
            println("$request: Fetching from network...")
            try {
                val response: HttpResponse = httpClient.get("http://ip-api.com/line/?fields=query")
                when (response.status) {
                    HttpStatusCode.OK -> Result.success(response.body())
                    else -> Result.failure(IllegalStateException(response.body() as String))
                }
            } catch (e: IOException) {
                Result.failure(e)
            }
        }
    ),
    storage = Storage.of(
        read = { request ->
            println("$request: Reading from local...")
            memoryStore
        },
        isValid = { it?.isNotBlank() == true },
        write = { request, newValue ->
            println("$request: Writing $newValue to local...")
            memoryStore.emit(newValue)
        },
    ),
)

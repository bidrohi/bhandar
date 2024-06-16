package com.bidyut.tech.bhandar

import app.cash.turbine.test
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.milliseconds

class BhandarTest {
    @Test
    fun canCreateRepositoryWithOnlyFetcher() = runTest {
        // Given
        val repository = Bhandar<Int, String>(
            fetcher = DataFetcher.of { request ->
                delay(50.milliseconds)
                Result.success(request.toString())
            }
        )

        // When
        repository.fresh(1337).test {
            // Then
            assertIs<ReadResult.Loading<*>>(awaitItem())
            val result = awaitItem()
            assertIs<ReadResult.Data<*>>(result)
            assertEquals("1337", result.data)
            awaitComplete()
        }

        // When
        repository.cached(1412).test {
            // Then
            assertIs<ReadResult.Loading<*>>(awaitItem())
            val result = awaitItem()
            assertIs<ReadResult.Data<*>>(result)
            assertEquals("1412", result.data)
            awaitComplete()
        }

        // When
        repository.cached(1412, refresh = false).test {
            // Then
            assertIs<ReadResult.Error<*>>(awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun canCreateRepositoryWithSuccessfulFetcher() = runTest {
        // Given
        val memoryStore = mutableMapOf<Int, MutableStateFlow<String?>>()
        fun getMemoryStore(request: Int) = memoryStore.getOrPut(request) {
            MutableStateFlow(null)
        }

        val repository = Bhandar<Int, String>(
            fetcher = DataFetcher.of { request ->
                delay(50.milliseconds)
                Result.success(request.toString())
            },
            storage = Storage.of(
                reader = StorageReader.of { request ->
                    getMemoryStore(request)
                },
                writer = StorageWriter.of { request, newValue ->
                    getMemoryStore(request).emit("store+$newValue")
                },
            )
        )

        // When
        repository.fresh(1337).test {
            // Then
            assertIs<ReadResult.Loading<*>>(awaitItem())
            val result = awaitItem()
            assertIs<ReadResult.Data<*>>(result)
            assertEquals("1337", result.data)
        }

        // When
        repository.cached(1337).test {
            // Then
            val cacheResult = awaitItem()
            assertIs<ReadResult.Cache<*>>(cacheResult)
            assertEquals("store+1337", cacheResult.data)
            val result = awaitItem()
            assertIs<ReadResult.Data<*>>(result)
            assertEquals("1337", result.data)
        }

        // When
        repository.cached(1412, refresh = false).test {
            // Then
            assertIs<ReadResult.Loading<*>>(awaitItem())
            val result = awaitItem()
            assertIs<ReadResult.Data<*>>(result)
            assertEquals("1412", result.data)
        }
    }

    @Test
    fun canCreateRepositoryWithBrokenFetcher() = runTest {
        // Given
        val memoryStore = mutableMapOf<Int, MutableStateFlow<String?>>()
        fun getMemoryStore(request: Int) = memoryStore.getOrPut(request) {
            MutableStateFlow(null)
        }

        val repository = Bhandar<Int, String>(
            fetcher = DataFetcher.of(
                isValid = { it != "broken" },
            ) { request ->
                delay(50.milliseconds)
                if (request == 1971) {
                    Result.success("broken")
                } else {
                    Result.failure(IllegalStateException("Forced fetcher error on $request"))
                }
            },
            storage = Storage.of(
                read = { request ->
                    getMemoryStore(request)
                },
                write = { request, newValue ->
                    getMemoryStore(request).emit("store+$newValue")
                },
            )
        )

        // When
        repository.fresh(1337).test {
            // Then
            assertIs<ReadResult.Loading<*>>(awaitItem())
            val result = awaitItem()
            assertIs<ReadResult.Error<*>>(result)
            assertNull(result.prev)
        }

        // When
        repository.cached(1337).test {
            // Then
            assertIs<ReadResult.Loading<*>>(awaitItem())
            val result = awaitItem()
            assertIs<ReadResult.Error<*>>(result)
            assertNull(result.prev)
        }

        // When
        getMemoryStore(1412).emit("store+1412")
        repository.cached(1412).test {
            // Then
            val cacheResult = awaitItem()
            assertIs<ReadResult.Cache<*>>(cacheResult)
            assertEquals("store+1412", cacheResult.data)
            val result = awaitItem()
            assertIs<ReadResult.Error<*>>(result)
            assertEquals("store+1412", result.prev)
        }

        // When
        repository.cached(1971, refresh = false).test {
            // Then
            assertIs<ReadResult.Loading<*>>(awaitItem())
            val result = awaitItem()
            assertIs<ReadResult.Error<*>>(result)
            assertNull(result.prev)
        }
    }

    @Test
    fun canCreateRepositoryWithBrokenStorage() = runTest {
        // Given
        val repository = Bhandar<Int, String>(
            fetcher = DataFetcher.of { request ->
                delay(50.milliseconds)
                Result.success(request.toString())
            },
            storage = Storage.of(
                isValid = { it != null && it != "broken" },
                read = { request ->
                    if (request == 1412) {
                        flow { emit("broken") }
                    } else {
                        flow { emit(null) }
                    }
                },
                write = { _, _ -> },
            )
        )

        // When
        repository.fresh(1337).test {
            // Then
            assertIs<ReadResult.Loading<*>>(awaitItem())
            val result = awaitItem()
            assertIs<ReadResult.Data<*>>(result)
            assertEquals("1337", result.data)
            awaitComplete()
        }

        // When
        repository.cached(1337, refresh = false).test {
            // Then
            assertIs<ReadResult.Loading<*>>(awaitItem())
            val result = awaitItem()
            assertIs<ReadResult.Data<*>>(result)
            assertEquals("1337", result.data)
            awaitComplete()
        }

        // When
        repository.cached(1971, refresh = true).test {
            // Then
            assertIs<ReadResult.Loading<*>>(awaitItem())
            val result = awaitItem()
            assertIs<ReadResult.Data<*>>(result)
            assertEquals("1971", result.data)
            awaitComplete()
        }

        // When
        repository.cached(1412, fetchIfMissing = false, refresh = false).test {
            // Then
            assertIs<ReadResult.Loading<*>>(awaitItem())
            val result = awaitItem()
            assertIs<ReadResult.Error<*>>(result)
            assertNull(result.prev)
            awaitComplete()
        }
    }
}

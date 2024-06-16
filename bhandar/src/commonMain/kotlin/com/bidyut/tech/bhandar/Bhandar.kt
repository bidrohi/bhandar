package com.bidyut.tech.bhandar

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

open class Bhandar<Request, Model>(
    private val fetcher: DataFetcher<Request, Model>,
    private val storage: Storage<Request, Model>? = null,
) {
    fun fresh(
        request: Request,
    ) = fetcherWithFallback(request, allowCacheFirst = false)

    fun cached(
        request: Request,
        refresh: Boolean = true,
        fetchIfMissing: Boolean = true,
    ) = if (refresh) {
        fetcherWithFallback(request, allowCacheFirst = true)
    } else if (storage != null) {
        if (fetchIfMissing) {
            cacheWithFallback(request)
        } else {
            cacheOnly(request)
        }
    } else flow {
        emit(ReadResult.Error("Storage is not available"))
    }

    private fun fetcherOnly(
        request: Request,
    ) = flow<ReadResult<Model>> {
        emit(ReadResult.Loading())
        fetcher.fetch(request).fold(
            onSuccess = {
                if (fetcher.isValid(it)) {
                    emit(ReadResult.Success(it))
                } else {
                    emit(ReadResult.Error("Data from fetcher is not valid"))
                }
            },
            onFailure = {
                emit(ReadResult.Error(it.message ?: "Unknown error"))
            }
        )
    }.distinctUntilChanged()

    private fun cacheOnly(
        request: Request,
    ) = flow<ReadResult<Model>> {
        emit(ReadResult.Loading())
        storage?.read(request)?.map {
            if (storage.isValid(it)) {
                ReadResult.Cache(it)
            } else {
                ReadResult.Error("Data in storage is not valid")
            }
        }?.let {
            emitAll(it)
        } ?: emit(ReadResult.Error("Storage is not available"))
    }.distinctUntilChanged()

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun cacheWithFallback(
        request: Request,
    ) = cacheOnly(request)
        .flatMapConcat { cacheResult ->
            when (cacheResult) {
                is ReadResult.Error -> fetcherWithFallback(request, allowCacheFirst = false)
                else -> flow { emit(cacheResult) }
            }
        }.distinctUntilChanged()

    private fun fetcherWithFallback(
        request: Request,
        allowCacheFirst: Boolean,
    ) = fetcherOnly(request)
        .combine(cacheOnly(request)) { fetcherResult, cacheResult ->
            when (fetcherResult) {
                is ReadResult.Loading -> if (
                    allowCacheFirst && cacheResult is ReadResult.Cache
                    && storage?.isValid(cacheResult.data) == true
                ) {
                    cacheResult
                } else {
                    fetcherResult
                }

                is ReadResult.Error -> if (
                    cacheResult is ReadResult.Cache
                    && storage?.isValid(cacheResult.data) == true
                ) {
                    ReadResult.Error(
                        fetcherResult.errorMessage,
                        cacheResult.data
                    )
                } else {
                    fetcherResult
                }

                is ReadResult.Success -> {
                    storage?.write(request, fetcherResult.data)
                    fetcherResult
                }

                else -> fetcherResult
            }
        }.distinctUntilChanged()
}

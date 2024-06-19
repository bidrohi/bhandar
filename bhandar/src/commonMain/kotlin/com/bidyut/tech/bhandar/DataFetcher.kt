package com.bidyut.tech.bhandar

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface DataFetcher<Request, Model> {
    fun fetch(
        request: Request,
    ): Flow<Result<Model>>

    fun isValid(
        data: Model?,
    ): Boolean = data != null

    companion object {
        fun <Request, Model> ofFlow(
            isValid: (Model?) -> Boolean = { it != null },
            fetch: (Request) -> Flow<Result<Model>>,
        ) = object : DataFetcher<Request, Model> {
            override fun fetch(
                request: Request,
            ) = fetch(request)

            override fun isValid(
                data: Model?,
            ) = isValid(data)
        }

        fun <Request, Model> of(
            isValid: (Model?) -> Boolean = { it != null },
            fetch: suspend (Request) -> Result<Model>,
        ) = object : DataFetcher<Request, Model> {
            override fun fetch(
                request: Request,
            ) = flow { emit(fetch(request)) }

            override fun isValid(
                data: Model?,
            ) = isValid(data)
        }
    }
}

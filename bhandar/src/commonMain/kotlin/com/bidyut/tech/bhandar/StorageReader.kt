package com.bidyut.tech.bhandar

import kotlinx.coroutines.flow.Flow

interface StorageReader<Request, Model> {
    fun read(
        request: Request,
    ): Flow<Model?>

    fun isValid(
        data: Model?,
    ): Boolean = data != null

    companion object {
        fun <Request, Model> of(
            isValid: (Model?) -> Boolean = { it != null },
            read: (Request) -> Flow<Model?>,
        ) = object : StorageReader<Request, Model> {
            override fun read(
                request: Request,
            ) = read(request)

            override fun isValid(
                data: Model?,
            ) = isValid(data)
        }
    }
}

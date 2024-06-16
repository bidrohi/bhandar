package com.bidyut.tech.bhandar

interface StorageWriter<Request, Model> {
    suspend fun write(
        request: Request,
        data: Model,
    )

    companion object {
        fun <Request, Model> of(
            write: suspend (Request, Model) -> Unit,
        ) = object : StorageWriter<Request, Model> {
            override suspend fun write(
                request: Request,
                data: Model,
            ) = write(request, data)
        }
    }
}

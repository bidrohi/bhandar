package com.bidyut.tech.bhandar

import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

interface StorageWriter<Request, Model> {
    suspend fun write(
        request: Request,
        data: Model,
    )

    @OptIn(ExperimentalObjCRefinement::class)
    @HiddenFromObjC
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

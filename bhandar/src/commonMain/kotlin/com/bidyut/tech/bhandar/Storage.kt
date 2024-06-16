package com.bidyut.tech.bhandar

import kotlinx.coroutines.flow.Flow

interface Storage<Request, Model> : StorageReader<Request, Model>, StorageWriter<Request, Model> {
    companion object {
        fun <Request, Model> of(
            reader: StorageReader<Request, Model>,
            writer: StorageWriter<Request, Model>,
        ): Storage<Request, Model> = object : Storage<Request, Model>,
            StorageReader<Request, Model> by reader,
            StorageWriter<Request, Model> by writer {}

        fun <Request, Model> of(
            isValid: (Model?) -> Boolean = { it != null },
            read: (Request) -> Flow<Model?>,
            write: suspend (Request, Model) -> Unit,
        ): Storage<Request, Model> = of(
            reader = StorageReader.of(isValid, read),
            writer = StorageWriter.of(write),
        )
    }
}

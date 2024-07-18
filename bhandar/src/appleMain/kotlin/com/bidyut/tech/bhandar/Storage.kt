package com.bidyut.tech.bhandar

import kotlinx.coroutines.flow.Flow

fun <Request, Model> of(
    reader: StorageReader<Request, Model>,
    writer: StorageWriter<Request, Model>,
) = Storage.of(reader, writer)

fun <Request, Model> of(
    isValid: (Model?) -> Boolean,
    read: (Request) -> Flow<Model?>,
    write: (Request, Model) -> Unit,
) = Storage.of(isValid, read, write)

fun <Request, Model> of(
    isValid: (Model?) -> Boolean,
    read: (Request) -> ModelResult<Model>,
    write: (Request, Model) -> Unit,
) = Storage.of(isValid, read = { request ->
    read(request).toFlow()
}, write)

fun <Request, Model> of(
    read: (Request) -> Flow<Model?>,
    write: (Request, Model) -> Unit,
) = of({ it != null }, read, write)

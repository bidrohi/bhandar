package com.bidyut.tech.bhandar

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.experimental.ExperimentalObjCName

@OptIn(ExperimentalObjCName::class)
class ModelResult<Model>(
    private val data: Model?,
    private val error: Throwable?,
) {
    constructor(
        @ObjCName("_") data: Model,
    ) : this(data, null)

    constructor(
        error: Throwable
    ) : this(null, error)

    fun toResult() = if (error == null) {
        Result.success(data)
    } else {
        Result.failure(error)
    }

    fun toFlow(): Flow<Model?> = flow {
        if (error != null) {
            throw error
        }
        emit(data)
    }
}

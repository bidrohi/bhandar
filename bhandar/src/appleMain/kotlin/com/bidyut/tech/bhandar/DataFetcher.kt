@file:OptIn(ExperimentalObjCName::class)

package com.bidyut.tech.bhandar

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.experimental.ExperimentalObjCName

fun <Request, Model> ofFlow(
    isValid: (Model?) -> Boolean,
    fetch: (Request) -> Flow<ModelResult<Model>>,
) = DataFetcher.ofFlow(isValid, fetch = { request: Request ->
    fetch(request).map { it.toResult() }
})

fun <Request, Model> ofFlow(
    @ObjCName("_") fetch: (Request) -> Flow<ModelResult<Model>>,
) = ofFlow({ it != null }, fetch)

fun <Request, Model> of(
    isValid: (Model?) -> Boolean,
    fetch: (Request) -> ModelResult<Model>,
) = DataFetcher.of(isValid, fetch = { request: Request ->
    fetch(request).toResult()
})

fun <Request, Model> of(
    @ObjCName("_") fetch: (Request) -> ModelResult<Model>,
) = of({ it != null }, fetch)

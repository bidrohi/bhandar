@file:OptIn(ExperimentalObjCName::class)

package com.bidyut.tech.bhandar

import kotlinx.coroutines.flow.Flow
import kotlin.experimental.ExperimentalObjCName

fun <Request, Model> of(
    isValid: (Model?) -> Boolean,
    read: (Request) -> Flow<Model?>,
) = StorageReader.of(isValid, read)

fun <Request, Model> of(
    @ObjCName("_") read: (Request) -> Flow<Model?>,
) = StorageReader.of({ it != null }, read)

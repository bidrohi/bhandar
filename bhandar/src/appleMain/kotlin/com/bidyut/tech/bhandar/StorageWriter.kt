@file:OptIn(ExperimentalObjCName::class)

package com.bidyut.tech.bhandar

import kotlin.experimental.ExperimentalObjCName

fun <Request, Model> of(
    @ObjCName("_") write: (Request, Model) -> Unit,
) = StorageWriter.of(write)

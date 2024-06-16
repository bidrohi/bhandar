package com.bidyut.tech.bhandar

sealed interface ReadResult<out DataModel> {
    data class Loading<out DataModel>(
        val data: DataModel? = null,
    ) : ReadResult<DataModel>

    data class Error<out DataModel>(
        val errorMessage: String,
        val prev: DataModel? = null,
    ) : ReadResult<DataModel>

    interface Data<out DataModel> : ReadResult<DataModel> {
        val data: DataModel?
    }

    data class Cache<out DataModel>(
        override val data: DataModel?,
    ) : Data<DataModel>

    data class Success<out DataModel>(
        override val data: DataModel,
    ) : Data<DataModel>
}

package com.bidyut.tech.bhandar

interface DataFetcher<Request, Model> {
    suspend fun fetch(
        request: Request,
    ): Result<Model>

    fun isValid(
        data: Model?,
    ): Boolean = data != null

    companion object {
        fun <Request, Model> of(
            isValid: (Model?) -> Boolean = { it != null },
            fetch: suspend (Request) -> Result<Model>
        ) = object : DataFetcher<Request, Model> {
            override suspend fun fetch(
                request: Request,
            ) = fetch(request)

            override fun isValid(
                data: Model?,
            ) = isValid(data)
        }
    }
}

package com.bidyut.tech.bhandar.example

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow

class MainViewModel(
    private val repository: IpRepository
) : ViewModel() {

    val mode = MutableStateFlow(FetchMode.NONE)

    @OptIn(ExperimentalCoroutinesApi::class)
    val ipResult = mode.flatMapMerge { mode ->
        println("Fetching IP: $mode")
        when (mode) {
            FetchMode.NONE -> flow { }
            FetchMode.CACHE_ONLY -> repository.cached(1, refresh = false, fetchIfMissing = false)
            FetchMode.CACHE_WITH_FALLBACK -> repository.cached(2, refresh = false)
            FetchMode.CACHE_REFRESH -> repository.cached(3, refresh = true)
            FetchMode.FRESH -> repository.fresh(4)
        }
    }

    enum class FetchMode {
        NONE,
        CACHE_ONLY,
        CACHE_WITH_FALLBACK,
        CACHE_REFRESH,
        FRESH
    }
}

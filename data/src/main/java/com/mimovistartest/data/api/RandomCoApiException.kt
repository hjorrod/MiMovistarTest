package com.mimovistartest.data.api

 data class RandomCoApiException(val errorMessage: String? = null) : Exception() {

    companion object {
        const val EMPTY_RESULT = "EMPTY_RESULT"
        const val UNKNOWN = "UNKNOWN"
    }
}
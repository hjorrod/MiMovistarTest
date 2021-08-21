package com.mimovistartest.data.util

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData

class InternetConnectionObserver private constructor() : LiveData<InternetConnectionEvent<InternetConnectionResponse>>() {

    fun setResponseValue(value: InternetConnectionResponse) {
        if (this.hasActiveObservers())
            super.setValue(InternetConnectionEvent((value)))
    }

    companion object {
        private lateinit var sInstance: InternetConnectionObserver

        @MainThread
        fun get(): InternetConnectionObserver {
            sInstance = if (::sInstance.isInitialized)
                sInstance
            else
                InternetConnectionObserver()
            return sInstance
        }
    }
}

sealed class InternetConnectionResponse(val message: String) {
    class LossConnection : InternetConnectionResponse("An error connection has occurred")
}
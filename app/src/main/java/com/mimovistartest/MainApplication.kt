package com.mimovistartest

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import dev.skomlach.biometric.compat.BiometricPromptCompat

@HiltAndroidApp
open class MainApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        initFirebase()
        BiometricPromptCompat.Companion.init(null)
    }

    private fun initFirebase() {
        FirebaseApp.initializeApp(this)
    }
}
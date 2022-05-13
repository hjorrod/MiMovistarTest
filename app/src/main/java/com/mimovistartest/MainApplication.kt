package com.mimovistartest

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.mimovistartest.data.di.*
import dagger.hilt.android.HiltAndroidApp
import dev.skomlach.biometric.compat.BiometricPromptCompat
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin

@HiltAndroidApp
open class MainApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        /*startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            val moduleList = listOf(
                appModule,
                databaseModule,
                networkModule,
                repositoryModule,
                remoteModule,
                useCaseModule,
                viewModelModule
            )
            loadKoinModules(moduleList)
        }*/
        initFirebase()
        BiometricPromptCompat.Companion.init(null)
    }

    private fun initFirebase() {
        Log.d("elfoco", " init Firebase")
        FirebaseApp.initializeApp(this)
    }
}
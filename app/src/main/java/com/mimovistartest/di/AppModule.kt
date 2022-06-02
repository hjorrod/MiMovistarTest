package com.mimovistartest.di

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mimovistartest.util.FirebaseTest
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideFirebase(
        firebaseStorage: FirebaseStorage,
        firebaseFirestore: FirebaseFirestore,
        firebaseDatabase: FirebaseDatabase
    ): FirebaseTest = FirebaseTest(firebaseStorage, firebaseFirestore, firebaseDatabase)

    @Singleton
    @Provides
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Singleton
    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Singleton
    @Provides
    fun provideFirebaseDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance()
}

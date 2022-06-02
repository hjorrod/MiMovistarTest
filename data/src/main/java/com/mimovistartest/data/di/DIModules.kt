package com.mimovistartest.data.di

import android.app.Application
import androidx.room.Room
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mimovistartest.data.BuildConfig
import com.mimovistartest.data.api.RandomCoApi
import com.mimovistartest.data.repository.IUsersRepository
import com.mimovistartest.data.repository.UsersRepository
import com.mimovistartest.data.repository.local.AppDatabase
import com.mimovistartest.data.repository.local.ILocalDataSource
import com.mimovistartest.data.repository.local.impl.LocalDataSource
import com.mimovistartest.data.repository.remote.IUsersRemoteDataSource
import com.mimovistartest.data.repository.remote.impl.UsersRemoteDataSource
import com.mimovistartest.data.util.CustomInterceptor
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideAppDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            "radom_co_database"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideDaoModule(
        appDatabase: AppDatabase
    ) = LocalDataSource(appDatabase.userDao())

    @Singleton
    @Provides
    fun provideHttpInterceptorOkHttpClient(
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .addInterceptor(CustomInterceptor())
            .retryOnConnectionFailure(true).build()
    }

    @Singleton
    @Provides
    fun provideRandomCoApi(): RandomCoApi = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RandomCoApi::class.java)

    @Module
    @InstallIn(SingletonComponent::class)
    interface RepositoryBindings {
        @Binds
        fun bindUserRepositoryModule(impl: UsersRepository): IUsersRepository

        @Binds
        fun bindLocalModule(impl: LocalDataSource): ILocalDataSource

        @Binds
        fun bindRemoteModule(impl: UsersRemoteDataSource): IUsersRemoteDataSource
    }
}
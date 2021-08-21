package com.mimovistartest.data.di

import androidx.room.Room
import com.google.gson.Gson
import com.mimovistartest.data.api.RandomCoApi
import com.mimovistartest.data.repository.local.AppDatabase
import com.mimovistartest.data.repository.remote.IUsersRemoteDataSource
import com.mimovistartest.data.repository.IUsersRepository
import com.mimovistartest.data.repository.local.ILocalDataSource
import com.mimovistartest.data.repository.remote.impl.UsersRemoteDataSource
import com.mimovistartest.data.repository.UsersRepository
import com.mimovistartest.data.repository.local.impl.LocalDataSource
import com.mimovistartest.data.util.CustomInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {

    /*** OkHTTP Singleton ***/
    single { OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
        .addInterceptor(get<CustomInterceptor>())
        .retryOnConnectionFailure(true).build() }

    /*** Interceptor ***/
    single { CustomInterceptor() }

    /** GSON instance **/
    single { Gson() }
}

val repositoryModule = module {
    factory { UsersRepository(get(), get()) as IUsersRepository }
}

val remoteModule = module {
    factory { UsersRemoteDataSource(get()) as IUsersRemoteDataSource }
}

// ---- Retrofit instance ----
private const val randomCo = "randomCo"

val networkModule = module {
    factory<RandomCoApi> { provideApi(get(named(randomCo))) }

    single(named(randomCo)) { provideRetrofit(get(), BaseURL.randomCo, get()) }
}

fun provideRetrofit(okHttpClient: OkHttpClient, urlBase: String, gson: Gson): Retrofit {
    return Retrofit.Builder()
        .baseUrl(urlBase)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
}

inline fun <reified T> provideApi(retrofit: Retrofit): T = retrofit.create(T::class.java)

object BaseURL {
    var randomCo = "https://api.randomuser.me/"
}

val databaseModule = module {

    // Room Database instance
    single {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java,
            "radom_co_database"
        ).fallbackToDestructiveMigration()
            .build()
    }

    // Local data source
    single { LocalDataSource(get()) as ILocalDataSource }

    // DAO
     single { get<AppDatabase>().userDao() }
}
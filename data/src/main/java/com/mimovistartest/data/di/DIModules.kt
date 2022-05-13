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

/*
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
*/

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

    /*@Singleton
    @Provides
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Singleton
    @Provides
    fun provideRandomCoApi(retrofit: Retrofit): RandomCoApi =
        retrofit.create(RandomCoApi::class.java)*/

    /*@Singleton
    @Provides
    fun provideUsersRemoteDataSource(randomCoApi: RandomCoApi) = UsersRemoteDataSource(randomCoApi)*/

    @Singleton
    @Provides
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Singleton
    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Singleton
    @Provides
    fun provideFirebaseDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance()

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
package com.raharawhey.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.raharawhey.data.api.ApiConstants
import com.raharawhey.data.api.PrayerApiService
import com.raharawhey.data.api.PrayerDatabase
import com.raharawhey.data.api.PrayerTimesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(ApiConstants.TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(ApiConstants.TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    @Provides @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(ApiConstants.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides @Singleton
    fun providePrayerApiService(retrofit: Retrofit): PrayerApiService =
        retrofit.create(PrayerApiService::class.java)

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): PrayerDatabase =
        Room.databaseBuilder(ctx, PrayerDatabase::class.java, PrayerDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides @Singleton
    fun providePrayerTimesDao(db: PrayerDatabase): PrayerTimesDao = db.prayerTimesDao()

    @Provides @Singleton
    fun provideDataStore(@ApplicationContext ctx: Context): DataStore<Preferences> = ctx.dataStore
}

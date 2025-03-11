package com.example.irecipefinder.network

import com.example.irecipefinder.BuildConfig
import com.example.irecipefinder.data.OpenAIService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @Named("RecipesBaseUrl")
    fun provideRecipesBaseUrl(): String = "https://api.together.ai/"

    @Provides
    @Singleton
    @Named("ImagesBaseUrl")
    fun provideImagesBaseUrl(): String = "https://foodish-api.herokuapp.com/"

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
        return logging
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()

    @Provides
    @Singleton
    fun provideApiKeyInterceptor(): AuthInterceptor = AuthInterceptor()

    @Singleton
    @Provides
    @Named("RecipesRetrofit")
    fun provideRecipesRetrofit(
        @Named("RecipesBaseUrl") baseUrl: String,
        okHttpClient: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Singleton
    @Provides
    @Named("ImagesRetrofit")
    fun provideImagesRetrofit(
        @Named("ImagesBaseUrl") baseUrl: String,
        okHttpClient: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Singleton
    @Provides
    @Named("RecipesService")
    fun provideRecipesService(
        @Named("RecipesRetrofit") retrofit: Retrofit
    ): OpenAIService =
        retrofit.create(OpenAIService::class.java)

    @Singleton
    @Provides
    @Named("ImagesService")
    fun provideImagesService(
        @Named("ImagesRetrofit") retrofit: Retrofit
    ): OpenAIService =
        retrofit.create(OpenAIService::class.java)

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
    }

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()
}
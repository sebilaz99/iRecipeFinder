package com.example.irecipefinder.network

import android.content.Context
import com.example.irecipefinder.data.FavouritesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    @Provides
    @Singleton
    fun provideSharedPrefHelper(@ApplicationContext context: Context): FavouritesManager {
        return FavouritesManager(context)
    }
}
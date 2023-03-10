package com.intelab.joblab.data.common.module

import android.content.Context
import com.intelab.joblab.data.common.utils.ErrorGenerator
import com.intelab.joblab.domain.common.SharedPrefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SharedPrefModule {

    @Provides
    fun provideSharedPref(@ApplicationContext context: Context) : SharedPrefs {
        return SharedPrefs(context)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object ErrorGeneratorModule {

    @Provides
    fun provideErrorGenerator(
        @ApplicationContext context: Context,
        sharedPrefs: SharedPrefs
    ) : ErrorGenerator {
        return ErrorGenerator(context, sharedPrefs)
    }
}

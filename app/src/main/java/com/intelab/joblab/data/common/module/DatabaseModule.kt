package com.intelab.joblab.data.common.module

import android.content.Context
import androidx.room.Room
import com.intelab.joblab.data.database.JoblabRoomDatabase
import com.intelab.joblab.data.database.dao.JobPostulationDao
import com.intelab.joblab.data.database.dao.JobReferenceDao
import com.intelab.joblab.data.database.dao.RegistrationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val DB_NAME = "joblab_db"

    @Singleton
    @Provides
    fun provideMyRoomDatabase(@ApplicationContext context: Context): JoblabRoomDatabase {
        return Room.databaseBuilder(
            context,
            JoblabRoomDatabase::class.java,
            DB_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Singleton
    @Provides
    fun providePrayDao(roomDatabase: JoblabRoomDatabase): RegistrationDao {
        return roomDatabase.registrationDao()
    }

    @Singleton
    @Provides
    fun provideJobReferenceDao(roomDatabase: JoblabRoomDatabase): JobReferenceDao {
        return roomDatabase.jobReferenceDao()
    }

    @Singleton
    @Provides
    fun provideJobApplicationDao(roomDatabase: JoblabRoomDatabase): JobPostulationDao {
        return roomDatabase.jopApplicationDao()
    }
}
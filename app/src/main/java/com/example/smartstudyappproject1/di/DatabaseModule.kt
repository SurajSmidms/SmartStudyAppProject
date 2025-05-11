package com.example.smartstudyappproject1.di

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import com.example.smartstudyappproject1.data.local.AppDataBase
import com.example.smartstudyappproject1.data.local.SessionDAO
import com.example.smartstudyappproject1.data.local.SubjectDAO
import com.example.smartstudyappproject1.data.local.TaskDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        application:Application
    ):AppDataBase{
        return Room.databaseBuilder(
            application,
            AppDataBase::class.java,
            "studysmart.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideSubjectDAO(database: AppDataBase):SubjectDAO{
        return database.subjectDAO()
    }

    @Provides
    @Singleton
    fun provideTaskDAO(database: AppDataBase):TaskDAO{
        return database.taskDAO()
    }

    @Provides
    @Singleton
    fun provideSessionDAO(database: AppDataBase):SessionDAO{
        return database.sessionDAO()
    }

}
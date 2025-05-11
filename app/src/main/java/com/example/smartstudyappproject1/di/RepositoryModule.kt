package com.example.smartstudyappproject1.di

import com.example.smartstudyappproject1.repository.SessionRepository
import com.example.smartstudyappproject1.repository.SessionRepositoryImpl
import com.example.smartstudyappproject1.repository.SubjectRepository
import com.example.smartstudyappproject1.repository.SubjectRepositoryImpl
import com.example.smartstudyappproject1.repository.TaskRepository
import com.example.smartstudyappproject1.repository.TaskRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindSubjectRepository(
        impl:SubjectRepositoryImpl
    ):SubjectRepository

    @Singleton
    @Binds
    abstract fun bindTaskRepository(
        impl:TaskRepositoryImpl
    ): TaskRepository

    @Singleton
    @Binds
    abstract fun bindSessionRepository(
        impl:SessionRepositoryImpl
    ):SessionRepository
}
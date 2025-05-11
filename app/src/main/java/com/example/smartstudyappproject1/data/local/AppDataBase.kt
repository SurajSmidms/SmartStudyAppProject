package com.example.smartstudyappproject1.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.smartstudyappproject1.model.Session
import com.example.smartstudyappproject1.model.Subject
import com.example.smartstudyappproject1.model.Task

@Database(entities = [Subject::class,Session::class, Task::class],
    version = 1
)

@TypeConverters(ColorListConverter::class)
abstract class AppDataBase:RoomDatabase(){

    abstract fun subjectDAO():SubjectDAO

    abstract fun taskDAO():TaskDAO

    abstract fun sessionDAO():SessionDAO
}
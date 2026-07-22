package com.example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.model.Athlete
import com.example.data.model.ContractSignature
import com.example.data.model.Reel
import com.example.data.model.UserComment
import com.example.data.model.AppNote
import com.example.data.model.CallLogEntry

@Database(
    entities = [
        Athlete::class,
        Reel::class,
        ContractSignature::class,
        UserComment::class,
        AppNote::class,
        CallLogEntry::class
    ],
    version = 3,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao
}

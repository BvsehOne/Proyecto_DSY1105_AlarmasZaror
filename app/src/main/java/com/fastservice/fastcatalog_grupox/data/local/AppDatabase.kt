package com.fastservice.fastcatalog_grupox.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [AlertaEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun alertaDao(): AlertaDao
}
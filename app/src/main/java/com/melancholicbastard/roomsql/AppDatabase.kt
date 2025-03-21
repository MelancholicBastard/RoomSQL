package com.melancholicbastard.roomsql

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Belka::class), version = 1)
abstract class AppDatabase: RoomDatabase() {
    // Сама библиотека Room реализует интерфейс BelkaDAO (Его не нужно наследовать)
    abstract fun belkaDao(): BelkaDAO
}
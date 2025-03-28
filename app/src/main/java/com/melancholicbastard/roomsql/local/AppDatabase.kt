package com.melancholicbastard.roomsql.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Belka::class, BelkaPhrase::class), version = 1)
// По хорошему нужно поменять version и сделать миграцию. Но миграцию надо отдельно реализовывать,
// поэтому мы просто переименуем имя датабазы
abstract class AppDatabase: RoomDatabase() {
    // Сама библиотека Room реализует интерфейс BelkaDAO (Его не нужно наследовать)
    abstract fun belkaDao(): BelkaDAO
}
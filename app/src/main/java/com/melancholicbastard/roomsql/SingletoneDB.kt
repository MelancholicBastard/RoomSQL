package com.melancholicbastard.roomsql

import androidx.room.Room
import com.melancholicbastard.roomsql.local.AppDatabase

object SingletoneDB {
    val db = Room.databaseBuilder(
        MainActivity.getContext(),
        AppDatabase::class.java,
        "Belka3"    // Изменили имя
    ).build()
}
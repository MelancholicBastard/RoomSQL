package com.melancholicbastard.roomsql

import androidx.room.Room

object SingletoneDB {
    val db = Room.databaseBuilder(
        MainActivity.getContext(),
        AppDatabase::class.java,
        "Belka1"
    ).build()
}
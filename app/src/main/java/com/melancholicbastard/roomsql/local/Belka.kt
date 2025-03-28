package com.melancholicbastard.roomsql.local

import androidx.room.Entity
import androidx.room.PrimaryKey

//@Entity(tableName = "Belka")
@Entity
data class Belka(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
//    @ColumnInfo(name = "bugaga") Можно переименовать table внутри sql файла
    val tailColor: String = "Black",
    val name: String,

    val phraseID: Int   // Добавили ID другой Entity
)

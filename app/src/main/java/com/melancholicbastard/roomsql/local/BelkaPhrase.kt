package com.melancholicbastard.roomsql.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BelkaPhrase(
    @PrimaryKey(autoGenerate = true)
    val phraseID: Int,
    val phrase: String
)

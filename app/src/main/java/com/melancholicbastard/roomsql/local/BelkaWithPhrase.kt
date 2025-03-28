package com.melancholicbastard.roomsql.local

import androidx.room.Embedded
import androidx.room.Relation

data class BelkaWithPhrase(     // Класс для настройки отношений между реляционными таблицами
    @Embedded
    val belka: Belka,   // Импортируем поля белки в данный класс

    @Relation(
        entityColumn = "phraseID",  // Строим отношение между полем Belka
        parentColumn = "phraseID"   // и полем BelkaPhrase
    )
    val phrase: BelkaPhrase
)

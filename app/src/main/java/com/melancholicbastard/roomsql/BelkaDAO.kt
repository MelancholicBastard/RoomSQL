package com.melancholicbastard.roomsql

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface BelkaDAO {
    @Query("SELeCT * fROM Belka") // Получение всех элементов Белки
    fun getAllBelka(): List<Belka> // Функция для реализации запроса в самом Kotlin

    // Аннотации сами поймут и сформируют запрос
    @Insert(onConflict = OnConflictStrategy.REPLACE) // При существовании такой же Белки по id
    fun insertBelka(belka: Belka): Unit

    @Update(onConflict = OnConflictStrategy.IGNORE) // При отсутствии Белки
    fun updateBelka(belka: Belka): Unit

    @Delete
    fun deleteBelka(belka: Belka): Unit

    @Query("DELETE FROM Belka WHERE id = :id")
    fun deleteBelkaByID(id: Int): Unit
}
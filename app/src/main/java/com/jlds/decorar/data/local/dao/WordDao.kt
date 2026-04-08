package com.jlds.decorar.data.local.dao

import androidx.room.*
import com.jlds.decorar.data.local.entities.WordEntry


import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    // Busca todas as palavras por ordem de inserção (essencial para a sua sequência)
    @Query("SELECT * FROM vocabulary ORDER BY id ASC")
    fun getAllWords(): Flow<List<WordEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: WordEntry)

    @Delete
    suspend fun deleteWord(word: WordEntry)
}
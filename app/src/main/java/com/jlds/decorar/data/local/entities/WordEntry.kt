package com.jlds.decorar.data.local.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "vocabulary")
data class WordEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val word: String,        // Palavra no idioma estrangeiro
    val translation: String, // Tradução
    val audioPath: String?   // Caminho do ficheiro de áudio no telemóvel
) {
    // Este campo existe apenas na memória durante o jogo
    @Ignore
    var isRevealed: Boolean = false
}
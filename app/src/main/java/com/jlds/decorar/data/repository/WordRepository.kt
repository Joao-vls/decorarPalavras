package com.jlds.decorar.data.repository
import com.jlds.decorar.data.local.dao.WordDao
import com.jlds.decorar.data.local.entities.WordEntry
import kotlinx.coroutines.flow.Flow

class WordRepository(private val wordDao: WordDao) {

    // Retorna o Flow de palavras diretamente do DAO
    // Isso manterá sua UI atualizada em tempo real
    val allWords: Flow<List<WordEntry>> = wordDao.getAllWords()

    // Operação de inserção suspensa (deve ser chamada dentro de uma Coroutine)
    suspend fun insert(word: WordEntry) {
        wordDao.insertWord(word)
    }

    // Caso queira deletar alguma palavra futuramente
    suspend fun delete(word: WordEntry) {
        wordDao.deleteWord(word)
    }
}
package com.jlds.decorar.ui.viewmodel

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.jlds.decorar.data.local.entities.WordEntry
import com.jlds.decorar.data.repository.WordRepository

import kotlinx.coroutines.launch
import kotlin.collections.get
import android.media.MediaPlayer
class VocabularyViewModel(private val repository: WordRepository) : ViewModel() {

    // 1. Pega as palavras do banco e transforma em LiveData para a UI observar
    val allWords = repository.allWords.asLiveData()

    // 2. Estados da UI
    var currentStep by mutableStateOf(0)           // Índice da palavra que você deve acertar agora
    var typedWord by mutableStateOf("")           // O que você está digitando no input 1
    var isSequenceComplete by mutableStateOf(false) // Libera os campos de cadastro (2, 3 e 4)

    // Estados para o cadastro de nova palavra
    var newWord by mutableStateOf("")
    var newTranslation by mutableStateOf("")
    var selectedAudioPath by mutableStateOf<String?>(null)

    // 3. Lógica de Verificação (Input 1)
    var errorMessage by mutableStateOf("") // Para mostrar na tela se quiser
    fun onWordTyped(input: String, isFinalAttempt: Boolean, context: android.content.Context) {
        typedWord = input
        val words = allWords.value ?: return
        if (currentStep >= words.size) return

        val targetWord = words[currentStep].word.trim()
        val currentInput = input.trim()

        if (isFinalAttempt) {
            if (currentInput.equals(targetWord, ignoreCase = true)) {
                // ACERTOU:
                // 1. Marca como revelada
                words[currentStep].isRevealed = true

                // 2. Força a UI a atualizar (Trigger manual para o LiveData/State)
                // Como words é uma List vinda do Room, precisamos "avisar" que ela mudou
                // Uma forma simples é incrementar o step e limpar o input
                currentStep++
                typedWord = ""

                if (currentStep == words.size) {
                    isSequenceComplete = true
                }
            } else {
                android.widget.Toast.makeText(context, "Erro! Voltando ao início.", android.widget.Toast.LENGTH_SHORT).show()
                resetGame()
            }
        }
    }

    fun resetGame() {
        currentStep = 0
        typedWord = ""
        isSequenceComplete = false
        // Garante que todas as palavras voltem a mostrar a tradução
        allWords.value?.forEach { it.isRevealed = false }
    }

    // 4. Salvar Nova Palavra (Inputs 2, 3 e 4)
    fun saveNewWord() {
        if (newWord.isNotBlank() && newTranslation.isNotBlank()) {
            viewModelScope.launch {
                val entry = WordEntry(
                    word = newWord.trim(),
                    translation = newTranslation.trim(),
                    audioPath = selectedAudioPath
                )
                repository.insert(entry)

                // Limpa os campos e volta para o modo memorização
                clearRegistrationFields()
                resetGame()
            }
        }
    }
    fun playAudio(path: String?) {
        if (path.isNullOrEmpty()) return

        try {
            MediaPlayer().apply {
                setDataSource(path)
                prepare()
                start()
                // Libera a memória quando o áudio terminar
                setOnCompletionListener { release() }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun clearRegistrationFields() {
        newWord = ""
        newTranslation = ""
        selectedAudioPath = null
    }
}
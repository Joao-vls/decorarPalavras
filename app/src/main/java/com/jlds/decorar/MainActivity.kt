package com.jlds.decorar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jlds.decorar.ui.theme.DecorarTheme
import com.jlds.decorar.ui.viewmodel.VocabularyViewModel
import kotlin.getValue
import com.jlds.decorar.data.local.AppDatabase
import com.jlds.decorar.data.repository.WordRepository
import com.jlds.decorar.ui.screens.MainScreen
import com.jlds.decorar.ui.viewmodel.VocabularyViewModelFactory


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        // 1. Inicializa o Banco de Dados e o Repositório
        val database by lazy { AppDatabase.getDatabase(this) }
        val repository by lazy { WordRepository(database.wordDao()) }

        // 2. Inicializa o ViewModel usando a Factory
        val viewModel: VocabularyViewModel by viewModels {
            VocabularyViewModelFactory(repository)
        }

        setContent {

            MainScreen(viewModel)
        }
    }
}


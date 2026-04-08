package com.jlds.decorar.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.jlds.decorar.ui.viewmodel.VocabularyViewModel
import com.jlds.decorar.util.FileHelper

@Composable
fun MainScreen(viewModel: VocabularyViewModel) {
    val words by viewModel.allWords.observeAsState(emptyList())
    val context = LocalContext.current

    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.selectedAudioPath = FileHelper.copyAudioToInternalStorage(context, it)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .safeDrawingPadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        AnimatedVisibility(visible = !viewModel.isSequenceComplete && words.isNotEmpty()) {
            Column {
                Text("Palavras anteriores", color = Color.White, modifier = Modifier.padding(bottom = 8.dp))
                CustomInputField(
                    value = viewModel.typedWord,
                    onValueChange = { newValue ->
                        if (newValue.endsWith(" ")) {
                            viewModel.onWordTyped(newValue, true, context)
                        } else {
                            viewModel.onWordTyped(newValue, false, context)
                        }
                    },
                    placeholder = "Digite a sequência...",
                    onDone = { viewModel.onWordTyped(viewModel.typedWord, true, context) }
                )
            }
        }

        AnimatedVisibility(visible = viewModel.isSequenceComplete || words.isEmpty()) {
            Column {
                Text("Nova Palavra (Inglês)", color = Color.White)
                CustomInputField(
                    value = viewModel.newWord,
                    onValueChange = { viewModel.newWord = it },
                    placeholder = "Ex: Apple"
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Tradução/Significado", color = Color.White)
                CustomInputField(
                    value = viewModel.newTranslation,
                    onValueChange = { viewModel.newTranslation = it },
                    placeholder = "Ex: Maçã"
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { audioPickerLauncher.launch("audio/*") },
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.White, RoundedCornerShape(8.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = if (viewModel.selectedAudioPath != null) Color(0xFF007FFF) else Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = { viewModel.saveNewWord() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007FFF))
                    ) {
                        Text("Salvar Palavra", color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "${words.size} palavras no total",
            color = Color.LightGray,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(words) { index, item ->
                val isCorrect = index < viewModel.currentStep || item.isRevealed

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCorrect) Color(0xFF1B5E20) else Color(0xFF212121)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isCorrect) item.word else item.translation,
                                color = if (isCorrect) Color.Green else Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        if (isCorrect) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (!item.audioPath.isNullOrEmpty()) {
                                    IconButton(onClick = { viewModel.playAudio(item.audioPath) }) {
                                        Icon(
                                            imageVector = Icons.Default.MusicNote,
                                            contentDescription = null,
                                            tint = Color.Green
                                        )
                                    }
                                }
                                Text("✓", color = Color.Green, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    onDone: () -> Unit = {}
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF007FFF),
            unfocusedContainerColor = Color(0xFF007FFF),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        placeholder = { Text(placeholder, color = Color.LightGray) },
        leadingIcon = {
            Icon(Icons.Default.Search, null, tint = Color.White, modifier = Modifier.size(28.dp))
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Unspecified, autoCorrectEnabled = false, keyboardType = KeyboardType.Password, imeAction = ImeAction.Done,platformImeOptions = null, showKeyboardOnFocus = null,hintLocales = null),
        keyboardActions = KeyboardActions(onDone = { onDone() })
    )
}
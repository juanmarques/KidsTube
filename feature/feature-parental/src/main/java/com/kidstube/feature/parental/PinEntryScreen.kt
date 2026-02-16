package com.kidstube.feature.parental

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinEntryScreen(
    onSuccess: () -> Unit,
    onBack: () -> Unit,
    viewModel: PinEntryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.setOnSuccess(onSuccess)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Enter PIN") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Enter your parental PIN",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(24.dp))

            // PIN dots
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                repeat(6) { index ->
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(
                                if (index < uiState.pin.length)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surfaceVariant
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Error / lock message
            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (uiState.isLocked) {
                Text(
                    text = "Try again in ${uiState.lockRemainingSeconds}s",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Number pad
            val buttons = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("", "0", "back")
            )

            buttons.forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    row.forEach { label ->
                        when (label) {
                            "" -> Spacer(modifier = Modifier.size(72.dp))
                            "back" -> {
                                IconButton(
                                    onClick = { viewModel.onBackspace() },
                                    modifier = Modifier.size(72.dp),
                                    enabled = !uiState.isLocked
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.Backspace,
                                        contentDescription = "Backspace",
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                            else -> {
                                FilledTonalButton(
                                    onClick = { viewModel.onDigitEntered(label) },
                                    modifier = Modifier.size(72.dp),
                                    enabled = !uiState.isLocked
                                ) {
                                    Text(text = label, fontSize = 24.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.kidstube.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kidstube.core.domain.model.SupportedLanguages

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isComplete) {
        if (uiState.isComplete) onComplete()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (uiState.step) {
            OnboardingStep.WELCOME -> {
                Text(
                    text = "Welcome to KidsTube!",
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "A safe way for your kids to watch YouTube videos in the languages you choose.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Let's set up parental controls first.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { viewModel.nextStep() }) {
                    Text("Get Started")
                }
            }

            OnboardingStep.CREATE_PIN -> {
                PinEntryStep(
                    title = "Create a PIN",
                    subtitle = "This PIN protects the parental settings (4-6 digits)",
                    pin = uiState.pin,
                    error = uiState.pinError,
                    onDigit = { viewModel.onPinDigit(it) },
                    onBackspace = { viewModel.onPinBackspace() },
                    onNext = { viewModel.nextStep() },
                    nextEnabled = uiState.pin.length in 4..6
                )
            }

            OnboardingStep.CONFIRM_PIN -> {
                PinEntryStep(
                    title = "Confirm PIN",
                    subtitle = "Enter the same PIN again",
                    pin = uiState.confirmPin,
                    error = uiState.pinError,
                    onDigit = { viewModel.onPinDigit(it) },
                    onBackspace = { viewModel.onPinBackspace() },
                    onNext = { viewModel.nextStep() },
                    nextEnabled = uiState.confirmPin.length in 4..6
                )
            }

            OnboardingStep.SELECT_LANGUAGES -> {
                Text(
                    text = "Select Languages",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Your child will only see videos in these languages",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SupportedLanguages.all.forEach { language ->
                        FilterChip(
                            selected = language.code in uiState.selectedLanguages,
                            onClick = { viewModel.toggleLanguage(language.code) },
                            label = { Text("${language.nativeName} (${language.code})") }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { viewModel.nextStep() },
                    enabled = uiState.selectedLanguages.isNotEmpty()
                ) {
                    Text("Finish Setup")
                }
            }
        }
    }
}

@Composable
private fun PinEntryStep(
    title: String,
    subtitle: String,
    pin: String,
    error: String?,
    onDigit: (String) -> Unit,
    onBackspace: () -> Unit,
    onNext: () -> Unit,
    nextEnabled: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
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
                            if (index < pin.length)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                )
            }
        }

        if (error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = error, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(24.dp))

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
                                onClick = onBackspace,
                                modifier = Modifier.size(72.dp)
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
                                onClick = { onDigit(label) },
                                modifier = Modifier.size(72.dp)
                            ) {
                                Text(text = label, fontSize = 24.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onNext, enabled = nextEnabled) {
            Text("Continue")
        }
    }
}

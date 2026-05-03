package com.example.pomodorotimer.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
//import androidx.compose.ui.viewinterop.AndroidView
import com.example.pomodorotimer.utils.Constants
//import com.google.android.gms.ads.AdRequest
//import com.google.android.gms.ads.AdSize
//import com.google.android.gms.ads.AdView

/**
 * Componente per il display del timer
 */
@Composable
fun TimerDisplay(
    timeFormatted: String,
    isBreak: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.semantics {
            contentDescription = "Timer: $timeFormatted"
        },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = timeFormatted,
            style = MaterialTheme.typography.displayLarge.copy(
                color = if (isBreak) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (isBreak) "PAUSA" else "FOCUS",
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
            modifier = Modifier.semantics {
                contentDescription = if (isBreak) "Sessione di pausa" else "Sessione di focus"
            }
        )
    }
}

/**
 * Componente per i pulsanti di controllo del timer
 */
@Composable
fun TimerControls(
    isRunning: Boolean,
    onStartFocus: () -> Unit,
    onStartBreak: () -> Unit,
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = onStartFocus,
                modifier = Modifier
                    .height(Constants.BUTTON_HEIGHT.dp)
                    .weight(1f)
                    .padding(horizontal = Constants.PADDING_SMALL.dp)
                    .semantics {
                        contentDescription = "Avvia sessione focus di ${Constants.FOCUS_SESSION_MINUTES} minuti"
                    }
            ) {
                Text("Start ${Constants.FOCUS_SESSION_MINUTES} min")
            }

            Button(
                onClick = onStartBreak,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                modifier = Modifier
                    .height(Constants.BUTTON_HEIGHT.dp)
                    .weight(1f)
                    .padding(horizontal = Constants.PADDING_SMALL.dp)
                    .semantics {
                        contentDescription = "Avvia sessione pausa di ${Constants.BREAK_SESSION_MINUTES} minuti"
                    }
            ) {
                Text("Break ${Constants.BREAK_SESSION_MINUTES} min")
            }
        }

        if (isRunning) {
            Spacer(modifier = Modifier.height(Constants.PADDING_SMALL.dp))
            TextButton(
                onClick = onStop,
                modifier = Modifier.semantics {
                    contentDescription = "Ferma il timer"
                }
            ) {
                Text("Stop", color = Color.Red)
            }
        }
    }
}

/**
 * Componente per la card delle statistiche
 */
@Composable
fun StatsCard(
    pomodorosCompleted: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier.padding(Constants.PADDING_MEDIUM.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Pomodoros completati oggi",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                modifier = Modifier.semantics {
                    contentDescription = "Pomodoros completati oggi"
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = pomodorosCompleted.toString(),
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.semantics {
                    contentDescription = "$pomodorosCompleted pomodoros completati"
                }
            )
        }
    }
}

/**
 * Componente per il banner AdMob
 */
@Composable
fun AdBanner(modifier: Modifier = Modifier) {
    // AdMob disabilitato in debug per risparmiare memoria
    // AndroidView(
    //     modifier = modifier.fillMaxWidth(),
    //     factory = { context ->
    //         AdView(context).apply {
    //             setAdSize(AdSize.BANNER)
    //             adUnitId = Constants.ADMOB_BANNER_ID
    //             loadAd(AdRequest.Builder().build())
    //             contentDescription = "Pubblicità"
    //         }
    //     }
    // )
}

/**
 * Componente per il campo input dei task
 */
@Composable
fun TaskInputField(
    value: String,
    onValueChange: (String) -> Unit,
    index: Int,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        placeholder = { Text("Task ${index + 1}") },
        singleLine = true
    )
}

/**
 * Componente per la sezione dei task
 */
@Composable
fun TasksSection(
    tasks: List<String>,
    onTaskUpdate: (Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            "Le mie task di oggi",
            style = MaterialTheme.typography.headlineSmall.copy(color = Color.Gray)
        )
        Spacer(modifier = Modifier.height(16.dp))

        tasks.forEachIndexed { index, task ->
            TaskInputField(
                value = task,
                onValueChange = { onTaskUpdate(index, it) },
                index = index
            )
        }
    }
}

/**
 * Componente per il messaggio di errore
 */
@Composable
fun ErrorMessage(
    message: String?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (message != null) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(Constants.PADDING_MEDIUM.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Constants.PADDING_MEDIUM.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = message,
                    color = Color(0xFFC62828),
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onDismiss) {
                    Text("Chiudi", color = Color(0xFFC62828))
                }
            }
        }
    }
}

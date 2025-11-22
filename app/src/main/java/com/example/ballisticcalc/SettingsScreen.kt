package com.example.ballisticcalc

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ballisticcalc.ui.components.ThemeControls

@Composable
fun SettingsScreen(
    user: UserProfile, // если у тебя есть класс User
    isAutoTheme: Boolean,
    isNightMode: Boolean,
    onToggleAutoTheme: (Boolean) -> Unit,
    onToggleNightMode: (Boolean) -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Настройки",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Профиль", style = MaterialTheme.typography.titleMedium)
                Text("Позывной: ${user.callsign}", style = MaterialTheme.typography.bodyMedium)
                Text("Подразделение: ${user.division}", style = MaterialTheme.typography.bodyMedium)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 👇 ВСТАВЛЯЕМ ThemeControls СЮДА
        ThemeControls(
            isAutoTheme = isAutoTheme,
            isNightMode = isNightMode,
            onToggleAutoTheme = onToggleAutoTheme,
            onToggleNightMode = onToggleNightMode
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Выйти")
        }
    }
}
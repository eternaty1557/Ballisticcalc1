package com.example.ballisticcalc.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ThemeControls(
    isAutoTheme: Boolean,
    isNightMode: Boolean,
    onToggleAutoTheme: (Boolean) -> Unit,
    onToggleNightMode: (Boolean) -> Unit
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Авто-тема (по восходу/закату)")
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = isAutoTheme,
                onCheckedChange = onToggleAutoTheme
            )
        }

        if (!isAutoTheme) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Тёмная тема")
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = isNightMode,
                    onCheckedChange = onToggleNightMode
                )
            }
        }
    }
}
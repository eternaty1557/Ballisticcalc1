package com.example.ballisticcalc

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun ProfileManagerScreen(
    profileManager: WeaponProfileManager,
    currentRolePrefix: String = "",
    onProfileSelected: (WeaponProfile) -> Unit,// ← ЕДИНСТВЕННЫЙ НОВЫЙ ПАРАМЕТР
    onDismiss: () -> Unit = {}
) {
    val profileIds by profileManager.getProfileIdsFlow().collectAsState(initial = emptySet())

    // ✅ Безопасный сбор профилей (без вложенного collectAsState)
    val profiles: List<WeaponProfile> = profileIds
        .filter { it.startsWith(currentRolePrefix) }
        .map { id ->
            profileManager.getProfileFlow(id)
        }.mapNotNull { flow ->
            flow.collectAsState(initial = null).value
        }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "📁 Профили для текущей роли",
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.titleMedium.fontSize
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (profiles.isEmpty()) {
            item {
                Text(
                    text = "Нет сохранённых профилей",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(profiles) { profile ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            onProfileSelected(profile) // ✅ передаём профиль
                        },
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = profile.weaponName,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Снаряд: ${profile.projectileName}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Прицел: ${profile.sightType}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = profile.notes,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            Button(
                onClick = {
                    scope.launch {
                        profileIds.filter { it.startsWith(currentRolePrefix) }.forEach { id ->
                            profileManager.deleteProfile(id)
                        }
                        Toast.makeText(context, "🗑️ Профили роли удалены", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Очистить все профили этой роли", color = MaterialTheme.colorScheme.onError)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text("Закрыть")
            }
        }
    }
}
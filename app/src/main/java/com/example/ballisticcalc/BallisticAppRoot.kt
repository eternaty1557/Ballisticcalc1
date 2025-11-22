package com.example.ballisticcalc.ui

import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ballisticcalc.BallisticCalculatorApp
import com.example.ballisticcalc.ProfileManagerScreen
import com.example.ballisticcalc.SettingsScreen
import com.example.ballisticcalc.WeaponProfileManager
import com.example.ballisticcalc.UserManager
import com.example.ballisticcalc.UserProfile
import com.example.ballisticcalc.WeaponProfile


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@RequiresApi(26)
fun BallisticAppRoot(
    profileManager: WeaponProfileManager,
    userManager: UserManager,
    user: UserProfile,
    onLogout: () -> Unit,
    onToggleAutoTheme: (Boolean) -> Unit,
    onToggleNightMode: (Boolean) -> Unit,
    isAutoTheme: Boolean,
    isNightMode: Boolean
) {
    // Переключение экранов
    var currentScreen by remember { mutableStateOf("main") }
    val selectedProfileState = remember { mutableStateOf<WeaponProfile?>(null) }


    when (currentScreen) {
        "main" -> {
            MainScreenContent(
                user = user,
                onShowCalculator = { currentScreen = "calculator" },
                onShowProfiles = { currentScreen = "profiles" },
                onShowSettings = { currentScreen = "settings" }
            )
        }

        "calculator" -> {
            // Подключаем ТВОЙ существующий экран расчёта
            BallisticCalculatorApp(
                profileManager = profileManager,
                userManager = userManager,
                user = user,
                onLogout = { currentScreen = "main" },
                selectedProfile = null)
        }

        "profiles" -> {
            ProfileManagerScreen(
                profileManager = profileManager,
                currentRolePrefix = "",
                onProfileSelected = { selectedProfile: WeaponProfile ->
                    currentScreen = "calculator"
                    selectedProfileState.value = selectedProfile
                },
                onDismiss = { currentScreen = "main" }
            )
        }

        "settings" -> {
            SettingsScreen(
                user = user,
                isAutoTheme = isAutoTheme,
                isNightMode = isNightMode,
                onToggleAutoTheme = onToggleAutoTheme,
                onToggleNightMode = onToggleNightMode,
                onLogout = onLogout
            )
        }
    }
}


@Composable
private fun MainScreenContent(
    user: UserProfile,
    onShowCalculator: () -> Unit,
    onShowProfiles: () -> Unit,
    onShowSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Логотип и заголовок
        Text(
            text = "BallisticCalc",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Карточка с приветствием
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Добро пожаловать",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF66BB6A) // светло-зелёный
                )
                Text(
                    text = user.callsign,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Подразделение: ${user.division}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Кнопка "Расчёт"
        ActionButton(
            text = "🎯 Расчёт",
            onClick = onShowCalculator // ← должно быть { currentScreen = "calculator" }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Кнопка "Профили"
        ActionButton(
            text = "🪖 Профили",
            onClick = onShowProfiles // ← должно быть { currentScreen = "profiles" }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Кнопка "Настройки"
        ActionButton(
            text = "⚙️ Настройки",
            onClick = onShowSettings // ← должно быть { currentScreen = "settings" }
        )
    }
}

// Универсальная кнопка — БЕЗ ИКОНОК
@Composable
private fun ActionButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF4CAF50), // тактический зелёный
            contentColor = Color.White
        ),
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
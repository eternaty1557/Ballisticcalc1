package com.example.ballisticcalc

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.ballisticcalc.data.ThemePreferenceRepository
import com.example.ballisticcalc.ui.BallisticAppRoot
import com.example.ballisticcalc.ui.theme.BallisticCalcTheme
import com.example.ballisticcalc.utils.LocationAndThemeManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Константа для имени DataStore
// Создаём DataStore через preferencesDataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_prefs")

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        val db = AppDatabase.getDatabase(this)
        val profileManager = WeaponProfileManager(db)
        val userManager = UserManager(this)

        setContent {
            // Получаем DataStore через extension property
            val dataStore = remember { applicationContext.dataStore }
            val themeRepo = remember { ThemePreferenceRepository(dataStore) }
            val locationAndThemeManager = remember { LocationAndThemeManager(this, themeRepo) }

            var isNightMode by remember { mutableStateOf(false) }
            var isAutoTheme by remember { mutableStateOf(true) }

            LaunchedEffect(Unit) {
                isAutoTheme = themeRepo.autoTheme.first()
                if (isAutoTheme) {
                    // Проверяем разрешения перед использованием GPS
                    val hasPermission = ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

                    if (hasPermission) {
                        isNightMode = withContext(Dispatchers.IO) {
                            LocationAndThemeManager.Companion.shouldUseNightMode(
                                locationAndThemeManager
                            )
                        }
                    } else {
                        // Если нет разрешения — fallback на день или последнюю известную тему
                        isNightMode = false
                    }
                } else {
                    isNightMode = themeRepo.nightMode.first()
                }
            }

// Авто-обновление темы каждые 10 минут
            LaunchedEffect(isAutoTheme) {
                while (isAutoTheme) {
                    kotlinx.coroutines.delay(10 * 60 * 1000) // 10 минут

                    val hasPermission = ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

                    if (hasPermission) {
                        isNightMode = withContext(Dispatchers.IO) {
                            LocationAndThemeManager.Companion.shouldUseNightMode(
                                locationAndThemeManager
                            )
                        }
                    }
                    // Если нет разрешения — не меняем тему, оставляем текущую
                }
            }

            val onToggleAutoTheme: (Boolean) -> Unit = { enabled ->
                lifecycleScope.launch {
                    themeRepo.setAutoTheme(enabled)
                    isAutoTheme = enabled
                    if (enabled) {
                        isNightMode = withContext(Dispatchers.IO) {
                            LocationAndThemeManager.Companion.shouldUseNightMode(
                                locationAndThemeManager
                            )
                        }
                    }
                }
            }

            val onToggleNightMode: (Boolean) -> Unit = { enabled ->
                lifecycleScope.launch {
                    themeRepo.setNightMode(enabled)
                    isNightMode = enabled
                }
            }

            BallisticCalcTheme(nightMode = isNightMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val user by userManager.authenticatedUserFlow.collectAsStateWithLifecycle(initialValue = null)

                    if (user == null) {
                        LoginScreen(
                            onLogin = { callsign, division ->
                                lifecycleScope.launch {
                                    userManager.login(callsign, division)
                                }
                            }
                        )
                    } else {
                        BallisticAppRoot(
                            profileManager = profileManager,
                            userManager = userManager,
                            user = user!!,
                            onLogout = {
                                lifecycleScope.launch {
                                    userManager.logout()
                                }
                            },
                            onToggleAutoTheme = onToggleAutoTheme,
                            onToggleNightMode = onToggleNightMode,
                            isAutoTheme = isAutoTheme,
                            isNightMode = isNightMode
                        )
                    }
                }
            }
        }
    }
}
package com.example.ballisticcalc

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.ballisticcalc.ui.theme.BallisticCalcTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getDatabase(this)
        val profileManager = WeaponProfileManager(db)
        val userManager = UserManager(this)

        setContent {
            BallisticCalcTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Получаем пользователя
                    val user by userManager.authenticatedUserFlow.collectAsStateWithLifecycle(initialValue = null)

                    if (user == null) {
                        // Показываем экран входа
                        LoginScreen(
                            onLogin = { callsign, division ->
                                lifecycleScope.launch {
                                    userManager.login(callsign, division)
                                }
                            }
                        )
                    } else {
                        // Показываем основное приложение
                        BallisticAppRoot(
                            profileManager = profileManager,
                            userManager = userManager,
                            user = user!!,
                            onLogout = {
                                lifecycleScope.launch {
                                    userManager.logout()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
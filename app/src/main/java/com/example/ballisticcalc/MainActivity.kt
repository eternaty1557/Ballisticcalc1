package com.example.ballisticcalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.ballisticcalc.data.BallisticDatabase
import com.example.ballisticcalc.ui.theme.BallisticCalcTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier


@RequiresApi(26)
@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = BallisticDatabase.getDatabase(this)
        val profileManager = WeaponProfileManager(database.ballisticDao())
        val userManager = UserManager(this) // Передаём Context

        setContent {
            BallisticCalcTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val user by userManager.userFlow.collectAsState(initial = UserProfile("Guest", "00", WeaponType.SNIPER_RIFLES))

                    BallisticCalculatorApp(
                        profileManager = profileManager,
                        userManager = userManager,
                        user = user,
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
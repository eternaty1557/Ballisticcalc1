package com.example.ballisticcalc
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch


@Composable
@RequiresApi(26)
@ExperimentalMaterial3Api
fun BallisticAppRoot(
    profileManager: WeaponProfileManager,
    userManager: UserManager,
    onLogout: Any
)
{
    val user = userManager.userFlow.collectAsState(initial = null).value
    val scope = rememberCoroutineScope()
    LocalContext.current

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            if (user == null) {
                LoginScreen { callsign, division ->
                    scope.launch {
                        userManager.login(callsign, division)
                        userManager.setCurrentRole(WeaponType.SNIPER_RIFLES)
                    }
                }
            } else {
                BallisticCalculatorApp(
                    profileManager = profileManager,
                    userManager = userManager,
                    user = user,
                    onLogout = {
                        scope.launch { userManager.logout() }
                    }
                )
            }
        }
    }
}
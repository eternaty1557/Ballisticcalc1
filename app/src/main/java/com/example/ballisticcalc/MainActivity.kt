package com.example.ballisticcalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api

@RequiresApi(value = 26)
@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val profileManager = WeaponProfileManager(this)
        val userManager = UserManager(this)
        setContent {
            BallisticAppRoot(
                profileManager = profileManager,
                userManager = userManager
            )
        }
    }
}
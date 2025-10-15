package com.example.ballisticcalc

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp



@Composable
fun LoginScreen(
    onLogin: (callsign: String, division: String) -> Unit
) {
    var callsign by remember { mutableStateOf("") }
    var division by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🎯 ВХОД В СИСТЕМУ", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = callsign,
            onValueChange = { callsign = it },
            label = { Text("Позывной") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = division,
            onValueChange = { division = it },
            label = { Text("Номер дивизии") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (callsign.isNotEmpty() && division.isNotEmpty()) {
                    onLogin(callsign, division)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = callsign.isNotEmpty() && division.isNotEmpty()
        ) {
            Text("ВОЙТИ", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

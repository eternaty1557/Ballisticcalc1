package com.example.ballisticcalc

import android.content.Context
import android.graphics.Paint
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*
import androidx.compose.ui.graphics.nativeCanvas
import kotlinx.coroutines.launch
import java.lang.Math.toRadians
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import kotlinx.coroutines.CoroutineScope
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalMaterial3Api
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BallisticCalculatorApp(
    profileManager: WeaponProfileManager,
    userManager: UserManager,
    user: UserProfile,
    onLogout: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedWeaponIndex by remember { mutableIntStateOf(0) }
    var selectedProjectileIndex by remember { mutableIntStateOf(0) }
    var angle by remember { mutableStateOf("") }
    var temperature by remember { mutableStateOf("") }
    var windSpeed by remember { mutableStateOf("") }
    var windDirection by remember { mutableStateOf("") }
    var pressure by remember { mutableStateOf("") }
    var targetDistance by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("") }
    var trajectoryPoints by remember { mutableStateOf<List<Pair<Double, Double>>>(emptyList()) }
    var showProfileManager by remember { mutableStateOf(false) }
    var roleMenuExpanded by remember { mutableStateOf(false) }

    // Фильтруем оружие по текущей роли
    val filteredWeapons = weapons.filter { it.weaponType == user.weaponType }
    if (filteredWeapons.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("⚠️ Нет доступного оружия для роли: ${user.weaponType.name}", color = Color.Red)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onLogout) {
                Text("Выйти и изменить настройки")
            }
        }
        return
    }

    // Основной скроллируемый контент
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "👤 ${user.callsign} | 🪖 ${user.division}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                ExposedDropdownMenuBox(
                    expanded = roleMenuExpanded,
                    onExpandedChange = { roleMenuExpanded = it }
                ) {
                    OutlinedButton(
                        onClick = { roleMenuExpanded = true },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Роль: ${
                                user.weaponType.name.replace("_", " ").lowercase()
                                    .replaceFirstChar { it.titlecase() }
                            }"
                        )
                        Icon(
                            imageVector = Icons.Outlined.ArrowDropDown,
                            contentDescription = "Сменить роль"
                        )
                    }

                    ExposedDropdownMenu(
                        expanded = roleMenuExpanded,
                        onDismissRequest = { roleMenuExpanded = false }
                    ) {
                        WeaponType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        type.name.replace("_", " ").lowercase()
                                            .replaceFirstChar { it.titlecase() })
                                },
                                onClick = {
                                    scope.launch { userManager.setCurrentRole(type) }
                                    roleMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "🎯 Баллистический Калькулятор",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            WeaponSelector(
                weapons = filteredWeapons,
                selectedWeaponIndex = selectedWeaponIndex,
                onWeaponSelected = { index ->
                    selectedWeaponIndex = index
                    selectedProjectileIndex = 0
                }
            )
        }

        item {
            ProjectileSelector(
                projectiles = filteredWeapons[selectedWeaponIndex].projectiles,
                selectedProjectileIndex = selectedProjectileIndex,
                onProjectileSelected = { index -> selectedProjectileIndex = index }
            )
        }

        item { InputField(label = "Угол выстрела (°)", value = angle, onValueChange = { angle = it }) }
        item { InputField(label = "Дистанция до цели (м)", value = targetDistance, onValueChange = { targetDistance = it }) }
        item { InputField(label = "Температура (°C)", value = temperature, onValueChange = { temperature = it }) }
        item { InputField(label = "Скорость ветра (м/с)", value = windSpeed, onValueChange = { windSpeed = it }) }
        item { InputField(label = "Направление ветра (°)", value = windDirection, onValueChange = { windDirection = it }) }
        item { InputField(label = "Давление (мм рт.ст.)", value = pressure, onValueChange = { pressure = it }) }

        item {
            Button(
                onClick = {
                    try {
                        // ✅ ШАГ 1: Проверяем, что все поля заполнены (с trim!)
                        if (temperature.trim().isEmpty() || windSpeed.trim().isEmpty() ||
                            windDirection.trim().isEmpty() || pressure.trim().isEmpty() ||
                            angle.trim().isEmpty() || targetDistance.trim().isEmpty()
                        ) {
                            resultText = "⚠️ Заполните все поля!"
                            trajectoryPoints = emptyList()
                            return@Button
                        }

                        // ✅ ШАГ 2: Безопасно парсим значения (с trim!)
                        val angleVal = angle.trim().toDouble()
                        val targetDistVal = targetDistance.trim().toDouble()
                        val tempVal = temperature.trim().toDouble()
                        val windSpeedVal = windSpeed.trim().toDouble()
                        val windDirVal = windDirection.trim().toDouble()
                        val pressureVal = pressure.trim().toDouble()

                        // Теперь используем эти переменные вместо angle.toDouble() и т.д.
                        val projectile = filteredWeapons[selectedWeaponIndex].projectiles[selectedProjectileIndex]
                        val v0 = projectile.muzzleVelocity
                        val thetaDegrees = angleVal // ← используем angleVal
                        val g = 9.81
                        val thetaRadians = toRadians(thetaDegrees)

                        // Формулы 1-3
                        val maxH = (v0 * v0 * sin(thetaRadians) * sin(thetaRadians)) / (2 * g)
                        val flightTime = (2 * v0 * sin(thetaRadians)) / g
                        val range = (v0 * v0 * sin(2 * thetaRadians)) / g

                        // Плотность воздуха
                        val tempC = tempVal // ← используем tempVal
                        val pressureMmHg = pressureVal // ← используем pressureVal
                        val pressurePa = pressureMmHg * 133.322
                        val tempK = tempC + 273.15
                        val rho = pressurePa / (287 * tempK)

                        // Сила сопротивления
                        val area = PI * (projectile.diameter / 2).pow(2.0)
                        val fd = 0.5 * projectile.cd * rho * area * v0 * v0

                        // Реалистичная траектория
                        println("Вычисляем траекторию для снаряда: ${projectile.name}, угол: $thetaDegrees°, плотность: $rho")
                        val calculatedTrajectory = calculateTrajectoryWithDrag(projectile, thetaDegrees, rho)
                        println("Получено точек траектории: ${calculatedTrajectory.size}")
                        val actualMaxHeight = calculatedTrajectory.maxOfOrNull { it.second } ?: 0.0
                        val actualRange = if (calculatedTrajectory.size > 1) calculatedTrajectory.last().first else 0.0
                        val actualFlightTime = calculatedTrajectory.size * 0.01

                        // Высота на дистанции цели
                        val distanceM = targetDistVal // ← используем targetDistVal
                        val heightAtTarget = findHeightAtDistance(calculatedTrajectory, distanceM)

                        // Ветер
                        val windSpeedValFinal = windSpeedVal // ← используем windSpeedVal
                        val windAngleRad = toRadians(windDirVal) // ← используем windDirVal
                        val windLateral = windSpeedValFinal * sin(windAngleRad)
                        val lateralDrift = windLateral * actualFlightTime

                        val verticalCorrectionM = -heightAtTarget
                        val horizontalCorrectionM = lateralDrift

                        val sightType = filteredWeapons[selectedWeaponIndex].sightType
                        var verticalAdjustmentText = ""
                        var horizontalAdjustmentText = ""

                        when (sightType) {
                            SightType.OPTICAL_MIL -> {
                                val vertMil = verticalCorrectionM / (distanceM / 1000)
                                val horMil = horizontalCorrectionM / (distanceM / 1000)
                                verticalAdjustmentText = "Поднять прицел на ${"%.2f".format(vertMil)} мила"
                                horizontalAdjustmentText =
                                    if (horMil > 0) "Правее на ${"%.2f".format(horMil)} мила" else "Левее на ${"%.2f".format(-horMil)} мила"
                            }

                            SightType.OPTICAL_MOA -> {
                                val vertCm = verticalCorrectionM * 100
                                val horCm = horizontalCorrectionM * 100
                                val vertMOA = vertCm / (distanceM / 100) / 2.908
                                val horMOA = horCm / (distanceM / 100) / 2.908
                                verticalAdjustmentText = "Поднять прицел на ${"%.2f".format(vertMOA)} MOA"
                                horizontalAdjustmentText =
                                    if (horMOA > 0) "Правее на ${"%.2f".format(horMOA)} MOA" else "Левее на ${"%.2f".format(-horMOA)} MOA"
                            }

                            SightType.IRON_SIGHTS -> {
                                val vertCm = verticalCorrectionM * 100
                                val horCm = horizontalCorrectionM * 100
                                val vertClicks = vertCm / (distanceM / 100) / 25
                                val horClicks = horCm / (distanceM / 100) / 25
                                verticalAdjustmentText = "Поднять на ${"%.1f".format(vertClicks)} делений"
                                horizontalAdjustmentText =
                                    if (horClicks > 0) "Правее на ${"%.1f".format(horClicks)} делений" else "Левее на ${"%.1f".format(-horClicks)} делений"
                            }

                            SightType.MORTAR -> {
                                val vertThousandths = verticalCorrectionM / (distanceM / 1000)
                                val horThousandths = horizontalCorrectionM / (distanceM / 1000)
                                verticalAdjustmentText = "Угол: +${"%.1f".format(-vertThousandths)} тыс."
                                horizontalAdjustmentText =
                                    if (horThousandths > 0) "Вправо: ${"%.1f".format(horThousandths)} тыс." else "Влево: ${"%.1f".format(-horThousandths)} тыс."
                            }
                        }

                        resultText = """
                    🎯 РАСЧЁТ ПО 4 ФОРМУЛАМ + ТРАЕКТОРИЯ:

                    📐 ФОРМУЛА 1 (макс. высота, идеал): ${"%.2f".format(maxH)} м
                    ⏱️ ФОРМУЛА 2 (время полёта, идеал): ${"%.2f".format(flightTime)} с
                    📏 ФОРМУЛА 3 (дальность, идеал): ${"%.2f".format(range)} м
                    🌀 ФОРМУЛА 4 (сила сопротивления): ${"%.3f".format(fd)} Н (ρ=${"%.4f".format(rho)} кг/м³)

                    🔥 РЕАЛИСТИЧНАЯ ТРАЕКТОРИЯ:
                    → Макс. высота: ${"%.2f".format(actualMaxHeight)} м
                    → Дальность: ${"%.2f".format(actualRange)} м
                    → Время: ${"%.2f".format(actualFlightTime)} с

                    🎯 ПОПРАВКИ НА ПРИЦЕЛЕ:
                    → $verticalAdjustmentText
                    → $horizontalAdjustmentText
                """.trimIndent()

                        trajectoryPoints = calculatedTrajectory

                    } catch (e: NumberFormatException) {
                        resultText = "⚠️ Введены некорректные числа. Проверьте все поля."
                        trajectoryPoints = emptyList()
                    } catch (e: Exception) {
                        resultText = "⚠️ Ошибка: ${e.message ?: "Неизвестная ошибка"}"
                        trajectoryPoints = emptyList()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Рассчитать", fontSize = 18.sp)
            }
        }

        item {
            Button(
                onClick = {
                    val weapon = filteredWeapons[selectedWeaponIndex]
                    val projectile = weapon.projectiles[selectedProjectileIndex]
                    val profileId =
                        "${user.callsign}_${user.weaponType.name}_${weapon.name}_${projectile.name}"
                            .replace(" ", "_")
                            .replace(".", "_")

                    val profile = WeaponProfile(
                        id = profileId,
                        weaponName = weapon.name,
                        projectileName = projectile.name,
                        sightType = weapon.sightType.name,
                        notes = "Сохранено ${LocalDateTime.now()}"
                    )

                    scope.launch {
                        profileManager.saveProfile(profile)
                        Toast.makeText(
                            context,
                            "✅ Профиль сохранён: ${profile.weaponName}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("💾 Сохранить профиль", color = Color.White)
            }
        }

        item {
            Button(
                onClick = { showProfileManager = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
            ) {
                Text("📂 Мои профили", color = Color.White)
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text("🚪 Выйти", color = Color.White)
            }

            }
        }

        item(key = "result_${resultText.hashCode()}") {
            ResultDisplay(resultText = resultText)
        }

        item(key = "graph_${trajectoryPoints.size}") {
            TrajectoryDisplay(points = trajectoryPoints)
        }
    }

    // Диалог — ВНЕ LazyColumn!
    if (showProfileManager) {
        AlertDialog(
            onDismissRequest = { showProfileManager = false },
            title = { Text(text = "Мои профили (${user.weaponType.name})") },
            text = {
                ProfileManagerScreen(
                    profileManager = profileManager,
                    currentRolePrefix = "${user.callsign}_${user.weaponType.name}_",
                    onProfileSelected = { selectedProfile: WeaponProfile ->
                        val weaponIndex = weapons.indexOfFirst { it.name == selectedProfile.weaponName }
                        if (weaponIndex != -1) {
                            selectedWeaponIndex = weaponIndex
                            val projectileIndex =
                                weapons[weaponIndex].projectiles.indexOfFirst { it.name == selectedProfile.projectileName }
                            if (projectileIndex != -1) {
                                selectedProjectileIndex = projectileIndex
                            }
                            Toast.makeText(
                                context,
                                "✅ Загружен: ${selectedProfile.weaponName}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        showProfileManager = false
                    },
                    onDismiss = { showProfileManager = false },
                    context = context,
                    scope = scope
                )
            },
            confirmButton = {}
        )
    }


    if (showProfileManager) {
        AlertDialog(
            onDismissRequest = { showProfileManager = false },
            title = { Text(text = "Мои профили (${user.weaponType.name})") },
            text = {
                ProfileManagerScreen(
                    profileManager = profileManager,
                    currentRolePrefix = "${user.callsign}_${user.weaponType.name}_",
                    onProfileSelected = { selectedProfile: WeaponProfile -> // ✅ Явно указали тип!
                        val weaponIndex =
                            weapons.indexOfFirst { it.name == selectedProfile.weaponName }
                        if (weaponIndex != -1) {
                            selectedWeaponIndex = weaponIndex
                            val projectileIndex =
                                weapons[weaponIndex].projectiles.indexOfFirst { it.name == selectedProfile.projectileName }
                            if (projectileIndex != -1) {
                                selectedProjectileIndex = projectileIndex
                            }
                            Toast.makeText(
                                context,
                                "✅ Загружен: ${selectedProfile.weaponName}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        showProfileManager = false
                    },
                    onDismiss = { showProfileManager = false },
                    context = context,
                    scope = scope
                )
            },
            confirmButton = {}
        )
    }
}


@Composable
fun ProfileManagerScreen(
    profileManager: WeaponProfileManager,
    currentRolePrefix: String,
    onProfileSelected: (WeaponProfile) -> Unit,
    onDismiss: () -> Unit,
    context: Context,
    scope: CoroutineScope,
) {
    val profileIds by profileManager.getProfileIdsFlow().collectAsState(initial = emptySet())
    val profiles = profileIds
        .filter { it.startsWith(currentRolePrefix) }
        .mapNotNull { id ->
            val profileFlow = profileManager.getProfileFlow(id)
            val profile by profileFlow.collectAsState(initial = null)
            profile
        }

    LazyColumn {
        item {
            Text("📁 Профили для текущей роли", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (profiles.isEmpty()) {
            item { Text("Нет сохранённых профилей", color = Color.Gray) }
        } else {
            items(profiles.size) { index ->
                val profile = profiles[index]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onProfileSelected(profile); onDismiss() },
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(profile.weaponName, fontWeight = FontWeight.Bold)
                        Text("Снаряд: ${profile.projectileName}")
                        Text("Прицел: ${profile.sightType}")
                        Text(profile.notes, color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    scope.launch {
                        profileIds.filter { it.startsWith(currentRolePrefix) }.forEach { id ->
                            profileManager.deleteProfile(id)
                        }
                        Toast.makeText(context, "🗑️ Профили роли удалены", Toast.LENGTH_SHORT)
                            .show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Очистить все профили этой роли", color = Color.White)
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

@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalMaterial3Api
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeaponSelector(
    weapons: List<Weapon>,
    selectedWeaponIndex: Int,
    onWeaponSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() } // ✅ Инициализация

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = weapons[selectedWeaponIndex].name,
            onValueChange = {},
            readOnly = true,
            label = { Text("Выберите оружие") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester) // ✅ Передача focusRequester
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            weapons.forEachIndexed { index, weapon ->
                DropdownMenuItem(
                    text = { Text(weapon.name) },
                    onClick = {
                        onWeaponSelected(index)
                        expanded = false
                    }
                )
            }
        }
    }
}

@ExperimentalMaterial3Api
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectileSelector(
    projectiles: List<Projectile>,
    selectedProjectileIndex: Int,
    onProjectileSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = projectiles[selectedProjectileIndex].name,
            onValueChange = {},
            readOnly = true,
            label = { Text("Выберите снаряд") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            projectiles.forEachIndexed { index, projectile ->
                DropdownMenuItem(
                    text = { Text(projectile.name) },
                    onClick = {
                        onProjectileSelected(index)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun TrajectoryGraph(
    points: List<Pair<Double, Double>>,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val screenWidthPx = with(density) { LocalConfiguration.current.screenWidthDp.dp.toPx() }
    val graphWidth = screenWidthPx * 0.9f
    val graphHeight = 400f

    // 👇 Добавь эту строку для отладки
    println("TrajectoryGraph: ${points.size} точек")

    if (points.isEmpty()) return

    Canvas(modifier = modifier
        .fillMaxWidth()
        .height(graphHeight.dp)) {
        val maxX = points.maxOfOrNull { it.first } ?: 1.0
        val maxY = points.maxOfOrNull { it.second } ?: 1.0

        val scaleX = graphWidth / maxX.toFloat()
        val scaleY = graphHeight / maxY.toFloat()

        drawLine(
            color = Color.Gray,
            start = Offset(0f, graphHeight),
            end = Offset(graphWidth, graphHeight),
            strokeWidth = 2f
        )

        for (i in 1 until points.size) {
            val prev = points[i - 1]
            val curr = points[i]

            val x1 = (prev.first * scaleX).toFloat()
            val y1 = graphHeight - (prev.second * scaleY).toFloat()
            val x2 = (curr.first * scaleX).toFloat()
            val y2 = graphHeight - (curr.second * scaleY).toFloat()

            drawLine(
                color = Color.Red,
                start = Offset(x1, y1),
                end = Offset(x2, y2),
                strokeWidth = 4f
            )
        }

        drawContext.canvas.nativeCanvas.drawText(
            "Земля",
            10f,
            graphHeight - 5f,
            Paint().apply {
                color = android.graphics.Color.GRAY
                textSize = 40f
            }
        )
    }
}
@Composable
fun ResultDisplay(resultText: String) {
    if (resultText.isNotEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Text(
                text = resultText,
                modifier = Modifier.padding(16.dp),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun TrajectoryDisplay(points: List<Pair<Double, Double>>) {
    if (points.isNotEmpty()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Траектория полёта", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            TrajectoryGraph(points = points)
        }
    }
}
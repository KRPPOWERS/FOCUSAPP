package com.example.focusapp.ui

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.focusapp.database.FocusAppDatabase
import com.example.focusapp.model.FocusTimeSchedule
import com.example.focusapp.viewmodel.FocusViewModel
import com.example.focusapp.viewmodel.FocusViewModelFactory
import androidx.compose.material3.Surface

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            FocusAppTheme {
                val database = FocusAppDatabase.getDatabase(this@MainActivity)
                val viewModel: FocusViewModel = viewModel(
                    factory = FocusViewModelFactory(database)
                )
                
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FocusAppMainScreen(viewModel, this@MainActivity)
                }
            }
        }
        
        // Check and enable accessibility service
        checkAccessibilityService()
    }

    private fun checkAccessibilityService() {
        val accessibilityEnabled = isAccessibilityServiceEnabled()
        if (!accessibilityEnabled) {
            // Show notification to user about enabling accessibility
        }
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val context = this
        var accessibilityEnabled = false
        val service = "${packageName}/com.example.focusapp.service.FocusAccessibilityService"
        try {
            accessibilityEnabled = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )?.contains(service) ?: false
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return accessibilityEnabled
    }
}

@Composable
fun FocusAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF6200EE),
            secondary = Color(0xFF03DAC6),
            tertiary = Color(0xFF03DAC6),
            background = Color(0xFF121212),
            surface = Color(0xFF1E1E1E)
        ),
        content = content
    )
}

@Composable
fun FocusAppMainScreen(viewModel: FocusViewModel, activity: ComponentActivity) {
    var selectedTab by remember { mutableStateOf(0) }
    val context = LocalContext.current
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Schedule, contentDescription = "Focus Times") },
                    label = { Text("Focus Times") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Lock, contentDescription = "Blocked Apps") },
                    label = { Text("Blocked Apps") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.BarChart, contentDescription = "Analytics") },
                    label = { Text("Analytics") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 }
                )
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            0 -> FocusScheduleScreen(viewModel, Modifier.padding(innerPadding))
            1 -> BlockedAppsScreen(viewModel, activity, Modifier.padding(innerPadding))
            2 -> AnalyticsScreen(viewModel, Modifier.padding(innerPadding))
            3 -> SettingsScreen(context, Modifier.padding(innerPadding))
        }
    }
}

@Composable
fun FocusScheduleScreen(viewModel: FocusViewModel, modifier: Modifier = Modifier) {
    val schedules by viewModel.allSchedules.collectAsState(initial = emptyList())
    var showDialog by remember { mutableStateOf(false) }
    
    Column(modifier = modifier
        .fillMaxSize()
        .padding(16.dp)
        .verticalScroll(rememberScrollState())) {
        Text(
            "Focus Time Schedules",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Text(
            "Set up to 10 focus sessions (${schedules.size}/10)",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        schedules.forEach { schedule ->
            ScheduleCard(schedule, viewModel)
        }
        
        if (schedules.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("📅 No focus times yet", fontWeight = FontWeight.Bold)
                    Text("Create your first focus session below", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        if (schedules.size < 10) {
            Button(
                onClick = { showDialog = true },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6200EE)
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Focus Time")
            }
        }
    }
    
    if (showDialog) {
        AddScheduleDialog(
            onDismiss = { showDialog = false },
            onAdd = { name, start, end, days ->
                viewModel.addSchedule(name, start, end, days)
                showDialog = false
            }
        )
    }
}

@Composable
fun ScheduleCard(schedule: FocusTimeSchedule, viewModel: FocusViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A2A2A)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    schedule.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    "${schedule.startTime} - ${schedule.endTime}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    "Every selected day",
                    fontSize = 12.sp,
                    color = Color(0xFF888888)
                )
            }
            
            Row {
                Switch(
                    checked = schedule.isActive,
                    onCheckedChange = { 
                        viewModel.updateSchedule(schedule.copy(isActive = it))
                    }
                )
                IconButton(
                    onClick = { viewModel.deleteSchedule(schedule) }
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun AddScheduleDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("Focus Session") }
    var startTime by remember { mutableStateOf("09:00") }
    var endTime by remember { mutableStateOf("12:00") }
    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val dayValues = listOf("1", "2", "3", "4", "5", "6", "0")
    var selectedDays by remember { mutableStateOf(setOf("1", "2", "3", "4", "5")) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Focus Schedule") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Schedule Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = OutlinedTextFieldDefaults.colors()
                )
                
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Start") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors()
                    )
                    TextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("End") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors()
                    )
                }
                
                Text("Days of Week:", fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
                
                daysOfWeek.zip(dayValues).forEach { (day, value) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selectedDays.contains(value),
                            onCheckedChange = {
                                selectedDays = if (it) {
                                    selectedDays + value
                                } else {
                                    selectedDays - value
                                }
                            }
                        )
                        Text(day, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onAdd(name, startTime, endTime, selectedDays.joinToString(","))
            }) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

data class AppInfo(
    val appName: String,
    val packageName: String
)

fun getInstalledApps(packageManager: PackageManager): List<AppInfo> {
    return try {
        packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 }
            .map { appInfo ->
                AppInfo(
                    appName = packageManager.getApplicationLabel(appInfo).toString(),
                    packageName = appInfo.packageName
                )
            }
            .sortedBy { it.appName }
    } catch (e: Exception) {
        emptyList()
    }
}

fun isSystemApp(packageName: String): Boolean {
    return packageName.startsWith("android.") ||
            packageName.startsWith("com.android.") ||
            packageName == "com.example.focusapp"
}

@Composable
fun BlockedAppsScreen(viewModel: FocusViewModel, activity: ComponentActivity, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val packageManager = context.packageManager
    var installedApps by remember { mutableStateOf<List<AppInfo>>(emptyList()) }
    var selectedApps by remember { mutableStateOf<Set<String>>(emptySet()) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(Unit) {
        installedApps = getInstalledApps(packageManager)
    }

    val filteredApps = installedApps.filter { app ->
        app.appName.contains(searchQuery.text, ignoreCase = true) &&
        !isSystemApp(app.packageName)
    }

    Column(modifier = modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Text(
            "Blocked Apps",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            "Apps to restrict during focus time (${selectedApps.size} selected)",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search apps...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = OutlinedTextFieldDefaults.colors()
        )

        LazyColumn {
            items(filteredApps) { app ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clickable {
                            selectedApps = if (selectedApps.contains(app.packageName)) {
                                selectedApps - app.packageName
                            } else {
                                selectedApps + app.packageName
                            }
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedApps.contains(app.packageName)) 
                            Color(0xFF2A3A3A) else Color(0xFF2A2A2A)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(app.appName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(app.packageName, fontSize = 11.sp, color = Color.Gray)
                        }
                        Checkbox(
                            checked = selectedApps.contains(app.packageName),
                            onCheckedChange = {
                                selectedApps = if (it) {
                                    selectedApps + app.packageName
                                } else {
                                    selectedApps - app.packageName
                                }
                            }
                        )
                    }
                }
            }
        }

        if (filteredApps.isEmpty()) {
            Text(
                "No apps found",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 32.dp),
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        if (selectedApps.isNotEmpty()) {
            Button(
                onClick = {
                    selectedApps.forEach { packageName ->
                        val appName = installedApps.find { it.packageName == packageName }?.appName ?: packageName
                        viewModel.addRestrictedApp(packageName, appName, 1)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
            ) {
                Text("Add ${selectedApps.size} App${if (selectedApps.size != 1) "s" else ""} to Focus Block")
            }
        }
    }
}

@Composable
fun AnalyticsScreen(viewModel: FocusViewModel, modifier: Modifier = Modifier) {
    Column(modifier = modifier
        .fillMaxSize()
        .padding(16.dp)
        .verticalScroll(rememberScrollState())) {
        Text(
            "Usage Analytics",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Today's Focus Time", fontWeight = FontWeight.Bold)
                Text("2 hours 30 minutes", fontSize = 20.sp, color = Color(0xFF03DAC6), modifier = Modifier.padding(top = 8.dp))
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Most Used App", fontWeight = FontWeight.Bold)
                Text("Twitter - 1h 15m", fontSize = 16.sp, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Warnings This Week", fontWeight = FontWeight.Bold)
                Text("23 warnings shown", fontSize = 16.sp, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}

@Composable
fun SettingsScreen(context: android.content.Context, modifier: Modifier = Modifier) {
    Column(modifier = modifier
        .fillMaxSize()
        .padding(16.dp)
        .verticalScroll(rememberScrollState())) {
        Text(
            "Settings",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .clickable {
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    context.startActivity(intent)
                },
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Enable Accessibility Service", fontWeight = FontWeight.Bold)
                    Text("Required for app monitoring", fontSize = 12.sp, color = Color.Gray)
                }
                Icon(Icons.Default.ChevronRight, contentDescription = "Open", tint = Color.Gray)
            }
        }
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .clickable {
                    val intent = Intent(Settings.ACTION_SECURITY_SETTINGS)
                    context.startActivity(intent)
                },
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Device Admin", fontWeight = FontWeight.Bold)
                    Text("For advanced control features", fontSize = 12.sp, color = Color.Gray)
                }
                Icon(Icons.Default.ChevronRight, contentDescription = "Open", tint = Color.Gray)
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("About FocusApp", fontWeight = FontWeight.Bold)
                    Text("Version 1.0.0", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}

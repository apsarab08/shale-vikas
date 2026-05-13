package com.shaalevikas.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.shaalevikas.app.ui.theme.*
import com.shaalevikas.app.viewmodel.AppViewModel

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlumniHomeScreen(
    viewModel: AppViewModel,
    onNeedDetail: (String) -> Unit,
    onLogout: () -> Unit
) {
    val tabs = listOf(
        BottomNavItem("Needs",  Icons.Default.List,        Icons.Default.List),
        BottomNavItem("Donors", Icons.Default.People,      Icons.Default.People),
        BottomNavItem("Impact", Icons.Default.PhotoLibrary, Icons.Default.PhotoLibrary)
    )

    var selectedTab by remember { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Show messages from ViewModel
    LaunchedEffect(Unit) {
        viewModel.message.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Shaale-Vikas", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = White)
                        Text("ZP School, Dharwad", fontSize = 12.sp, color = White.copy(alpha = 0.8f))
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout", tint = White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenPrimary)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = White) {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick  = { selectedTab = index },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == index) tab.selectedIcon else tab.icon,
                                contentDescription = tab.label
                            )
                        },
                        label = { Text(tab.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor   = GreenPrimary,
                            selectedTextColor   = GreenPrimary,
                            indicatorColor      = GreenLight,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> NeedsTab(viewModel, onNeedDetail)
                1 -> DonorsTab(viewModel)
                2 -> ImpactTab(viewModel)
            }
        }
    }
}

package com.shaalevikas.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.SideEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController

import com.shaalevikas.app.ui.AppNavGraph
import com.shaalevikas.app.ui.theme.GreenDark
import com.shaalevikas.app.ui.theme.ShaaleVikasTheme
import com.shaalevikas.app.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            ShaaleVikasTheme {
                val navController = rememberNavController()
                AppNavGraph(
                    navController = navController,
                    viewModel     = viewModel
                )
            }
        }
    }
}

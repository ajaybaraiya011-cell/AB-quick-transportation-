package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.UserRole
import com.example.ui.AdminDashboardScreen
import com.example.ui.CustomerDashboard
import com.example.ui.DriverDashboard
import com.example.ui.LandingScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.TransportViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: TransportViewModel = viewModel()
                var currentScreen by remember { mutableStateOf("landing") } // "landing", "customer", "driver", "admin"

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    // Main Route Switcher
                    when (currentScreen) {
                        "landing" -> {
                            LandingScreen(
                                onNavigateToCustomer = {
                                    viewModel.switchRole(UserRole.CUSTOMER)
                                    currentScreen = "customer"
                                },
                                onNavigateToDriver = {
                                    viewModel.switchRole(UserRole.DRIVER)
                                    currentScreen = "driver"
                                },
                                onNavigateToAdmin = {
                                    viewModel.switchRole(UserRole.ADMIN)
                                    currentScreen = "admin"
                                }
                            )
                        }
                        "customer" -> {
                            CustomerDashboard(
                                viewModel = viewModel,
                                onBackToHome = { currentScreen = "landing" }
                            )
                        }
                        "driver" -> {
                            DriverDashboard(
                                viewModel = viewModel,
                                onBackToHome = { currentScreen = "landing" }
                            )
                        }
                        "admin" -> {
                            AdminDashboardScreen(
                                viewModel = viewModel,
                                onBackToHome = { currentScreen = "landing" }
                            )
                        }
                    }
                }
            }
        }
    }
}

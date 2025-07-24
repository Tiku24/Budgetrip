package com.example.budgetrip.ui.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.budgetrip.ui.features.home.HomeScreen
import com.example.budgetrip.ui.features.home.HomeViewModel
import com.example.budgetrip.ui.features.textextraction.TextExtractScreen

@Composable
fun NavApp(modifier: Modifier = Modifier) {
    val vm = hiltViewModel<HomeViewModel>()
    val navController = rememberNavController()
    val showFloatingActionButton = remember { mutableStateOf(false) }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            if (showFloatingActionButton.value){
                FloatingActionButton(onClick = {
                    navController.navigate(TextExtractScreen)
                }, containerColor = MaterialTheme.colorScheme.onPrimary) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Icon(imageVector = Icons.Outlined.QrCodeScanner,contentDescription = null, tint = MaterialTheme.colorScheme.background)
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(text = "Scan Receipt",color = MaterialTheme.colorScheme.background)
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(navController = navController,startDestination = Home, modifier = Modifier.padding(paddingValues)) {
            composable<Home> {
                showFloatingActionButton.value = true
                HomeScreen(vm =vm,modifier=modifier)
            }
            composable<TextExtractScreen> {
                showFloatingActionButton.value = false
                TextExtractScreen(modifier = modifier)
            }
            composable<UploadScanImage> {

            }
        }
    }
}
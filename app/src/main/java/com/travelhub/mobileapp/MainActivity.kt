package com.travelhub.mobileapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.travelhub.mobileapp.navigation.RootNavGraph
import com.travelhub.mobileapp.ui.theme.TravelHubTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TravelHubTheme {
                val navController = rememberNavController()
                RootNavGraph(navController = navController)
            }
        }
    }
}
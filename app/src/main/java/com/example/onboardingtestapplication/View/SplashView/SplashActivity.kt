package com.example.onboardingtestapplication.View.SplashView

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.onboardingtestapplication.View.MapView.MapActivity
import com.example.onboardingtestapplication.View.SplashView.screeen.SplashScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.filter


@AndroidEntryPoint
class SplashActivity : ComponentActivity() {

    private val splashViewModel : SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        splashViewModel.launchProgress()

        setContent {
          SplashScreen(splashViewModel)
        }

        splashViewModel.viewModelScope.launch(CoroutineName("change screen coroutine")) {
               splashViewModel.changeScreen
                   .filter { changeScreen ->
                       Log.d("splashActivity", "change value : $changeScreen")
                       changeScreen
                   }
                   .collect {
                       Log.d("splashActivity", "launch screen change")
                       val intent = Intent(this@SplashActivity, MapActivity::class.java)
                       intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                       startActivity(intent)
                   }
            Log.d("splashActivity", "change logic end")
        }
    }
}


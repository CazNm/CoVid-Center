package com.example.onboardingtestapplication.View.SplashView

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.onboardingtestapplication.View.MapView.MapActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*


@AndroidEntryPoint
class SplashActivity : ComponentActivity() {

   private val splashViewModel : SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        splashViewModel.launchProgress()
        setContent {
            Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
                loadingText()
                progressBar(progressValue = splashViewModel.progressValue.observeAsState().value!!)
            }
        }


        newSingleThreadContext("checkThread").use {
            CoroutineScope(it).launch {
                println("active screen load checker ${Thread.currentThread().name}")

                while (isActive)
                {
                    if (splashViewModel.changeScreen.value!!) {
                        val intent = Intent(this@SplashActivity, MapActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        break
                    }
                }
            }
        }

    }
}

@Composable
fun progressBar(progressValue : Float) {
    LinearProgressIndicator(progressValue, backgroundColor = Color.White, modifier = Modifier.padding(30.dp));
}

@Preview
@Composable
fun loadingText() {
    Text("Loading...",fontSize = 24.sp, color = Color.Black)
}
package com.example.onboardingtestapplication.View.SplashView.screeen

import android.window.SplashScreen
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onboardingtestapplication.View.SplashView.SplashViewModel


@Composable
fun SplashScreen(splashViewModel : SplashViewModel) {
    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
        loadingText()
        progressBar(progressValue = splashViewModel.progressValue.observeAsState().value!!)
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
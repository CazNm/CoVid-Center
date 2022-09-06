@file:OptIn(ExperimentalNaverMapApi::class)

package com.example.onboardingtestapplication.View.MapView

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import com.example.onboardingtestapplication.Model.CoVidCenter
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.MarkerState
import com.naver.maps.map.compose.NaverMap
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MapActivity : ComponentActivity() {

    private val mapViewModel : MapViewModel by viewModels()

   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       MainScope().launch {
           mapViewModel.getCenterData().collect {
               Log.d("center", "$it")
           }
       }

        setContent {
            NaverMap(modifier = Modifier.fillMaxSize()) {
                Marker(
                    state = MarkerState(position = LatLng(37.532600, 127.024612)),
                    captionText = "Marker in Seoul"
                )
            }
            Text("This is MapView")
        }
    }
}
@Composable
fun naverMapContainer() {

}

@Composable
fun customMarker() {
    Marker(
        state = MarkerState(),
        captionText = "Marker in seoul"
    )
}

@Composable
fun centerInformationCard(coVidCenter: CoVidCenter) {

}

@Composable
fun currentPositionButton() {

}


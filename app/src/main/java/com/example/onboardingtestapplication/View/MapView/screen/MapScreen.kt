package com.example.onboardingtestapplication.View.MapView.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onboardingtestapplication.Model.CoVidCenter
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.MarkerState
import com.naver.maps.map.overlay.Marker

@Composable
fun MapScreen() {

}

@Composable
fun naverMap() {

}

@ExperimentalNaverMapApi
@Composable
fun customMarker(coVidCenter: CoVidCenter, clickFunction : (Marker) ->  Boolean) {

    val markerColor = when (coVidCenter.centerType){
        "지역" -> Color.Blue
        "중앙/권역" -> Color.Green
        else -> Color.Yellow

    }

    val markerPosition = LatLng(coVidCenter.lat, coVidCenter.lng)

    Marker(
        state = MarkerState(position = markerPosition),
        captionText = "${coVidCenter.centerName}",
        iconTintColor = markerColor,
        onClick = clickFunction
    )
}

@Composable
fun centerInformationCard(center: CoVidCenter) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Top
    ) {

        Card(modifier = Modifier
            .padding(20.dp)
            .width(150.dp)
            .height(220.dp),
            backgroundColor = Color.DarkGray
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(12.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier =  Modifier.fillMaxWidth().padding(5.dp),
                    horizontalArrangement = Arrangement.Center,

                    ) {
                    Text(text = "코로나 센터 정보", fontSize = 18.sp, color = Color.White)
                }
                centerTextStyle(text = "주소 : ${center.address}")
                centerTextStyle(text = "이름 : ${center.centerName}")
                centerTextStyle(text = "건물 : ${center.facilityName}")
                centerTextStyle(text = "연락처: ${center.phoneNumber}")
                centerTextStyle(text = "업데이트 날짜: ${center.updatedAt}")
            }
        }

    }

}

@Composable
fun centerTextStyle(text : String) {
    Text(text = text, fontSize = 10.sp, modifier = Modifier.padding(5.dp), color = Color.White )
}

@Composable
fun currentPositionButton(onClick : ()-> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End
    ) {
        Button(onClick = onClick, colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)
        ) {
            Text("내 위치", fontSize = 12.sp, color = Color.White)
        }
    }
}
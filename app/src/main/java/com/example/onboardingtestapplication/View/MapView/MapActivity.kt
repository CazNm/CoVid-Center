@file:OptIn(ExperimentalNaverMapApi::class)

package com.example.onboardingtestapplication.View.MapView

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.graphics.PointF
import android.location.Location
import androidx.compose.ui.graphics.Color

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import com.example.onboardingtestapplication.Model.CoVidCenter
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.compose.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MapActivity : ComponentActivity() {

    private val mapViewModel : MapViewModel by viewModels()
    private val locationSource = MutableLiveData<FusedLocationSource>()

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    private val requestLocation = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        result ->
        if (!result) { // 권한 거부 시 로직
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestLocation.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        requestLocation.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        //권한 요청

        locationSource.postValue(FusedLocationSource(this@MapActivity, LOCATION_PERMISSION_REQUEST_CODE))

        MainScope().launch {
           mapViewModel.getCenterData()
       }

        setContent {
            val seoul = LatLng(37.532600, 127.024612)
            val cameraPositionState : CameraPositionState = rememberCameraPositionState {
                position = CameraPosition(seoul, 11.0)
            }

            NaverMap(
                cameraPositionState = cameraPositionState,
                locationSource = locationSource.observeAsState().value,
                modifier = Modifier.fillMaxSize(),
                onMapClick = { point, latLang ->
                    mapViewModel.updateSelectedMarker(null)
                    Log.d("mapView", "screen : $point / mapPos : $latLang")
                }
            ) {

                val context = LocalContext.current

                MapEffect(key1 = context) {
                        naverMap ->
                    naverMap.locationTrackingMode = LocationTrackingMode.Follow
                }

                if(mapViewModel.centerList.observeAsState().value != null) {
                    Log.d("mapView", "${mapViewModel.centerList.value?.size}")
                    for(item in mapViewModel.centerList.value!!) {
                        Log.d("mapView", "$item")

                        customMarker(coVidCenter = item) {
                            mapViewModel.updateSelectedMarker(item)
                            Log.d("mapView", "marker clicked id: ${item.id}")
                            val centerLocation = LatLng(item.lat, item.lng)
                            cameraPositionState.move(CameraUpdate.scrollTo(centerLocation))
                            true
                        }
                    }
                }
            }

            currentPositionButton {
                if(locationSource.value != null) {
                    locationSource.value?.activate {
                        location ->
                        Log.d("mapView", "${location}")
                    }

                    Log.d("mapView","active ${locationSource.value?.isActivated}")
                    Log.d("mapView","last ${locationSource.value?.lastLocation}")

                    val lat = locationSource.value?.lastLocation?.latitude
                    val lng = locationSource.value?.lastLocation?.longitude

                    val currentLocation = LatLng(lat!!, lng!!)
                    cameraPositionState.move(CameraUpdate.scrollTo(currentLocation))

                }
                else{
                    Log.d("mapView","no location source")
                }
            } // 지도 관련한건.. view model 에 어떻게 넣어야할지 모르겠다..

            if(mapViewModel.markerSelect.observeAsState().value == true) {
                mapViewModel.selectedCenterData.observeAsState().value?.let {
                    centerInformationCard(it)
                }
            }
        }
    }
}

@Composable
fun naverMap() {

}

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
     Column(Modifier.fillMaxSize(),
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


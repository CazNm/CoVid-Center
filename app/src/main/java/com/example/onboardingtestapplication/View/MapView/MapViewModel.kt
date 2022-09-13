package com.example.onboardingtestapplication.View.MapView

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.onboardingtestapplication.Model.CoVidCenter
import com.example.onboardingtestapplication.Model.CoVidCenterRepository
import com.naver.maps.map.util.FusedLocationSource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject


@HiltViewModel
class MapViewModel @Inject constructor (private val coVidCenterRepository: CoVidCenterRepository) : ViewModel() {

    private val _centerList = MutableLiveData<List<CoVidCenter>>()
    val centerList : LiveData<List<CoVidCenter>> = _centerList

    private val _markerSelect = MutableLiveData<Boolean>()
    val markerSelect : LiveData<Boolean> = _markerSelect

    private var _selectedMarkerId = -1
    private val _selectedCenterData = MutableLiveData<CoVidCenter?>()

    val selectedCenterData : LiveData<CoVidCenter?> = _selectedCenterData

    init{
        _markerSelect.value = false
    } // 생성 초기화 타이밍은 같은 걸로 보임 , 생성자에서 추가적인 로직을 설정할 수 없으므로 추가적인 로직을 통해 넘어오는
    //값들이 valid 한지 검사하는 것을 직접 구현해서 확인하는 것.

    private fun updateSelectingState(select : Boolean, id : Int , centerData : CoVidCenter?) {
        _markerSelect.postValue(select)
        _selectedMarkerId = id
        _selectedCenterData.postValue(centerData)
    }

    fun updateSelectedMarker(center : CoVidCenter?)  {
        if(center == null) {
            updateSelectingState(false, -1, null)
            return
        }

        if (!markerSelect.value!!){
            updateSelectingState(true, center.id, center)
            Log.d("mapViewModel", "new marker selected: ${center.id}")
        }
        else {
            when(center.id)
            {
                _selectedMarkerId -> {
                    updateSelectingState(false,  -1, null)
                    Log.d("mapViewModel", "same marker selected release it: ${center.id}")
                }
                else -> {
                    updateSelectingState(true,  center.id, center)
                    Log.d("mapViewModel", "different marker selected change it: ${center.id}")
                }
            }
        }
    }

    suspend fun getCenterData() {
        val centerDataList: MutableList<CoVidCenter> = mutableListOf()

        if(centerList.value == null)
        {
            coVidCenterRepository.getCoVidCenterList()
                .map { centerValue->
                    Log.d("mapViewModel", "${centerValue.centerType}")
                    centerValue
                }
                .collect {
                    centerDataList.add(it)
                } // hot stream 으로 바꿔서 써보기

            Log.d("mapViewModel", "${centerDataList.size}")
        }
        else {
            centerList.value!!.map {
                centerDataList.add(it)
            }
        }
        _centerList.postValue(centerDataList)
    }
}


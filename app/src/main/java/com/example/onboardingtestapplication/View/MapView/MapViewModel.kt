package com.example.onboardingtestapplication.View.MapView

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.onboardingtestapplication.Model.CoVidCenter
import com.example.onboardingtestapplication.Model.CoVidCenterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
    }

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

        if (!_markerSelect.value!!){
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

        if(_centerList.value == null)
        {
            coVidCenterRepository.getCoVidCenterList()
                .map { centerValue->
                    Log.d("mapViewModel", "${centerValue.centerType}")
                    centerValue
                }
                .collect {
                    centerDataList.add(it)
                }

            Log.d("mapViewModel", "${centerDataList.size}")
        }
        else {
            _centerList.value!!.map {
                centerDataList.add(it)
            }
        }

        _centerList.postValue(centerDataList)
    }

}


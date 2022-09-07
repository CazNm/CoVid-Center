package com.example.onboardingtestapplication.View.MapView

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.onboardingtestapplication.Model.CoVidCenter
import com.example.onboardingtestapplication.Model.CoVidCenterDataBase
import com.example.onboardingtestapplication.Model.CoVidCenterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


@HiltViewModel
class MapViewModel @Inject constructor (val coVidCenterRepository: CoVidCenterRepository) : ViewModel() {


    private val _centerList = MutableLiveData<List<CoVidCenter>>()
    val centerList : LiveData<List<CoVidCenter>> = _centerList

    private val _markerSelect = MutableLiveData<Boolean>()
    val markerSelect : LiveData<Boolean> = _markerSelect

    private val _selectedMarkerId = MutableLiveData<Int>()
    val selectedMarkerId : LiveData<Int> = _selectedMarkerId

    private val _selectedCenterData = MutableLiveData<CoVidCenter?>()
    val selectedCenterData : LiveData<CoVidCenter?> = _selectedCenterData

    init{
        _markerSelect.value = false
        _selectedMarkerId.value = -1
    }


    fun updateSelectedMarker(center : CoVidCenter?)  {
        if(center == null) {
            _markerSelect.postValue(false)
            _selectedMarkerId.postValue(-1)
            _selectedCenterData.postValue(null)
            return
        }

        if (!_markerSelect.value!!){
            _markerSelect.postValue(true)
            _selectedMarkerId.postValue(center?.id)
            _selectedCenterData.postValue(center)
            Log.d("mapViewModel", "new marker selected: ${center?.id}")
        }
        else {
            when(center.id)
            {
                _selectedMarkerId.value!! -> {
                    _selectedMarkerId.postValue(-1)
                    _markerSelect.postValue(false)
                    _selectedCenterData.postValue(null)

                    Log.d("mapViewModel", "same marker selected release it: ${center.id}")

                }
                else -> {
                    _selectedMarkerId.postValue(center.id)
                    _markerSelect.postValue(true)
                    _selectedCenterData.postValue(center)
                    Log.d("mapViewModel", "different marker selected change it: ${center.id}")

                }
            }

        }
    }

    suspend fun getCenterData() {
        val centerDataList: MutableList<CoVidCenter> = mutableListOf<CoVidCenter>()

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


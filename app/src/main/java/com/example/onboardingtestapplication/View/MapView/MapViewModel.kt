package com.example.onboardingtestapplication.View.MapView

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onboardingtestapplication.Model.CoVidCenter
import com.example.onboardingtestapplication.Model.CoVidCenterRepository
import com.naver.maps.map.util.FusedLocationSource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import okhttp3.internal.threadName
import java.lang.Exception
import javax.inject.Inject


@HiltViewModel
class MapViewModel @Inject constructor (private val coVidCenterRepository: CoVidCenterRepository) : ViewModel() {

    private val centerListBuffer = mutableListOf<CoVidCenter>()

    private val _centerList = MutableLiveData<List<CoVidCenter>>()
    val centerList : LiveData<List<CoVidCenter>> = _centerList

    private val _markerSelect = MutableLiveData<Boolean>()
    val markerSelect : LiveData<Boolean> = _markerSelect

    private var _selectedMarkerId = -1
    private val _selectedCenterData = MutableLiveData<CoVidCenter?>()

    val selectedCenterData : LiveData<CoVidCenter?> = _selectedCenterData

    private var centerListSharedFlow : SharedFlow<List<CoVidCenter>>? = null

    init{
        _markerSelect.postValue(false)

        viewModelScope.launch(CoroutineName("initCoroutine")) {
            initializeListFlow()
            getCenterList()
            Log.d("checkingSequential", "coroutineEnd")
        }

        Log.d("checkingSequential", "initLogicEnd")
    } // 생성 초기화 타이밍은 같은 걸로 보임 , 생성자
    // 에서 추가적인 로직을 설정할 수 없으므로 추가적인 로직을 통해 넘어오는
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
    private suspend fun initializeListFlow() {
        coVidCenterRepository.getCenterData().flowOn(Dispatchers.IO).collect()
        centerListSharedFlow =   coVidCenterRepository.getCenterData().flowOn(Dispatchers.IO).shareIn(viewModelScope, SharingStarted.WhileSubscribed(100), 1)
    }


    suspend fun getCenterList() {
        centerListBuffer.clear()
        centerListSharedFlow!!.map { centerValue ->
            Log.d("mapViewModel", "${centerValue.size}")
            centerValue
        }.collect {
            it.map { center ->
                Log.d("mapViewModel", "center input $center")
                centerListBuffer.add(center)
            }
            Log.d("mapViewModel", "list size ${centerListBuffer.size}")
            _centerList.postValue(centerListBuffer)
        }
        //flow 하단 코드에 로직을 설정하면 계속 실행이 안됨.. 이거는 왜 이럴까.,?
        //stateIn 을 구독하고 있기 때문에 계속 구독중임.. 밑으로 내려오는게 이상한것.
    }
}
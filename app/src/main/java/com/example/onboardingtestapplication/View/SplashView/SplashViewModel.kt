package com.example.onboardingtestapplication.View.SplashView

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onboardingtestapplication.Model.CoVidCenter
import com.example.onboardingtestapplication.Model.CoVidCenterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


@HiltViewModel
class SplashViewModel @Inject constructor(

    private val coVidCenterRepository: CoVidCenterRepository) : ViewModel() {
    private var progressPercent : Float = 0.0f
    private val _progressValue = MutableLiveData(0.0f)
    private val _changeScreen = MutableStateFlow(false)

    private var dataSave = false
    private val requestListIndex = 10
    private var taskDone = 0

    val progressValue : LiveData<Float> = _progressValue
    val changeScreen = _changeScreen

    private fun progressValueLogic(ctx : CoroutineContext)  = CoroutineScope(ctx).launch {
        Log.d("splashViewModel", "launch #3 ${Thread.currentThread().name}")
        delay(1000)
        while (isActive) {
                if (progressPercent >= 0.8f && !dataSave)
                    else {
//                    _progressValue.postValue(_progressValue.value!! + 0.005f) //this is for 2 sec
                    progressPercent += 0.05f //post value 는 ui thread 에서 읽어가서 post value 값이 갱신되지 않으면 문제가 생길수도...
                    _progressValue.postValue(progressPercent)  // this is for my debug fast load
                    delay(10)
                    }

                if(progressPercent >= 1.0f) {
                    progressPercent = 1.0f
                    _progressValue.postValue(1.0f)

                    val centerList = mutableListOf<CoVidCenter>()
                    coVidCenterRepository.getCoVidCenterList().collect {
                        centerList.add(it)
                    }

                    Log.d("center","flow return : $centerList")
                    Log.d("center","list size : ${centerList.size}")
                    Log.d("splashViewModel","launch process done")
                    _changeScreen.emit(true)
                    break
                }
            }
        }

    fun launchProgress() = viewModelScope.launch(Dispatchers.IO) {
        Log.d("splashViewModel", "launch #1 ${Thread.currentThread().name}")

        coVidCenterRepository.removeCenterData()

        for(index in 1..requestListIndex)
            launch {
                coVidCenterRepository.requestCoVidCenterList(index).collect{ data ->
                    Log.d("data", "list size : ${data.size}")
                    Log.d("data", "list : $data")

                    withContext(Dispatchers.Default) {
                        coVidCenterRepository.saveCenterData(data)
                    }

                    taskDone += 1
                    Log.d("splashViewModel", "$taskDone")
                    if(taskDone >= requestListIndex)
                        dataSave = true
                }
            }

        runBlocking {
            progressValueLogic(this.coroutineContext)
        }

        Log.d("splashViewModel", "Logic end")
    }
}
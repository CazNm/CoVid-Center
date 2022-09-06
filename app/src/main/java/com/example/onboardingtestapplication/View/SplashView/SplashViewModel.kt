package com.example.onboardingtestapplication.View.SplashView

import android.view.View
import androidx.lifecycle.*
import com.example.onboardingtestapplication.Model.CoVidCenterRepository
import com.example.onboardingtestapplication.View.MapView.MapActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.concurrent.timer
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext


@HiltViewModel
class SplashViewModel @Inject constructor(
    private val coVidCenterRepository: CoVidCenterRepository) : ViewModel() {
    private val _progressValue = MutableLiveData(0.0f)
    private val _changeScreen = MutableLiveData(false)
    private var dataSave = false

    val progressValue : LiveData<Float> = _progressValue
    val changeScreen : LiveData<Boolean> = _changeScreen

    private fun progressValueLogic(ctx : CoroutineContext)  = CoroutineScope(ctx).launch {
            while (isActive) {
                if (_progressValue.value!! >= 0.8f && !dataSave)
                else if (_progressValue.value!! > 1.0f )
                else {
//                    _progressValue.postValue(_progressValue.value!! + 0.005f) //this is for 2 sec
                    _progressValue.postValue(_progressValue.value!! + 0.05f) // this is for my debug fast load
                    delay(10)
                }

                if(_progressValue.value!! >= 1.0f) {
                    _progressValue.postValue(1.0f)

                    val list = coVidCenterRepository.getCoVidCenterList()
                    println(list)
                    println("list size : ${list.size}")
                    println("launch process done")
                    _changeScreen.postValue(true)
                    break
                }
            }
        }



    fun launchProgress() = viewModelScope.launch(Dispatchers.IO) {
        progressValueLogic(this.coroutineContext)

        runBlocking {
            coVidCenterRepository.removeCenterData()
            for(index in 1..10) {
                launch {
                    coVidCenterRepository.requestCoVidCenterList(index)
                }
            }
        }

        dataSave = true
        println("logic end")
   }
}

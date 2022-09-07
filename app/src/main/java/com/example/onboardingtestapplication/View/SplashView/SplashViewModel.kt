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
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


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

                    val centerList = mutableListOf<CoVidCenter>()
                    coVidCenterRepository.getCoVidCenterList().collect {
                        centerList.add(it)
                    }
                    Log.d("center","flow return : $centerList")
                    Log.d("center","list size : ${centerList.size}")
                    println("launch process done")
                    _changeScreen.postValue(true)
                    break
                }
            }
        }



    fun launchProgress() = viewModelScope.launch(Dispatchers.IO) {
        val centerRequestContext = mutableListOf<CoroutineContext>()

        coVidCenterRepository.removeCenterData()
        for(index in 1..10) { //나중에 async await으로 수정 하면 괜찮을거 같다. 타이밍 문제 b
            launch {
                centerRequestContext.add(this.coroutineContext)
                coVidCenterRepository.requestCoVidCenterList(index)
                delay(5000) //딜레이를 줄이면 리스트에 저장되는 개수가 적어지는 문제가 있다.;; 일단 오늘 나중에 리팩토링;; 다른 기능부터 하자

            }
        }

        delay(500)
        runBlocking {
            progressValueLogic(this.coroutineContext)
            Log.d("dataSave", "item ${centerRequestContext[0].isActive}")
            withContext(Dispatchers.Default) {

                while (!dataSave)
                {
                    Log.d("dataSave", "$dataSave")
                    dataSave = !centerRequestContext.any { it.isActive }
                    Log.d("dataSave", "$dataSave")
                    delay(100)
                }
            }
        }

        println("logic end")
   }
}

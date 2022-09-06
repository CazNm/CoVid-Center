package com.example.onboardingtestapplication.View.MapView

import androidx.lifecycle.ViewModel
import com.example.onboardingtestapplication.Model.CoVidCenter
import com.example.onboardingtestapplication.Model.CoVidCenterDataBase
import com.example.onboardingtestapplication.Model.CoVidCenterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


@HiltViewModel
class MapViewModel @Inject constructor (val coVidCenterRepository: CoVidCenterRepository) : ViewModel() {


    fun getCenterData() : Flow<CoVidCenter> = flow {
        val centerList = coVidCenterRepository.getCoVidCenterList()

        println(centerList.size)
        for (item in centerList)
            emit(item)
    }

}


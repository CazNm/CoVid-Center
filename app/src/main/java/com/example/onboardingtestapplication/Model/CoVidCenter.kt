package com.example.onboardingtestapplication.Model

import androidx.room.*
import com.example.onboardingtestapplication.Model.dao.CoVidCenterDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class CoVidCenterRepository @Inject constructor(val dataBase: CoVidCenterDataBase) {


    fun requestCoVidCenterList(pageIndex: Int) {
        requestCoVidCenterListFlow(pageIndex)
    }

    suspend fun getCoVidCenterList(): List<CoVidCenter> {
        val job = CoroutineScope(Dispatchers.Default).async {
            dataBase.coVidCenterDao().getAll()
        }

        return job.await()
    }

    private fun saveList(list: List<CoVidCenter>) {
       CoroutineScope(Dispatchers.Default).launch {
           for (item in list)
               dataBase.coVidCenterDao().insertCoVidCenter(item)
       }
    }

    fun removeCenterData() {
        CoroutineScope(Dispatchers.Default).launch {
            dataBase.coVidCenterDao().deleteAll()
        }
    }

    private fun requestCoVidCenterListFlow(pageIndex: Int) {
        val pageSize = 10
        val callGetCoVidCenter = RetrofitObject.coVidCenterApi.getCenterList(pageIndex, pageSize)

        callGetCoVidCenter.enqueue(object : Callback<CovidCenterResponse> {
            override fun onResponse(
                call: Call<CovidCenterResponse>,
                response: Response<CovidCenterResponse>
            ) {
                if (response.isSuccessful) {
                    println("CoVidCenterData receive success")
                    println(response.body()?.page)
                    println(response.body()?.currentCount)
                    println(response.body()?.perPage)
                    println("CoVidCenterData receive success")

                    saveList(response.body()!!.data)
                } else {
                    println("CoVidCenterData receive fail")
                    println("CoVidCenterData receive fail")

                }
            }

            override fun onFailure(call: Call<CovidCenterResponse>, t: Throwable) {
                println("CoVidCenterData receive fail")
                println("CoVidCenterData receive fail")

            }
        })


    }
}

data class CovidCenterResponse (
    val currentCount : Int,
    val data : List<CoVidCenter>,
    val matchCount : Int,
    val page : Int,
    val perPage : Int,
    val totalCount : Int
    )

@Entity(tableName = "covidcenter")
data class CoVidCenter @JvmOverloads constructor(
    val address : String?,
    @ColumnInfo(name = "center_name") val centerName : String?,
    @ColumnInfo(name = "center_type") val centerType : String?,
    @ColumnInfo(name = "created_at") val createdAt : String?,
    @ColumnInfo(name = "facility_name") val facilityName : String?,
    @PrimaryKey val id : Int,
    val lat : Double,
    val Long : Double,
    val org : String?,
    @ColumnInfo(name =  "phone_number") val phoneNumber : String?,
    val sido : String?,
    val sigungu : String?,
    @ColumnInfo(name = "updated_at") val updatedAt : String?,
    @ColumnInfo(name = "zip_code") val zipCode : Int
    )// this is model


@Database(entities = [CoVidCenter::class], version = 1)
abstract class CoVidCenterDataBase : RoomDatabase() {
    abstract fun coVidCenterDao() : CoVidCenterDao
}
/*
Sample data
{
  "currentCount": 4,
  "data": [
    {
      "address": "경기도 파주시 와석순환로 415",
      "centerName": "코로나19 경기도 파주시 예방접종센터",
      "centerType": "지역",
      "createdAt": "2021-07-15 00:25:54",
      "facilityName": "운정행복센터",
      "id": 281,
      "lat": "37.7242066",
      "lng": "126.7513156",
      "org": "",
      "phoneNumber": "031-940-5597",
      "sido": "경기도",
      "sigungu": "파주시",
      "updatedAt": "2021-07-16 04:55:59",
      "zipCode": "10894"
    },
    {
      "address": "경기도 남양주시 화도읍 수레로 1259",
      "centerName": "코로나19 경기도 남양주시 예방접종센터",
      "centerType": "지역",
      "createdAt": "2021-07-16 04:55:59",
      "facilityName": "화도체육문화센터",
      "id": 282,
      "lat": "37.6498487",
      "lng": "127.301275",
      "org": "",
      "phoneNumber": "031-590-2598",
      "sido": "경기도",
      "sigungu": "남양주시",
      "updatedAt": "2021-07-16 04:55:59",
      "zipCode": "12178"
    },
    {
      "address": "경기도 시흥시 하중로 226",
      "centerName": "코로나19 경기도 시흥시 예방접종센터",
      "centerType": "지역",
      "createdAt": "2021-07-16 04:55:59",
      "facilityName": "시흥국민체육센터",
      "id": 283,
      "lat": "37.3916531",
      "lng": "126.8053548",
      "org": "",
      "phoneNumber": "031-310-6822",
      "sido": "경기도",
      "sigungu": "시흥시",
      "updatedAt": "2021-07-16 04:55:59",
      "zipCode": "14976"
    },
    {
      "address": "충청남도 당진시 시청1로 30",
      "centerName": "코로나19 충청남도 당진시 예방접종센터",
      "centerType": "지역",
      "createdAt": "2021-07-16 04:56:00",
      "facilityName": "국민체육센터",
      "id": 284,
      "lat": "36.8936392",
      "lng": "126.6443718",
      "org": "",
      "phoneNumber": "041-360-6138",
      "sido": "충청남도",
      "sigungu": "당진시",
      "updatedAt": "2021-07-16 04:56:00",
      "zipCode": "31772"
    }
  ],
  "matchCount": 284,
  "page": 29,
  "perPage": 10,
  "totalCount": 284
}*/
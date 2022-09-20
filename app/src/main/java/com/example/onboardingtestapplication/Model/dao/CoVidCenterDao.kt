package com.example.onboardingtestapplication.Model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.onboardingtestapplication.Model.CoVidCenter
import kotlinx.coroutines.flow.Flow

@Dao
interface CoVidCenterDao {
    @Query("SELECT * FROM covidcenter")
    fun getAll() : List<CoVidCenter>

    @Insert
    fun insertCoVidCenter(coVidCenter: CoVidCenter)

    @Query("DELETE FROM covidcenter")
    fun deleteAll()

}
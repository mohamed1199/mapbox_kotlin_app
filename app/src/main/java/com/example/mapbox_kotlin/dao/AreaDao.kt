package com.example.mapbox_kotlin.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.mapbox_kotlin.model.Area
import com.example.mapbox_kotlin.model.AreaPoint
import com.example.mapbox_kotlin.model.AreaWithPoint


@Dao
interface AreaDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE) //2
     fun insertArea(area: Area):Long?

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE) //2
    fun insertPoint(points: List<AreaPoint>)

    //--------------------------

    @Query("DELETE FROM area_table")
    fun deleteAreas()

    @Query("DELETE FROM point_table")
    fun deletePoints()


    @Transaction
    @Query("SELECT * FROM area_table")
    fun getAreaWithPoints(): LiveData<List<AreaWithPoint>>

    @Query("SELECT * FROM point_table")
    fun getPoints(): LiveData<List<AreaPoint>>

}
package com.example.mapbox_kotlin.repository

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.mapbox_kotlin.dao.AreaDao
import com.example.mapbox_kotlin.model.Area
import com.example.mapbox_kotlin.model.AreaPoint
import com.example.mapbox_kotlin.model.AreaWithPoint
import com.example.mapbox_kotlin.room.MyDatabase

class DataRepo(application: Application) {

    private var areDao: AreaDao? = null

    private var allAreas: LiveData<List<AreaWithPoint>>? = null

     var allPoints: LiveData<List<AreaPoint>>? = null


    fun getAllAreas(): LiveData<List<AreaWithPoint>>? {
        return allAreas
    }


    fun insert(areaWithPoint: AreaWithPoint) {
        var id : Long? = areDao?.insertArea(areaWithPoint.area)
        areaWithPoint.points.forEach {
            if (id != null) {
                it.areaId = id
            }
        }
        areDao?.insertPoint(areaWithPoint.points)
    }

    fun deleteAll() {
        areDao?.deleteAreas()
        areDao?.deletePoints()
    }


    init {
        val db: MyDatabase? = MyDatabase.getDatabase(application)
        areDao = db?.areaDao()
        allAreas = areDao?.getAreaWithPoints()
        allPoints = areDao?.getPoints()
    }

}


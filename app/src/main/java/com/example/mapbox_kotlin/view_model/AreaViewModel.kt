package com.example.mapbox_kotlin.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.mapbox_kotlin.dao.AreaDao
import com.example.mapbox_kotlin.model.AreaPoint
import com.example.mapbox_kotlin.model.AreaWithPoint
import com.example.mapbox_kotlin.repository.DataRepo
import com.example.mapbox_kotlin.room.MyDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AreaViewModel(application: Application) : AndroidViewModel(application) {

    private var dataRepo:DataRepo? = null

    var allPoints: LiveData<List<AreaPoint>>? = null

    private var allAreas: LiveData<List<AreaWithPoint>>? = null

    init {
        dataRepo = DataRepo(application)
        allAreas = dataRepo?.getAllAreas()
        allPoints = dataRepo?.allPoints
    }

    fun getAllAreas():LiveData<List<AreaWithPoint>>?{
        return allAreas
    }

    fun insert( areaWithPoint: AreaWithPoint){
        viewModelScope.launch(Dispatchers.IO){
            dataRepo?.insert(areaWithPoint)
        }
    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO){
            dataRepo?.deleteAll()
        }
    }

}
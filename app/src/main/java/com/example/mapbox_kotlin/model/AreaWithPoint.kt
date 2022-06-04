package com.example.mapbox_kotlin.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation


data class AreaWithPoint(
    @Embedded var area: Area,
    @Relation(
        parentColumn = "areaId",
        entityColumn = "areaId",
    )
    var points: List<AreaPoint>

){
    override fun toString(): String {
        return area.name
    }
}
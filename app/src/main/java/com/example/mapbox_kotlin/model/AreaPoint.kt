package com.example.mapbox_kotlin.model

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import org.jetbrains.annotations.NotNull

@Entity(tableName = "point_table")
data class AreaPoint(
    @PrimaryKey(autoGenerate = true) val pointId: Long,
    @NotNull var areaId: Long,
    @NotNull val longitude: Double,
    @NotNull val latitude: Double,
)
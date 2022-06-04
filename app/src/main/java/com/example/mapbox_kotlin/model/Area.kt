package com.example.mapbox_kotlin.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity(tableName = "area_table")
data class Area(
    @NotNull val name: String,
    @PrimaryKey(autoGenerate = true) val areaId: Long
)
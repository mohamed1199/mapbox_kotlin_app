package com.example.mapbox_kotlin.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mapbox_kotlin.dao.AreaDao
import com.example.mapbox_kotlin.model.Area
import com.example.mapbox_kotlin.model.AreaPoint
import com.example.mapbox_kotlin.model.AreaWithPoint
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Database(entities = [Area::class,AreaPoint::class], version = 1, exportSchema = false)
abstract class MyDatabase : RoomDatabase() {

    abstract fun areaDao(): AreaDao?

    companion object {
        @Volatile
        private var INSTANCE: MyDatabase? = null

        fun getDatabase(context: Context): MyDatabase? {
            val tmpInstance = INSTANCE
            if(tmpInstance != null){
                return tmpInstance
            }
            synchronized(this){
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    MyDatabase::class.java, "areas_database"
                ).build()
                return INSTANCE
            }
        }
    }
}

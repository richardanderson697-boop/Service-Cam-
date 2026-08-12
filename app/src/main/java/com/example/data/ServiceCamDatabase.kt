package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        DowntimeLogEntity::class,
        ApiKeyEntity::class,
        SystemConfigEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ServiceCamDatabase : RoomDatabase() {
    abstract fun dao(): ServiceCamDao

    companion object {
        @Volatile
        private var INSTANCE: ServiceCamDatabase? = null

        fun getDatabase(context: Context): ServiceCamDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ServiceCamDatabase::class.java,
                    "service_cam_db"
                ).fallbackToDestructiveMigration()
                 .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

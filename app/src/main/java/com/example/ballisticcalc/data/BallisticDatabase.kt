// com.example.ballisticcalc.data.BallisticDatabase.kt

package com.example.ballisticcalc.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [WeaponProfileEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class BallisticDatabase : RoomDatabase() {

    abstract fun ballisticDao(): BallisticDao

    companion object {
        @Volatile
        private var INSTANCE: BallisticDatabase? = null

        fun getDatabase(context: Context): BallisticDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BallisticDatabase::class.java,
                    "ballistic_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
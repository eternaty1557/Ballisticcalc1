package com.example.ballisticcalc

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [WeaponProfile::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun weaponDao(): WeaponDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ballisticcalc_database"
                )
                    .fallbackToDestructiveMigration() // ← УДАЛЯЕТ СТАРУЮ БД при изменении схемы
                    .build()

            }
        }
    }
}
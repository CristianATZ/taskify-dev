package com.devtorres.taskalarm.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.devtorres.taskalarm.data.model.Task

@Database(entities = [Task::class], version = 3)
@TypeConverters(Converters::class)
abstract class AppDataBase : RoomDatabase() {
    // Definir DAO
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getInstance(context: Context): AppDataBase {
            /*val MIGRATION_1_2 = object : Migration(2, 3) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    // Agregar la nueva columna 'expired' a la tabla 'tasks'
                    db.execSQL("ALTER TABLE tasks ADD COLUMN expired INTEGER NOT NULL DEFAULT 0")
                }
            }*/


            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "app_database"
                )
                    //.addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

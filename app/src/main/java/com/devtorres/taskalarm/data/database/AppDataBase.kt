package com.devtorres.taskalarm.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.devtorres.taskalarm.data.model.Task

@Database(entities = [Task::class], version = 4)
@TypeConverters(Converters::class)
abstract class AppDataBase : RoomDatabase() {
    // Definir DAO
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getInstance(context: Context): AppDataBase {
            val MIGRATION_2_3 = object : Migration(2, 3) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    // Agregar la nueva columna 'expired' a la tabla 'tasks'
                    db.execSQL("ALTER TABLE tasks ADD COLUMN expired INTEGER NOT NULL DEFAULT 0")
                }
            }
            val MIGRATION_3_4 = object : Migration(3, 4) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    // Agregar la nueva columna 'expired' a la tabla 'tasks'
                    db.execSQL("ALTER TABLE tasks ADD COLUMN subtasks TEXT NOT NULL DEFAULT '[]'")
                }
            }


            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "app_database"
                )
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

package com.devtorres.taskalarm.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.devtorres.taskalarm.data.model.Task

@Database(entities = [Task::class], version = 2)
@TypeConverters(Converters::class)
abstract class AppDataBase : RoomDatabase() {
    // Definir DAO
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDataBase? = null

        // Migración de versión 1 a versión 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Eliminar la tabla antigua
                db.execSQL("DROP TABLE IF EXISTS tasks")

                // Crear una nueva tabla con el esquema actualizado
                db.execSQL("""
            CREATE TABLE tasks (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                isCompleted INTEGER NOT NULL,
                reminder INTEGER NOT NULL DEFAULT 0,
                finishDate INTEGER NOT NULL DEFAULT 0
            )
        """)
            }
        }


        fun getInstance(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "app_database"
                )
                    .addMigrations(MIGRATION_1_2) // Añadir migraciones aquí
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

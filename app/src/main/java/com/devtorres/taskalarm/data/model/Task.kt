package com.devtorres.taskalarm.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.absoluteValue

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    var isCompleted: Boolean,
    val reminder: Boolean,
    val finishDate: LocalDateTime
) {
    // Genera un valor entero único basado en los campos de la clase
    fun toUniqueInt(): Int {
        val hash = "$title$isCompleted$reminder${finishDate}".hashCode()
        // Asegúrate de que el valor sea positivo
        return hash.absoluteValue
    }
}
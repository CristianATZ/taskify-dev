package com.devtorres.taskalarm.data.database

import androidx.room.TypeConverter
import com.devtorres.taskalarm.data.model.SubTask
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDateTime
import java.time.ZoneOffset

class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let { LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.toEpochSecond(ZoneOffset.UTC)
    }

    @TypeConverter
    fun fromSubtaskList(value: List<SubTask>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toSubtaskList(value: String): List<SubTask> {
        val listType = object : TypeToken<List<SubTask>>() {}.type
        return Gson().fromJson(value, listType)
    }
}
package com.example.tracklayoff.core.database

import com.example.tracklayoff.core.database.favorites.*
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.tracklayoff.core.database.util.InstantConverter
import com.example.tracklayoff.features.feed.data.local.dao.CompanyDao
import com.example.tracklayoff.features.feed.data.local.entity.CompanyEntity

@Database(
    entities = [CompanyEntity::class, UserFavoriteEntity::class, FavoriteOutboxEntity::class],
    version = 2,
    exportSchema = true
)
@TypeConverters(InstantConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun companyDao(): CompanyDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun favoriteOutboxDao(): FavoriteOutboxDao

    companion object {
        const val DATABASE_NAME = "track_layoff_db"
    }
}

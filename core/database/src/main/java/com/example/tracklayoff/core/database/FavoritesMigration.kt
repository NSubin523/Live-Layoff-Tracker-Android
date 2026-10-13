package com.example.tracklayoff.core.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val FAVORITES_MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS user_favorites (userId TEXT NOT NULL, companyId TEXT NOT NULL, PRIMARY KEY(userId, companyId))")
        db.execSQL("CREATE TABLE IF NOT EXISTS favorite_outbox (userId TEXT NOT NULL, companyId TEXT NOT NULL, desiredSaved INTEGER NOT NULL, revision TEXT NOT NULL, status TEXT NOT NULL, PRIMARY KEY(userId, companyId))")
    }
}

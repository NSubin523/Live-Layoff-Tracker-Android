package com.example.tracklayoff.core.favorites

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.platform.app.InstrumentationRegistry
import com.example.tracklayoff.core.database.AppDatabase
import com.example.tracklayoff.core.database.FAVORITES_MIGRATION_1_2
import org.junit.*
import org.junit.Assert.*

class FavoritesMigrationTest {
    @get:Rule val migration = MigrationTestHelper(InstrumentationRegistry.getInstrumentation(), AppDatabase::class.java, emptyList(), FrameworkSQLiteOpenHelperFactory())
    @Test fun migrationPreservesFeedAndCreatesAccountScopedTables() {
        val name = "favorites-migration"
        migration.createDatabase(name, 1).apply {
            execSQL("INSERT INTO companies VALUES ('c','Acme',100,'CONFIRMED','Tech','NY',0,NULL,'STABLE')")
            close()
        }
        migration.runMigrationsAndValidate(name, 2, true, FAVORITES_MIGRATION_1_2).apply {
            query("SELECT company_name FROM companies WHERE id='c'").use { assertTrue(it.moveToFirst()); assertEquals("Acme", it.getString(0)) }
            execSQL("INSERT INTO user_favorites VALUES ('a','c'),('b','c')")
            execSQL("INSERT INTO favorite_outbox VALUES ('a','c',0,'revision','Pending')")
            query("SELECT COUNT(*) FROM user_favorites").use { assertTrue(it.moveToFirst()); assertEquals(2, it.getInt(0)) }
            close()
        }
    }
}

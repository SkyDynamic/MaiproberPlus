package io.github.skydynamic.maiproberplus.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import io.github.skydynamic.maiproberplus.core.database.entity.ChuniScoreEntity
import io.github.skydynamic.maiproberplus.core.database.entity.MaimaiScoreEntity
import io.github.skydynamic.maiproberplus.core.database.inteface.ChuniScoreDao
import io.github.skydynamic.maiproberplus.core.database.inteface.MaimaiScoreDao

@Database(
    entities = [
        MaimaiScoreEntity::class,
        ChuniScoreEntity::class
    ],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun maimaiScoreDao() : MaimaiScoreDao
    abstract fun chuniScoreDao() : ChuniScoreDao
}

object DatabaseMigration {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.beginTransaction()
            try {
                database.execSQL("ALTER TABLE chuni_score_entity ADD COLUMN recent INTEGER NOT NULL DEFAULT 0")

                database.execSQL("""
                    CREATE TABLE new_chuni_score_entity (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    title TEXT,
                    difficulty TEXT,
                    score INTEGER,
                    rating REAL,
                    over_power REAL,
                    clear TEXT,
                    full_combo TEXT,
                    full_chain TEXT,
                    rank TEXT,
                    play_time TEXT,
                    upload_time TEXT,
                    recent INTEGER NOT NULL DEFAULT 0
                )
                """.trimIndent())

                database.execSQL("""
                    INSERT INTO new_chuni_score_entity (id, title, difficulty, score, rating, over_power, clear, full_combo, full_chain, rank, play_time, upload_time, recent)
                    SELECT id, title, difficulty, score, rating, over_power, clear, full_combo, full_chain, rank, play_time, upload_time, recent
                    FROM chuni_score_entity
                """.trimIndent())

                database.execSQL("DROP TABLE chuni_score_entity")

                database.execSQL("ALTER TABLE new_chuni_score_entity RENAME TO chuni_score_entity")

                database.setTransactionSuccessful()
            } finally {
                database.endTransaction()
            }
        }
    }

}
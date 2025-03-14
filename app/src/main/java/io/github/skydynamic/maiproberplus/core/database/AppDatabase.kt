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
                        scoreId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        song_id INTEGER NOT NULL DEFAULT -1,
                        title TEXT NOT NULL,
                        level REAL NOT NULL,
                        score INTEGER NOT NULL,
                        rating REAL NOT NULL,
                        version INTEGER NOT NULL,
                        playTime TEXT NOT NULL DEFAULT '',
                        rankType TEXT NOT NULL,
                        diff TEXT NOT NULL,
                        fullComboType TEXT NOT NULL,
                        clearType TEXT NOT NULL,
                        fullChainType TEXT NOT NULL,
                        recent INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())

                database.execSQL("""
                    INSERT INTO new_chuni_score_entity (scoreId, song_id, title, level, score, rating, version, playTime, rankType, diff, fullComboType, clearType, fullChainType, recent)
                    SELECT scoreId, song_id, title, level, score, rating, version, playTime, rankType, diff, fullComboType, clearType, fullChainType, recent
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
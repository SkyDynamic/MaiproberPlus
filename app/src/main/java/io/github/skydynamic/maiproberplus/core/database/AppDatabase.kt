package io.github.skydynamic.maiproberplus.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.skydynamic.maiproberplus.core.database.entity.ChuniScoreEntity
import io.github.skydynamic.maiproberplus.core.database.entity.MaimaiScoreEntity
import io.github.skydynamic.maiproberplus.core.database.inteface.ChuniScoreDao
import io.github.skydynamic.maiproberplus.core.database.inteface.MaimaiScoreDao

@Database(
    entities = [
        MaimaiScoreEntity::class,
        ChuniScoreEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun maimaiScoreDao() : MaimaiScoreDao
    abstract fun chuniScoreDao() : ChuniScoreDao
}
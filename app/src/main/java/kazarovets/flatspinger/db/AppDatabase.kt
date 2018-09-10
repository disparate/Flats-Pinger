package kazarovets.flatspinger.db

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.arch.persistence.room.migration.Migration
import kazarovets.flatspinger.db.dao.FlatsDao
import kazarovets.flatspinger.db.model.DBFlat
import kazarovets.flatspinger.db.model.DBFlatInfo


@Database(entities = arrayOf(DBFlat::class, DBFlatInfo::class), version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun flatsDao(): FlatsDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }
    }
}
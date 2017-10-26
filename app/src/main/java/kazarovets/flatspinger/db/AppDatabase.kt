package kazarovets.flatspinger.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import kazarovets.flatspinger.db.dao.FlatsDao
import kazarovets.flatspinger.db.model.DBFlatInfo
import kazarovets.flatspinger.model.ineedaflat.INeedAFlatFlat
import kazarovets.flatspinger.model.onliner.OnlinerFlat


@Database(entities = arrayOf(INeedAFlatFlat::class, OnlinerFlat::class, DBFlatInfo::class), version = 1)
@TypeConverters(Converters::class)
public abstract class AppDatabase : RoomDatabase() {
    abstract public fun flatsDao(): FlatsDao
}
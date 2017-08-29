package kazarovets.flatspinger.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import kazarovets.flatspinger.model.FlatStatus
import kazarovets.flatspinger.model.Provider


class FlatsDatabase private constructor(context: Context)
    : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private var instance: FlatsDatabase? = null

        @Synchronized
        fun getInstance(context: Context): FlatsDatabase {
            if (instance == null)
                instance = FlatsDatabase(context)

            return instance!!
        }


        val DATABASE_NAME = "FlatsDatabase"
        val TAG = DATABASE_NAME
        val DATABASE_VERSION = 10

        val TABLE_FLATS_STATUSES = "FlatsStatuses"
        val KEY_FLAT_ID = "id"
        val KEY_STATUS = "status"
        val KEY_SEEN = "seen"
        val KEY_PROVIDER = "provider"
    }


    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_STATUSES_TABLE = "CREATE TABLE $TABLE_FLATS_STATUSES" +
                "(" +
                "$KEY_FLAT_ID TEXT," +
                "$KEY_PROVIDER TEXT," +
                "$KEY_STATUS TEXT DEFAULT \"${FlatStatus.REGULAR.name}\"," +
                "$KEY_SEEN INTEGER DEFAULT 0," +
                "PRIMARY KEY ($KEY_FLAT_ID, $KEY_PROVIDER)" +
                ")"
        db?.execSQL(CREATE_STATUSES_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
//        if (newVersion == 7 && oldVersion < newVersion) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_FLATS_STATUSES")
//        }
        onCreate(db)
    }

    private fun addFlatStatus(id: String, status: FlatStatus, provider: Provider) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val values = ContentValues()
            values.put(KEY_FLAT_ID, id)
            values.put(KEY_PROVIDER, provider.name)
            values.put(KEY_STATUS, status.name)

            val res = db.insertWithOnConflict(TABLE_FLATS_STATUSES, null, values, SQLiteDatabase.CONFLICT_IGNORE)
            if (res == -1L) {
                db.update(TABLE_FLATS_STATUSES, values, "$KEY_FLAT_ID = ? AND $KEY_PROVIDER = ?", arrayOf(id, provider.name))
            }
            db.setTransactionSuccessful()
        } catch (ex: Exception) {
            Log.d(TAG, "Error when trying to add a flat status", ex)
        } finally {
            db.endTransaction()
        }
    }

    fun setRegularFlat(id: String, provider: Provider) {
        addFlatStatus(id, FlatStatus.REGULAR, provider)
    }

    fun setFavoriteFlat(id: String, provider: Provider) {
        addFlatStatus(id, FlatStatus.FAVORITE, provider)
    }

    fun setHiddenFlat(id: String, provider: Provider) {
        addFlatStatus(id, FlatStatus.HIDDEN, provider)
    }

    private fun setSeenFlat(id: String, provider: Provider, seen: Boolean) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val values = ContentValues()
            values.put(KEY_FLAT_ID, id)
            values.put(KEY_PROVIDER, provider.name)
            values.put(KEY_SEEN, if (seen) 1 else 0)

            val res = db.insertWithOnConflict(TABLE_FLATS_STATUSES, null, values, SQLiteDatabase.CONFLICT_IGNORE)
            if (res == -1L) {
                db.update(TABLE_FLATS_STATUSES, values, "$KEY_FLAT_ID = ? AND $KEY_PROVIDER = ?", arrayOf(id, provider.name))
            }
            db.setTransactionSuccessful()
        } catch (ex: Exception) {
            Log.d(TAG, "Error when trying to add a flat status", ex)
        } finally {
            db.endTransaction()
        }
    }

    fun setSeenFlat(id: String, provider: Provider) {
        setSeenFlat(id, provider, true)
    }

    fun setNotSeenFlat(id: String, provider: Provider) {
        setSeenFlat(id, provider, false)
    }

    fun isSeenFlat(id: String, provider: Provider): Boolean {
        val db = readableDatabase
        var seen = false
        val cursor = db.query(TABLE_FLATS_STATUSES,
                arrayOf(KEY_SEEN),
                "$KEY_FLAT_ID = ? AND $KEY_PROVIDER = ?",
                arrayOf(id, provider.name),
                null, null, null)
        if (cursor.moveToFirst()) {
            val seenInt = cursor.getInt(cursor.getColumnIndex(KEY_SEEN))
            seen = seenInt > 0
        }
        cursor.close()
        return seen
    }

    fun getFlatStatus(id: String, provider: Provider): FlatStatus {
        val db = readableDatabase
        var status = FlatStatus.REGULAR
        val cursor = db.query(TABLE_FLATS_STATUSES,
                arrayOf(KEY_STATUS),
                "$KEY_FLAT_ID = ? AND $KEY_PROVIDER = ?",
                arrayOf(id, provider.name),
                null, null, null)
        if (cursor.moveToFirst()) {
            val name = cursor.getString(cursor.getColumnIndex(KEY_STATUS))
            status = FlatStatus.valueOf(name)
        }
        cursor.close()
        return status
    }

}
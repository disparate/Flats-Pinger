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
            if (instance == null)  // NOT thread safe!
                instance = FlatsDatabase(context)

            return instance!!
        }


        val DATABASE_NAME = "FlatsDatabase"
        val TAG = DATABASE_NAME
        val DATABASE_VERSION = 3

        val TABLE_FLATS_STATUSES = "FlatsStatuses"
        val KEY_FLAT_ID = "id"
        val KEY_STATUS = "status"
        val KEY_PROVIDER = "provider"
    }


    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_STATUSES_TABLE = "CREATE TABLE $TABLE_FLATS_STATUSES" +
                "(" +
                "$KEY_FLAT_ID TEXT," +
                "$KEY_STATUS TEXT," +
                "$KEY_PROVIDER TEXT" +
                ")"
        db?.execSQL(CREATE_STATUSES_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (newVersion == 3 && oldVersion < newVersion) {
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_FLATS_STATUSES")
        }
        onCreate(db)
    }

    private fun addFlatWithStatus(id: String, status: FlatStatus, provider: Provider) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val values = ContentValues()
            values.put(KEY_FLAT_ID, id)
            values.put(KEY_PROVIDER, provider.name)
            values.put(KEY_STATUS, status.name)

            db.insert(TABLE_FLATS_STATUSES, null, values)
            db.setTransactionSuccessful()
        } catch (ex: Exception) {
            Log.d(TAG, "Error when trying to add a flat status", ex)
        } finally {
            db.endTransaction()
        }
    }

    fun setSeenFlat(id: String, provider: Provider) {
        addFlatWithStatus(id, FlatStatus.SEEN, provider)
    }

    fun setFavoriteFlat(id: String, provider: Provider) {
        addFlatWithStatus(id, FlatStatus.FAVORITE, provider)
    }

    fun setHiddenFlat(id: String, provider: Provider) {
        addFlatWithStatus(id, FlatStatus.HIDDEN, provider)
    }

    fun setNotSeenFlat(id: String, provider: Provider) {
        addFlatWithStatus(id, FlatStatus.NOT_SEEN, provider)
    }

    fun getFlatStatus(id: String, provider: Provider): FlatStatus {
        val db = readableDatabase
        var status = FlatStatus.NOT_SEEN
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
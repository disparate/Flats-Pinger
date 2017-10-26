package kazarovets.flatspinger.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import kazarovets.flatspinger.db.model.DBFlatInfo
import kazarovets.flatspinger.model.FlatStatus
import kazarovets.flatspinger.model.Provider
import kazarovets.flatspinger.model.ineedaflat.INeedAFlatFlat
import kazarovets.flatspinger.model.onliner.OnlinerFlat


@Dao
interface FlatsDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun addFlatStatus(dbFlatInfo: DBFlatInfo)

    //    @Query("UPDATE DBFlatInfo SET status = :status WHERE flat_id = :id AND provider = :provider")
    @Query("UPDATE DBFlatInfo SET status = :arg1 WHERE flat_id = :arg0 AND provider = :arg2")
    fun updateFlatStatus(id: String, status: FlatStatus, provider: Provider)

    //    @Query("UPDATE DBFlatInfo SET is_seen = :isSeen WHERE flat_id = :id AND provider = :provider")
    @Query("UPDATE DBFlatInfo SET is_seen = :arg1 WHERE flat_id = :arg0 AND provider = :arg2")
    fun updateFlatIsSeen(id: String, isSeen: Boolean, provider: Provider)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun addOnlinerFlat(flat: OnlinerFlat)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun addINeedAFlatFlat(flat: INeedAFlatFlat)


}
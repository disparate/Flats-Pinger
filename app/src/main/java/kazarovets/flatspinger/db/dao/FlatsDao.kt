package kazarovets.flatspinger.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Flowable
import kazarovets.flatspinger.db.model.DBFlatInfo
import kazarovets.flatspinger.db.model.INeedAFlatFlatInfo
import kazarovets.flatspinger.db.model.OnlinerFlatInfo
import kazarovets.flatspinger.model.FlatStatus
import kazarovets.flatspinger.model.Provider
import kazarovets.flatspinger.model.ineedaflat.INeedAFlatFlat
import kazarovets.flatspinger.model.onliner.OnlinerFlat


@Dao
interface FlatsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addFlatStatus(dbFlatInfo: DBFlatInfo)

    //    @Query("UPDATE DBFlatInfo SET status = :status WHERE flat_id = :id AND provider = :provider")
    @Query("UPDATE DBFlatInfo SET status = :arg1 WHERE flat_id = :arg0 AND provider = :arg2")
    fun updateFlatStatus(id: String, status: FlatStatus, provider: Provider)

    //    @Query("UPDATE DBFlatInfo SET is_seen = :isSeen WHERE flat_id = :id AND provider = :provider")
    @Query("UPDATE DBFlatInfo SET is_seen = :arg1 WHERE flat_id = :arg0 AND provider = :arg2")
    fun updateFlatIsSeen(id: String, isSeen: Boolean, provider: Provider)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addOnlinerFlats(flats: List<OnlinerFlat>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addINeedAFlatFlats(flats: List<INeedAFlatFlat>)

    @Query("SELECT F.*, FI.status, FI.is_seen, FI.provider from OnlinerFlat F LEFT OUTER JOIN DBFlatInfo FI ON FI.flat_id = id WHERE FI.provider LIKE \"ONLINER\" OR FI.provider IS NULL")
    fun getOnlinerFlats(): Flowable<List<OnlinerFlatInfo>>

    @Query("SELECT F.*, FI.status, FI.is_seen, FI.provider from INeedAFlatFlat F LEFT OUTER JOIN DBFlatInfo FI ON FI.flat_id = id WHERE FI.provider LIKE \"I_NEED_A_FLAT\" OR FI.provider IS NULL")
    fun getINeedAFlatFlats(): Flowable<List<INeedAFlatFlatInfo>>
}
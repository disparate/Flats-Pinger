package kazarovets.flatspinger.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import io.reactivex.Flowable
import kazarovets.flatspinger.db.model.DBFlat
import kazarovets.flatspinger.db.model.DBFlatInfo
import kazarovets.flatspinger.model.FlatStatus
import kazarovets.flatspinger.model.Provider


@Dao
interface FlatsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addFlatStatus(dbFlatInfo: DBFlatInfo)

    @Query("UPDATE DBFlatInfo SET status = :status WHERE flat_id = :id AND provider = :provider")
    fun updateFlatStatus(id: String, status: FlatStatus, provider: Provider)

    @Query("UPDATE DBFlatInfo SET is_seen = :isSeen WHERE flat_id = :id AND provider = :provider")
    fun updateFlatIsSeen(id: String, isSeen: Boolean, provider: Provider)

    @Query("SELECT * FROM DBFlatInfo WHERE flat_id = :id AND provider = :provider")
    fun getFlatInfo(id: String, provider: Provider): LiveData<DBFlatInfo>

    @Query("SELECT * FROM DBFlatInfo WHERE status <> :status")
    fun getFlatsExcludingStatusFlowable(status: FlatStatus): Flowable<List<DBFlatInfo>>

    @Query("SELECT * FROM DBFlatInfo WHERE is_seen = :isSeen")
    fun getSeenFlatsFlowable(isSeen: Boolean): Flowable<List<DBFlatInfo>>

    @Query("SELECT * FROM DBFlatInfo")
    fun getDBFlatInfosFlowable(): Flowable<List<DBFlatInfo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addFavoriteFlat(flat: DBFlat)

    @Delete
    fun removeFavoriteFlat(flat: DBFlat)

    @Query("SELECT * FROM DBFlat")
    fun getFavoriteFlats(): Flowable<List<DBFlat>>
}
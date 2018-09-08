package kazarovets.flatspinger.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Flowable
import kazarovets.flatspinger.db.model.DBFlatInfo
import kazarovets.flatspinger.model.FlatStatus
import kazarovets.flatspinger.model.Provider
import kazarovets.flatspinger.model.ineedaflat.DBFlat
import kazarovets.flatspinger.model.onliner.OnlinerFlat


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

    @Query("SELECT * FROM DBFlatInfo WHERE status = :status")
    fun getFlatsByStatus(status: FlatStatus): LiveData<List<DBFlatInfo>>

    @Query("SELECT * FROM DBFlatInfo WHERE status <> :status")
    fun getFlatsExcludingStatusFlowable(status: FlatStatus): Flowable<List<DBFlatInfo>>

    @Query("SELECT * FROM DBFlatInfo WHERE is_seen = :isSeen")
    fun getSeenFlatsFlowable(isSeen: Boolean): Flowable<List<DBFlatInfo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addFavoriteOnlinerFlat(flats: OnlinerFlat)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addFavoriteFlat(flat: DBFlat)

    @Query("SELECT DBFlat.*")
    fun getFavoriteFlats(): Flowable<List<DBFlat>>
}
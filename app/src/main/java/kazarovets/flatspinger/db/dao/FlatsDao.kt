package kazarovets.flatspinger.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import io.reactivex.Flowable
import kazarovets.flatspinger.db.model.DBFlat
import kazarovets.flatspinger.db.model.DBFlatInfo
import kazarovets.flatspinger.model.Provider


@Dao
interface FlatsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addOrReplaceFlatStatus(dbFlatInfo: DBFlatInfo)

    @Query("SELECT * FROM DBFlatInfo WHERE flat_id = :id AND provider = :provider")
    fun getFlatInfo(id: String, provider: Provider): LiveData<DBFlatInfo>

    @Query("SELECT * FROM DBFlatInfo WHERE is_hidden = 1")
    fun getHiddenFlatsFlowable(): Flowable<List<DBFlatInfo>>

    @Query("SELECT * FROM DBFlatInfo WHERE is_seen = 1")
    fun getSeenFlatsFlowable(): Flowable<List<DBFlatInfo>>

    @Query("SELECT * FROM DBFlatInfo")
    fun getDBFlatInfosFlowable(): Flowable<List<DBFlatInfo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addFavoriteFlat(flat: DBFlat)

    @Query("DELETE FROM DBFlat WHERE id = :flatId")
    fun removeFavoriteFlat(flatId: String)

    @Query("DELETE FROM DBFlat WHERE url = :imageUrl")
    fun removeFavoriteFlatByImageUrl(imageUrl: String?)

    @Query("SELECT * FROM DBFlat")
    fun getFavoriteFlats(): Flowable<List<DBFlat>>
}
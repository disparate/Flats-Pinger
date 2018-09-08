package kazarovets.flatspinger.db.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import kazarovets.flatspinger.model.Flat
import kazarovets.flatspinger.model.FlatStatus
import kazarovets.flatspinger.model.Provider


@Entity(primaryKeys = arrayOf("flat_id", "provider"))
class DBFlatInfo(@ColumnInfo(name = "status") var status: FlatStatus = FlatStatus.REGULAR,
                 @ColumnInfo(name = "is_seen") var isSeen: Boolean = false,
                 @ColumnInfo(name = "provider") var provider: Provider = Provider.I_NEED_A_FLAT,
                 @ColumnInfo(name = "flat_id") var flatId: String = "") {

    fun isInfoFor(flat: Flat): Boolean {
        return flat.id == flatId && flat.provider == provider
    }
}
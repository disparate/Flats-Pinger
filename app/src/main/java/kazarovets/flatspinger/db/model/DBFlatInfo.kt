package kazarovets.flatspinger.db.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import kazarovets.flatspinger.model.Flat
import kazarovets.flatspinger.model.Provider


@Entity(primaryKeys = arrayOf("flat_id", "provider"))
class DBFlatInfo(@ColumnInfo(name = "is_hidden") var isHidden: Boolean = false,
                 @ColumnInfo(name = "is_seen") var isSeen: Boolean = false,
                 @ColumnInfo(name = "provider") var provider: Provider = Provider.I_NEED_A_FLAT,
                 @ColumnInfo(name = "flat_id") var flatId: String = "",
                 @ColumnInfo(name = "image_url") var imageUrl: String = "") {

    fun isSameIdAndProvider(flat: Flat): Boolean {
        return (flat.id == flatId && flat.provider == provider)
    }

    fun isSameImageUrl(flat: Flat): Boolean {
        return imageUrl.isNotBlank() && imageUrl == flat.imageUrl
    }

    companion object {
        private fun createFromFlat(flat: Flat, isHidden: Boolean, isSeen: Boolean): DBFlatInfo {
            return DBFlatInfo(isHidden = isHidden,
                    isSeen = isSeen,
                    provider = flat.provider,
                    flatId = flat.id,
                    imageUrl = flat.imageUrl ?: "")
        }

        @JvmStatic
        fun createSeenFromFlat(flat: Flat): DBFlatInfo {
            return createFromFlat(flat, false, true)
        }

        @JvmStatic
        fun createHiddenFromFlat(flat: Flat): DBFlatInfo {
            return createFromFlat(flat, true, false)
        }
    }
}
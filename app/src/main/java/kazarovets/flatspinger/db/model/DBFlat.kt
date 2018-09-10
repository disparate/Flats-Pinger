package kazarovets.flatspinger.db.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull
import kazarovets.flatspinger.model.Flat
import kazarovets.flatspinger.model.Provider
import kazarovets.flatspinger.model.RentType


@Entity
class DBFlat(@PrimaryKey @ColumnInfo(name = "id") @NonNull override var id: String = "",
                  @ColumnInfo(name = "url") override var imageUrl: String? = null,
                  @ColumnInfo(name = "images") override var images: List<String> = emptyList(),
                  @ColumnInfo(name = "address") override var address: String = "",
                  @ColumnInfo(name = "cost") override var costInDollars: Int = 0,
                  @ColumnInfo(name = "original_url") override var originalUrl: String? = null,
                  @ColumnInfo(name = "latitude") override var latitude: Double? = null,
                  @ColumnInfo(name = "longitude") override var longitude: Double? = null,
                  @ColumnInfo(name = "isOwner") override var isOwner: Boolean = false,
                  @ColumnInfo(name = "rentType") override var rentType: RentType = RentType.NONE,
                  @ColumnInfo(name = "provider") override var provider: Provider = Provider.I_NEED_A_FLAT,
                  @ColumnInfo(name = "updated_time") override var updatedTime: Long = 0,
                  @ColumnInfo(name = "created_time") override var createdTime: Long = 0,
                  @ColumnInfo(name = "source") override var source: String = "",
                  @ColumnInfo(name = "description") override var description: String = "",
                  @ColumnInfo(name = "phone") override var phones: List<String> = emptyList()) : Flat {

    companion object {
        @JvmStatic
        fun createFromFlat(flat: Flat): DBFlat {
            return DBFlat(
                    id = flat.id,
                    imageUrl = flat.imageUrl,
                    images = flat.images,
                    address = flat.address,
                    costInDollars = flat.costInDollars,
                    originalUrl = flat.originalUrl,
                    latitude = flat.latitude,
                    longitude = flat.longitude,
                    isOwner = flat.isOwner,
                    rentType = flat.rentType,
                    provider = flat.provider,
                    updatedTime = flat.updatedTime,
                    createdTime = flat.createdTime,
                    source = flat.source,
                    description = flat.description,
                    phones = flat.phones
            )
        }
    }

}
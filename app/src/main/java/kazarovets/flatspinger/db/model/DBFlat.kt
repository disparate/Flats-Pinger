package kazarovets.flatspinger.db.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull
import com.google.gson.annotations.SerializedName
import kazarovets.flatspinger.model.Flat
import kazarovets.flatspinger.model.Provider
import kazarovets.flatspinger.model.RentType


@Entity
data class DBFlat(@PrimaryKey @SerializedName("id") @ColumnInfo(name = "id") @NonNull override var id: String = "",
                  @SerializedName("url") override var imageUrl: String? = null,
                  @SerializedName("images") override var images: List<String> = emptyList(),
                  @SerializedName("address") override var address: String = "",
                  @SerializedName("cost") override var costInDollars: Int = 0,
                  @SerializedName("original_url") override var originalUrl: String? = null,
                  @SerializedName("latitude") override var latitude: Double? = null,
                  @SerializedName("longitude") override var longitude: Double? = null,
                  @SerializedName("isOwner") override var isOwner: Boolean = false,
                  @SerializedName("rentType") override var rentType: RentType = RentType.NONE,
                  @SerializedName("provider") override var provider: Provider = Provider.I_NEED_A_FLAT,
                  @SerializedName("updated_time") override var updatedTime: Long = 0,
                  @SerializedName("created_time") override var createdTime: Long = 0,
                  @SerializedName("source") override var source: String = "",
                  @SerializedName("description") override var description: String = "",
                  @SerializedName("phone") override var phones: List<String> = emptyList()) : Flat {

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
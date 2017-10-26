package kazarovets.flatspinger.model.ineedaflat

import com.google.gson.annotations.SerializedName
import kazarovets.flatspinger.model.ineedaflat.INeedAFlatFlat


class INeedAFlatListResponse {
    @SerializedName("items")
    var items: List<INeedAFlatFlat>? = null
}
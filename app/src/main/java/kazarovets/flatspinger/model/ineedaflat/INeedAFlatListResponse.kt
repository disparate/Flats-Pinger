package kazarovets.flatspinger.model.ineedaflat

import com.google.gson.annotations.SerializedName


class INeedAFlatListResponse {
    @SerializedName("items")
    var items: List<INeedAFlatFlat>? = null
}
package kazarovets.flatspinger.model

import com.google.gson.annotations.SerializedName


class INeedAFlatListResponse {
    @SerializedName("items")
    var items: List<INeedAFlatFlat>? = null
}
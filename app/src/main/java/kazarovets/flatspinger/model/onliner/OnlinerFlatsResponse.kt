package kazarovets.flatspinger.model.onliner

import com.google.gson.annotations.SerializedName
import kazarovets.flatspinger.model.onliner.OnlinerFlat


class OnlinerFlatsResponse {

    @SerializedName("apartments")
    private val flats: List<OnlinerFlat>? = null

    val flatsList: List<OnlinerFlat>
        get() = flats?: emptyList()
}
package kazarovets.flatspinger.model

import com.google.gson.annotations.SerializedName


class OnlinerFlatsResponse {

    @SerializedName("apartments")
    private val flats: List<OnlinerFlat>? = null

    val flatsList: List<OnlinerFlat>
        get() = flats?: emptyList()
}
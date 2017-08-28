package kazarovets.flatspinger.model

import android.content.Context
import kazarovets.flatspinger.R


interface Tag {
    fun getString(context: Context): String

    fun matches(flat: Flat) = false

    fun isFilter(): Boolean

    fun isEmpty() = false

    fun isNonEmpty() = !isEmpty()
}


class StringTag(private val value: String?) : Tag {
    override fun getString(context: Context): String = value.toString()

    override fun isFilter(): Boolean = false

    override fun isEmpty(): Boolean = value.isNullOrBlank()
}

class RentTypeTag(private val type: RentType) : Tag {
    override fun getString(context: Context): String = when (type) {
        RentType.FLAT_1_ROOM -> context.getString(R.string.tag_rent_1k)
        RentType.FLAT_2_ROOM -> context.getString(R.string.tag_rent_2k)
        RentType.FLAT_3_ROOM -> context.getString(R.string.tag_rent_3k)
        RentType.FLAT_4_ROOM -> context.getString(R.string.tag_rent_4k)
        RentType.NONE -> ""
    }

    override fun isEmpty() = type == RentType.NONE

    override fun isFilter() = true

    override fun matches(flat: Flat): Boolean = flat.getRentType() == type
}

class SubwayTag(private val subway: Subway?) : Tag {
    override fun getString(context: Context) = subway?.name ?: ""

    override fun isFilter() = true

    override fun matches(flat: Flat): Boolean = flat.getNearestSubway() == subway

    override fun isEmpty(): Boolean = subway == null
}

class OwnerTag(private val isOwner: Boolean) : Tag {
    override fun getString(context: Context) = context.getString(if (isOwner) R.string.owner else R.string.agent)

    override fun isFilter(): Boolean = true

    override fun matches(flat: Flat): Boolean = flat.isOwner() == isOwner

}


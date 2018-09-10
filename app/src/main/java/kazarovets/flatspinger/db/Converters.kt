package kazarovets.flatspinger.db

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import kazarovets.flatspinger.model.FlatStatus
import kazarovets.flatspinger.model.Provider
import kazarovets.flatspinger.model.RentType


class Converters {

    companion object {
        val gson: Gson = Gson()
    }

    @TypeConverter
    fun statusFromString(name: String?): FlatStatus =
            FlatStatus.values().find { it.name.equals(name) } ?: FlatStatus.REGULAR

    @TypeConverter
    fun stringFromFlatStatus(status: FlatStatus): String = status.name


    @TypeConverter
    fun providerFromString(name: String?): Provider =
            Provider.values().find { it.name.equals(name) } ?: Provider.I_NEED_A_FLAT

    @TypeConverter
    fun stringFromProvider(provider: Provider): String = provider.name

    @TypeConverter
    fun listDoubleFromString(string: String?): List<Double> {
        if (string.isNullOrEmpty()) {
            return emptyList()
        }
        return string!!.split(",").map { it.toDouble() }
    }

    @TypeConverter
    fun stringFromListDouble(list: List<String>?): String {
        if (list == null || list.isEmpty()) {
            return ""
        }
        return list.joinToString(",")
    }

    @TypeConverter
    fun listStringFromString(string: String?): List<String> {
        if (string.isNullOrEmpty()) {
            return emptyList()
        }
        return string!!.split(",")
    }


    @TypeConverter
    fun stringFromListString(list: List<Double>?): String {
        if (list == null || list.isEmpty()) {
            return ""
        }
        return list.joinToString(",")
    }

    @TypeConverter
    fun rentTypeFromInt(int: Int?): RentType {
        return when (int) {
            1 -> RentType.FLAT_1_ROOM
            2 -> RentType.FLAT_2_ROOM
            3 -> RentType.FLAT_3_ROOM
            4 -> RentType.FLAT_4_ROOM_OR_MORE
            else -> RentType.NONE
        }
    }

    @TypeConverter
    fun intFromRentType(rentType: RentType): Int? {
        return when(rentType) {
            RentType.FLAT_1_ROOM -> 1
            RentType.FLAT_2_ROOM -> 2
            RentType.FLAT_3_ROOM -> 3
            RentType.FLAT_4_ROOM_OR_MORE -> 4
            RentType.NONE -> 0
        }
    }
}
package kazarovets.flatspinger.db

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import kazarovets.flatspinger.model.FlatStatus
import kazarovets.flatspinger.model.Provider


public class Converters {

    companion object {
        val gson: Gson = Gson()
    }

    @TypeConverter
    public fun statusFromString(name: String?): FlatStatus =
            FlatStatus.values().find { it.name.equals(name) } ?: FlatStatus.REGULAR

    @TypeConverter
    public fun stringFromFlatStatus(status: FlatStatus): String = status.name


    @TypeConverter
    public fun providerFromString(name: String?): Provider =
            Provider.values().find { it.name.equals(name) } ?: Provider.I_NEED_A_FLAT

    @TypeConverter
    public fun stringFromProvider(provider: Provider): String = provider.name

    @TypeConverter
    public fun listDoubleFromString(string: String?): List<Double> {
        if (string.isNullOrEmpty()) {
            return emptyList()
        }
        return string!!.split(",").map { it.toDouble() }
    }

    @TypeConverter
    public fun stringFromListDouble(list: List<String>?): String {
        if (list == null || list.isEmpty()) {
            return ""
        }
        return list.joinToString(",")
    }

    @TypeConverter
    public fun listStringFromString(string: String?): List<String> {
        if (string.isNullOrEmpty()) {
            return emptyList()
        }
        return string!!.split(",")
    }


    @TypeConverter
    public fun stringFromListString(list: List<Double>?): String {
        if (list == null || list.isEmpty()) {
            return ""
        }
        return list.joinToString(",")
    }

}
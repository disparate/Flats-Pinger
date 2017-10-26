package kazarovets.flatspinger.db

import android.arch.persistence.room.TypeConverter
import kazarovets.flatspinger.model.FlatStatus
import kazarovets.flatspinger.model.Provider


public class Converters {

    @TypeConverter
    public fun statusFromString(name: String): FlatStatus =
            FlatStatus.values().find { it.name.equals(name) } ?: FlatStatus.REGULAR

    @TypeConverter
    public fun stringFromFlatStatus(status: FlatStatus): String = status.name


    @TypeConverter
    public fun providerFromString(name: String): Provider =
            Provider.values().find { it.name.equals(name) } ?: Provider.I_NEED_A_FLAT

    @TypeConverter
    public fun stringFromProvider(provider: Provider): String = provider.name
}
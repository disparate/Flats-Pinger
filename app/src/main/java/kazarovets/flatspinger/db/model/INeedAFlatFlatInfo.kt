package kazarovets.flatspinger.db.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Embedded
import kazarovets.flatspinger.model.Flat
import kazarovets.flatspinger.model.FlatInfo
import kazarovets.flatspinger.model.FlatStatus
import kazarovets.flatspinger.model.Provider
import kazarovets.flatspinger.model.ineedaflat.INeedAFlatFlat


class INeedAFlatFlatInfo(@Embedded var flat: INeedAFlatFlat = INeedAFlatFlat(),
                         @ColumnInfo(name = "status") var status: FlatStatus = FlatStatus.REGULAR,
                         @ColumnInfo(name = "is_seen") var isSeen: Boolean = false,
                         @ColumnInfo(name = "provider") var dbProvider: Provider? = Provider.I_NEED_A_FLAT)
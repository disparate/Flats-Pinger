package kazarovets.flatspinger.db.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Embedded
import kazarovets.flatspinger.model.Flat
import kazarovets.flatspinger.model.FlatInfo
import kazarovets.flatspinger.model.FlatStatus
import kazarovets.flatspinger.model.onliner.OnlinerFlat


class OnlinerFlatInfo(@Embedded override val flat: OnlinerFlat,
                      @ColumnInfo(name = "status") override val status: FlatStatus,
                      @ColumnInfo(name = "is_seen") override val isSeen: Boolean) : FlatInfo, Flat by flat
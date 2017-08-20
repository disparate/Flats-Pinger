package kazarovets.flatspinger.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import kazarovets.flatspinger.model.FlatFilter
import kazarovets.flatspinger.model.RentType


object PreferenceUtils {

    lateinit var context: Context
    val prefs: SharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(context) }

    val FILTER_MIN_COST_USD = "filter_min_cost_usd"
    val FILTER_MAX_COST_USD = "filter_max_cost_usd"
    val FILTER_AGENCY_ALLOWED = "filter_agency_allowed"
    val FILTER_SUBWAYS_IDS = "filter_subways_ids"
    val FILTER_RENT_TYPES = "filter_rent_types"

    val TAG = "PreferenceUtils"

    val FLATS_FILTER_JSON = "flats_filter_json"


    fun init(context: Context) {
        this.context = context.applicationContext
    }

    var minCost: Int = 0
        get() {
            field = prefs.getInt(FILTER_MIN_COST_USD, 0)
            return field
        }
        set(value) {
            field = value
            prefs.edit().putInt(FILTER_MIN_COST_USD, value).apply()
        }


    var maxCost: Int = 0
        get() {
            field = prefs.getInt(FILTER_MAX_COST_USD, 0)
            return field
        }
        set(value) {
            field = value
            prefs.edit().putInt(FILTER_MAX_COST_USD, value).apply()
        }

    var allowAgency: Boolean = false
        get() {
            field = prefs.getBoolean(FILTER_AGENCY_ALLOWED, false)
            return field
        }
        set(value) {
            field = value
            prefs.edit().putBoolean(FILTER_AGENCY_ALLOWED, value).apply()
        }

    var subwayIds: MutableSet<Int> = HashSet()
        get() {
            val stringSet = prefs.getStringSet(FILTER_SUBWAYS_IDS, HashSet<String>())
            field = HashSet()
            for (string in stringSet) {
                field.add(string.toInt())
            }
            return field
        }
        set(value) {
            field = value
            val set = HashSet<String>()
            for (id in field) {
                set.add(id.toString())
            }
            prefs.edit().putStringSet(FILTER_SUBWAYS_IDS, set).apply()
        }

    var rentTypes: MutableSet<RentType> = HashSet()
        get() {
            val stringSet = prefs.getStringSet(FILTER_RENT_TYPES, HashSet<String>())
            field = HashSet()
            for (string in stringSet) {
                try {
                    val rentType = RentType.valueOf(string)
                    field.add(rentType)
                } catch (e: Exception) {
                    Log.e(TAG, "Exception parsing rent types", e)
                }
            }
            return field
        }
        set(value) {
            field = value
            val set = HashSet<String>()
            for (type in field) {
                set.add(type.name)
            }
            prefs.edit().putStringSet(FILTER_SUBWAYS_IDS, set).apply()
        }

    var flatFilter: FlatFilter? = null
        get() {
            field = FlatFilter(minCost = minCost,
                    maxCost = maxCost,
                    subwaysIds = subwayIds,
                    agencyAllowed = allowAgency,
                    rentTypes = rentTypes)
            return field

        }
        set(value) {
            if(value != null) {
                field = value
                minCost = value.minCost
                maxCost = value.maxCost
                subwayIds = value.subwaysIds
                allowAgency = value.agencyAllowed
                rentTypes = value.rentTypes
            }
        }
}
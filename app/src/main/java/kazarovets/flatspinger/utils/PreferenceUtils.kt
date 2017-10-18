package kazarovets.flatspinger.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import kazarovets.flatspinger.FlatsApplication
import kazarovets.flatspinger.model.FlatFilter
import kazarovets.flatspinger.model.RentType


object PreferenceUtils {

    lateinit var context: Context
    val prefs: SharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(context) }

    val FILTER_MIN_COST_USD = "filter_min_cost_usd"
    val FILTER_MAX_COST_USD = "filter_max_cost_usd"
    val FILTER_AGENCY_ALLOWED = "filter_agency_allowed"
    val FILTER_WITH_PHOTOS_ONLY = "filter_with_photos_only"
    val FILTER_SUBWAYS_IDS = "filter_subways_ids"
    val FILTER_RENT_TYPES = "filter_rent_types"
    val FILTER_MAX_DISTANCE_TO_SUBWAY = "filter_max_dist_to_subway"
    val FILTER_KEYWORDS = "filter_keywords"
    val FILTER_ROOM_NUMBER = "filter_room_number"

    val SETTINGS_DAYS_AD_IS_ACTUAL = "days_ad_is_actual"
    val SETTINGS_SHOW_SEEN_FLATS = "show_seen_flats"
    val SETTINGS_ENABLE_NOTIFICATONS = "enable_notifications"

    val TAG = "PreferenceUtils"

    val FLATS_FILTER_JSON = "flats_filter_json"


    fun init(context: Context) {
        this.context = context.applicationContext
    }

    var minCost: Int? = null
        get() {
            field = getNullableInt(FILTER_MIN_COST_USD)
            return field
        }
        set(value) {
            field = value
            putNullableValue(FILTER_MIN_COST_USD, value)
        }


    var maxCost: Int? = null
        get() {
            field = getNullableInt(FILTER_MAX_COST_USD)
            return field
        }
        set(value) {
            field = value
            putNullableValue(FILTER_MAX_COST_USD, value)
        }

    var allowAgency: Boolean = true
        get() {
            field = prefs.getBoolean(FILTER_AGENCY_ALLOWED, true)
            return field
        }
        set(value) {
            field = value
            prefs.edit().putBoolean(FILTER_AGENCY_ALLOWED, value).apply()
        }

    var allowPhotosOnly: Boolean = false
        get() {
            field = prefs.getBoolean(FILTER_WITH_PHOTOS_ONLY, false)
            return field
        }
        set(value) {
            field = value
            prefs.edit().putBoolean(FILTER_WITH_PHOTOS_ONLY, value).apply()
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

    var maxDistToSubway: Double? = null
        get() {
            field = getNullableDouble(FILTER_MAX_DISTANCE_TO_SUBWAY)
            return field
        }
        set(value) {
            field = value
            putNullableValue(FILTER_MAX_DISTANCE_TO_SUBWAY, value)
        }

    var keywords: MutableSet<String> = HashSet()
        get() {
            val stringSet = prefs.getStringSet(FILTER_KEYWORDS, HashSet<String>())
            field = HashSet()
            for (string in stringSet) {
                field.add(string)
            }
            return field
        }
        set(value) {
            field = value
            val set = HashSet(value)
            prefs.edit().putStringSet(FILTER_KEYWORDS, set).apply()
        }

    var roomNumbers: MutableSet<String> = HashSet()
        get() {
            val stringSet = prefs.getStringSet(FILTER_ROOM_NUMBER, HashSet<String>())
            field = HashSet()
            for (string in stringSet) {
                field.add(string)
            }
            return field
        }
        set(value) {
            field = value
            val set = HashSet(value)
            prefs.edit().putStringSet(FILTER_ROOM_NUMBER, set).apply()
        }

    var updateDaysAgo: Int = FlatsApplication.DEFAULT_NUMBER_OF_DAYS_AD_IS_ACTUAL
        get() {
            field = prefs.getInt(SETTINGS_DAYS_AD_IS_ACTUAL,
                    FlatsApplication.DEFAULT_NUMBER_OF_DAYS_AD_IS_ACTUAL)
            return field
        }
        set(value) {
            field = value
            prefs.edit().putInt(SETTINGS_DAYS_AD_IS_ACTUAL, value).apply()
        }

    var flatFilter: FlatFilter? = null
        get() {
            field = FlatFilter(minCost = minCost,
                    maxCost = maxCost,
                    subwaysIds = subwayIds,
                    agencyAllowed = allowAgency,
                    allowWithPhotosOnly = allowPhotosOnly,
                    rentTypes = rentTypes,
                    maxDistToSubway = maxDistToSubway,
                    keywords = keywords,
                    roomNumbers = roomNumbers,
                    updateDatesAgo = updateDaysAgo)
            return field

        }
        set(value) {
            if (value != null) {
                field = value
                minCost = value.minCost
                maxCost = value.maxCost
                subwayIds = value.subwaysIds
                allowAgency = value.agencyAllowed
                rentTypes = value.rentTypes
                maxDistToSubway = value.maxDistToSubway
                allowPhotosOnly = value.allowWithPhotosOnly
                keywords = value.keywords
                roomNumbers = value.roomNumbers
                updateDaysAgo = value.updateDatesAgo
            }
        }

    var enableNotifications: Boolean = false
        get() {
            field = prefs.getBoolean(SETTINGS_ENABLE_NOTIFICATONS, false)
            return field
        }
        set(value) {
            prefs.edit().putBoolean(SETTINGS_ENABLE_NOTIFICATONS, value).apply()
        }

    var showSeenFlats: Boolean = false
        get() {
            field = prefs.getBoolean(SETTINGS_SHOW_SEEN_FLATS, false)
            return field
        }
        set(value) {
            prefs.edit().putBoolean(SETTINGS_SHOW_SEEN_FLATS, value).apply()
        }

    private fun getNullableInt(fieldName: String): Int? {
        val value = prefs.getInt(fieldName, 0)
        return if (value > 0) value else null
    }

    private fun getNullableDouble(fieldName: String): Double? {
        val value = prefs.getFloat(fieldName, 0F)
        return if (value > 0F) value.toDouble() else null
    }

    private fun putNullableValue(fieldName: String, value: Int?) {
        if (value != null) {
            prefs.edit().putInt(fieldName, value).apply()
        } else {
            prefs.edit().remove(fieldName).apply()
        }
    }

    private fun putNullableValue(fieldName: String, value: Double?) {
        if (value != null) {
            prefs.edit().putFloat(fieldName, value.toFloat()).apply()
        } else {
            prefs.edit().remove(fieldName).apply()
        }
    }
}
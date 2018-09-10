package kazarovets.flatspinger.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kazarovets.flatspinger.FlatsApplication
import kazarovets.flatspinger.base.prefs.observers.SharedPreferenceMultiKeyObservable
import kazarovets.flatspinger.base.prefs.observers.SharedPreferenceObservableBoolean
import kazarovets.flatspinger.model.FlatFilter
import kazarovets.flatspinger.model.RentType


@SuppressLint("StaticFieldLeak", "CommitPrefEdits")
object PreferenceUtils {

    /**
     * Don't use apply() because it runs listeners on UI thread and blocks it.
     * Use schedule() instead.
     */

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

    val flatsKeys = listOf(FILTER_MIN_COST_USD, FILTER_MAX_COST_USD,
            FILTER_AGENCY_ALLOWED, FILTER_WITH_PHOTOS_ONLY, FILTER_SUBWAYS_IDS,
            FILTER_RENT_TYPES, FILTER_MAX_DISTANCE_TO_SUBWAY, FILTER_KEYWORDS)

    val SETTINGS_DAYS_AD_IS_ACTUAL = "days_ad_is_actual"
    val SETTINGS_SHOW_SEEN_FLATS = "show_seen_flats"
    val SETTINGS_ENABLE_NOTIFICATONS = "enable_notifications"

    val TAG = "PreferenceUtils"

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
            prefs.edit().putBoolean(FILTER_AGENCY_ALLOWED, value).schedule()
        }

    var allowPhotosOnly: Boolean = false
        get() {
            field = prefs.getBoolean(FILTER_WITH_PHOTOS_ONLY, false)
            return field
        }
        set(value) {
            field = value
            prefs.edit().putBoolean(FILTER_WITH_PHOTOS_ONLY, value).schedule()
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
            prefs.edit().putStringSet(FILTER_SUBWAYS_IDS, set).schedule()
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
            prefs.edit().putStringSet(FILTER_RENT_TYPES, set).schedule()
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
            prefs.edit().putStringSet(FILTER_KEYWORDS, set).schedule()
        }

    var updateDaysAgo: Int? = null
        get() {
            field = prefs.getInt(SETTINGS_DAYS_AD_IS_ACTUAL,
                    FlatsApplication.DEFAULT_NUMBER_OF_DAYS_AD_IS_ACTUAL)
            return field
        }
        set(value) {
            field = value
            putNullableValue(SETTINGS_DAYS_AD_IS_ACTUAL, value)
        }

    var flatFilter: FlatFilter = FlatFilter()
        get() {
            field = FlatFilter(minCost = minCost,
                    maxCost = maxCost,
                    subwaysIds = subwayIds,
                    agencyAllowed = allowAgency,
                    allowWithPhotosOnly = allowPhotosOnly,
                    closeToSubway = maxDistToSubway != null,
                    rentTypes = rentTypes,
                    maxDistToSubway = maxDistToSubway,
                    keywords = keywords,
                    updateDatesAgo = updateDaysAgo)
            return field

        }
        set(value) {
            field = value
            minCost = value.minCost
            maxCost = value.maxCost
            subwayIds = HashSet(value.subwaysIds)
            allowAgency = value.agencyAllowed
            rentTypes = HashSet(value.rentTypes)
            maxDistToSubway = value.maxDistToSubway
            allowPhotosOnly = value.allowWithPhotosOnly
            keywords = HashSet(value.keywords)
            updateDaysAgo = value.updateDatesAgo
        }

    val flatsFilterObservable: Observable<FlatFilter> by lazy {
        object : SharedPreferenceMultiKeyObservable<FlatFilter>(prefs,
                flatsKeys,
                FlatFilter()
        ) {
            override fun getValueFromPreferences(defValue: FlatFilter): FlatFilter {
                return PreferenceUtils.flatFilter
            }
        }
    }

    var enableNotifications: Boolean = false
        get() {
            field = prefs.getBoolean(SETTINGS_ENABLE_NOTIFICATONS, false)
            return field
        }
        set(value) {
            prefs.edit().putBoolean(SETTINGS_ENABLE_NOTIFICATONS, value).schedule()
        }

    var showSeenFlats: Boolean = false
        get() {
            field = prefs.getBoolean(SETTINGS_SHOW_SEEN_FLATS, false)
            return field
        }
        set(value) {
            prefs.edit().putBoolean(SETTINGS_SHOW_SEEN_FLATS, value).schedule()
        }

    val showSeenFlatObservable: Observable<Boolean> by lazy {
        SharedPreferenceObservableBoolean(prefs, SETTINGS_SHOW_SEEN_FLATS, false)
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
            prefs.edit().putInt(fieldName, value).schedule()
        } else {
            prefs.edit().remove(fieldName).schedule()
        }
    }

    private fun putNullableValue(fieldName: String, value: Double?) {
        if (value != null) {
            prefs.edit().putFloat(fieldName, value.toFloat()).schedule()
        } else {
            prefs.edit().remove(fieldName).schedule()
        }
    }


    fun SharedPreferences.Editor.schedule() {
        Schedulers.io().scheduleDirect {
            this.apply()
        }
    }
}

package kazarovets.flatspinger.base.prefs.observers

import android.content.SharedPreferences
import kazarovets.flatspinger.base.prefs.observers.SharedPreferenceObservable


class SharedPreferenceObservableBoolean(sharedPrefs: SharedPreferences, key: String, defValue: Boolean) :
        SharedPreferenceObservable<Boolean>(sharedPrefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: Boolean): Boolean = sharedPrefs.getBoolean(key, defValue)
}
package kazarovets.flatspinger.base.prefs.observers

import android.content.SharedPreferences
import kazarovets.flatspinger.base.prefs.observers.SharedPreferenceObservable

class SharedPreferenceObservableInt(sharedPrefs: SharedPreferences, key: String, defValue: Int) :
        SharedPreferenceObservable<Int>(sharedPrefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: Int): Int = sharedPrefs.getInt(key, defValue)
}
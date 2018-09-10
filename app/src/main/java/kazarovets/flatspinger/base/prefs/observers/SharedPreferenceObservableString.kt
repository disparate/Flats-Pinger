package kazarovets.flatspinger.base.prefs.observers

import android.content.SharedPreferences
import kazarovets.flatspinger.base.prefs.observers.SharedPreferenceObservable


class SharedPreferenceObservableString(sharedPrefs: SharedPreferences, key: String, defValue: String) :
        SharedPreferenceObservable<String>(sharedPrefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: String): String = sharedPrefs.getString(key, defValue)
}

package kazarovets.flatspinger.base.prefs.observers

import android.content.SharedPreferences
import kazarovets.flatspinger.base.prefs.observers.SharedPreferenceObservable


class SharedPreferenceObservableStringSet(sharedPrefs: SharedPreferences, key: String) :
        SharedPreferenceObservable<Set<String>>(sharedPrefs, key, emptySet()) {
    override fun getValueFromPreferences(key: String, defValue: Set<String>): Set<String> {
        return sharedPrefs.getStringSet(key, defValue)
    }
}
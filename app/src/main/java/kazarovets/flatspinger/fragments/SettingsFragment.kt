package kazarovets.flatspinger.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import kazarovets.flatspinger.FlatsApplication
import kazarovets.flatspinger.R
import kazarovets.flatspinger.utils.PreferenceUtils
import kazarovets.flatspinger.utils.ScheduleUtils
import kazarovets.flatspinger.widgets.OnNumberChangedTextWatcher


class SettingsFragment : Fragment() {


    private var enableNotificationsCheckbox: CheckBox? = null
    private var showSeenFlatsCheckbox: CheckBox? = null
    private var daysAdIsActualView: TextView? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        enableNotificationsCheckbox = view.findViewById(R.id.enable_notifications_checkbox)
        enableNotificationsCheckbox?.isChecked = PreferenceUtils.enableNotifications
        enableNotificationsCheckbox?.setOnCheckedChangeListener { compoundButton, checked ->
            PreferenceUtils.enableNotifications = checked
            if (checked) {
                ScheduleUtils.scheduleFlatsNotificationsJob(context!!)
            } else {
                ScheduleUtils.cancelScheduledJob(context!!)
            }
        }

        showSeenFlatsCheckbox = view.findViewById(R.id.show_seen_flats_checkbox)
        showSeenFlatsCheckbox?.isChecked = PreferenceUtils.showSeenFlats
        showSeenFlatsCheckbox?.setOnCheckedChangeListener { compoundButton, checked ->
            PreferenceUtils.showSeenFlats = checked
        }

        daysAdIsActualView = view.findViewById(R.id.edit_text_days_ad_actual)
        daysAdIsActualView?.text = PreferenceUtils.updateDaysAgo?.toString() ?: ""
        daysAdIsActualView?.addTextChangedListener(object : OnNumberChangedTextWatcher {
            override fun parseText(text: String) {
                if (text.isNotEmpty()) {
                    val value = text.toInt()
                    PreferenceUtils.updateDaysAgo = value
                } else {
                    PreferenceUtils.updateDaysAgo = FlatsApplication.DEFAULT_NUMBER_OF_DAYS_AD_IS_ACTUAL
                }
            }
        })


    }
}
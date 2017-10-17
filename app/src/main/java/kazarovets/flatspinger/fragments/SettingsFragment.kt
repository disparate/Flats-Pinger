package kazarovets.flatspinger.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import kazarovets.flatspinger.R
import kazarovets.flatspinger.utils.PreferenceUtils
import kazarovets.flatspinger.utils.ScheduleUtils


class SettingsFragment : Fragment() {


    private var enableNotificationsCheckbox: CheckBox? = null


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        enableNotificationsCheckbox = view?.findViewById(R.id.enable_notifications_checkbox)
        enableNotificationsCheckbox?.isChecked = PreferenceUtils.enableNotifications
        enableNotificationsCheckbox?.setOnCheckedChangeListener { compoundButton, checked ->
            PreferenceUtils.enableNotifications = checked
            if (checked) {
                ScheduleUtils.scheduleFlatsNotificationsJob(context)
            } else {
                ScheduleUtils.cancelScheduledJob(context)
            }
        }


    }
}
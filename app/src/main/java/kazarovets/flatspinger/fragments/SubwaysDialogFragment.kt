package kazarovets.flatspinger.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import kazarovets.flatspinger.R
import kazarovets.flatspinger.model.Subway
import kazarovets.flatspinger.utils.PreferenceUtils
import kazarovets.flatspinger.utils.SubwayUtils
import kazarovets.flatspinger.views.SubwaysSelectorView
import kazarovets.flatspinger.widgets.OnNumberChangedTextWatcher
import kotlinx.android.synthetic.main.dialog_subways.*

class SubwaysDialogFragment : DialogFragment() {


    private var selectedSubwaysIds: MutableSet<Int> = HashSet()


    private val onSubwaySelectedListener = object : SubwaysSelectorView.OnSubwayCheckedListener {
        override fun onPositionChecked(subway: Subway, checked: Boolean) {
            if (checked) selectedSubwaysIds.add(subway.id) else selectedSubwaysIds.remove(subway.id)
            PreferenceUtils.subwayIds = selectedSubwaysIds
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_subways, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme)

        dialogSubwaysClose.setOnClickListener { dismiss() }

        selectedSubwaysIds = PreferenceUtils.subwayIds

        dialogSubwaysRedSubwaySelector.subways = SubwayUtils.RED_LINE_SUBWAYS
        dialogSubwaysRedSubwaySelector.updateCheckedSubways(selectedSubwaysIds)
        dialogSubwaysRedSubwaySelector.onCheckedChangedListener = onSubwaySelectedListener

        dialogSubwaysBlueSubwaySelector.subways = SubwayUtils.BLUE_LINE_SUBWAYS
        dialogSubwaysBlueSubwaySelector.updateCheckedSubways(selectedSubwaysIds)
        dialogSubwaysBlueSubwaySelector.onCheckedChangedListener = onSubwaySelectedListener

        dialogSubwaysDistance.setText(PreferenceUtils.maxDistToSubway?.toString() ?: "")
        dialogSubwaysDistance.addTextChangedListener(object : OnNumberChangedTextWatcher {
            override fun parseText(text: String) {
                val value = if (text.isNotEmpty()) text.toDouble() else null
                PreferenceUtils.maxDistToSubway = value
            }
        })

    }
}

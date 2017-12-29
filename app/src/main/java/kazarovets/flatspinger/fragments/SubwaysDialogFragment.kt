package kazarovets.flatspinger.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import kazarovets.flatspinger.R
import kazarovets.flatspinger.model.Subway
import kazarovets.flatspinger.utils.PreferenceUtils
import kazarovets.flatspinger.utils.SubwayUtils
import kazarovets.flatspinger.views.SubwaysSelectorView
import kazarovets.flatspinger.widgets.OnNumberChangedTextWatcher

class SubwaysDialogFragment : DialogFragment() {

    private var exitButton: View? = null

    private var selectedSubwaysIds: MutableSet<Int> = HashSet()

    private var maxDistanceToSubway: EditText? = null
    private var redSubwaysSelector: SubwaysSelectorView? = null
    private var blueSubwaysSelector: SubwaysSelectorView? = null


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

        exitButton = view.findViewById(R.id.close_button)
        exitButton?.setOnClickListener { dismiss() }

        selectedSubwaysIds = PreferenceUtils.subwayIds

        redSubwaysSelector = view.findViewById(R.id.red_line_subway_selector)
        blueSubwaysSelector = view.findViewById(R.id.blue_line_subway_selector)

        redSubwaysSelector?.subways = SubwayUtils.RED_LINE_SUBWAYS
        redSubwaysSelector?.updateCheckedSubways(selectedSubwaysIds)
        redSubwaysSelector?.onCheckedChangedListener = onSubwaySelectedListener

        blueSubwaysSelector?.subways = SubwayUtils.BLUE_LINE_SUBWAYS
        blueSubwaysSelector?.updateCheckedSubways(selectedSubwaysIds)
        blueSubwaysSelector?.onCheckedChangedListener = onSubwaySelectedListener

        maxDistanceToSubway = view.findViewById(R.id.edit_text_distance_to_subway)
        maxDistanceToSubway?.setText(PreferenceUtils.maxDistToSubway?.toString() ?: "")
        maxDistanceToSubway?.addTextChangedListener(object : OnNumberChangedTextWatcher {
            override fun parseText(text: String) {
                val value = if (text.isNotEmpty()) text.toDouble() else null
                PreferenceUtils.maxDistToSubway = value
            }
        })

    }
}

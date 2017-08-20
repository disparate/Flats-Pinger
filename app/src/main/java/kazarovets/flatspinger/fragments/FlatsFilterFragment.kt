package kazarovets.flatspinger.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ScrollView
import android.widget.TextView
import kazarovets.flatspinger.R
import kazarovets.flatspinger.model.Subway
import kazarovets.flatspinger.utils.PreferenceUtils
import kazarovets.flatspinger.utils.SubwayUtils
import kazarovets.flatspinger.views.SubwaysSelectorView


class FlatsFilterFragment : Fragment() {

    private var redSubwaysSelector: SubwaysSelectorView? = null
    private var blueSubwaysSelector: SubwaysSelectorView? = null
    private var allowAgencyCheckbox: CheckBox? = null
    private var minCostUsd: TextView? = null
    private var maxCostUsd: TextView? = null
    private var scrollView: ScrollView? = null

    private var selectedSubwaysIds: MutableSet<Int> = HashSet()

    private val onSubwaySelectedListener = object : SubwaysSelectorView.OnSubwayCheckedListener {
        override fun onPositionChecked(subway: Subway, checked: Boolean) {
            if (checked) selectedSubwaysIds.add(subway.id) else selectedSubwaysIds.remove(subway.id)
            PreferenceUtils.subwayIds = selectedSubwaysIds
        }

    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_filter, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scrollView = view?.findViewById(R.id.scroll_view)
        scrollView?.setOnTouchListener { v, motionEvent ->
            v.requestFocusFromTouch()
            return@setOnTouchListener false
        }

        redSubwaysSelector = view?.findViewById(R.id.red_line_subway_selector)
        blueSubwaysSelector = view?.findViewById(R.id.blue_line_subway_selector)

        redSubwaysSelector?.subways = SubwayUtils.RED_LINE_SUBWAYS
        redSubwaysSelector?.updateCheckedSubways(PreferenceUtils.subwayIds)
        redSubwaysSelector?.onCheckedChangedListener = onSubwaySelectedListener

        blueSubwaysSelector?.subways = SubwayUtils.BLUE_LINE_SUBWAYS
        blueSubwaysSelector?.updateCheckedSubways(PreferenceUtils.subwayIds)
        blueSubwaysSelector?.onCheckedChangedListener = onSubwaySelectedListener

        allowAgencyCheckbox = view?.findViewById(R.id.allow_agency_checkbox)
        allowAgencyCheckbox?.isChecked = PreferenceUtils.allowAgency
        allowAgencyCheckbox?.setOnCheckedChangeListener { compoundButton, b -> PreferenceUtils.allowAgency = b }


        minCostUsd = view?.findViewById(R.id.edit_text_min_cost)
        if (PreferenceUtils.minCost > 0) {
            minCostUsd?.text = PreferenceUtils.minCost.toString()
        }
        minCostUsd?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val value = if (p0.toString().isNotEmpty()) p0.toString().toInt() else 0
                PreferenceUtils.minCost = value
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })

        maxCostUsd = view?.findViewById(R.id.edit_text_max_cost)
        if (PreferenceUtils.maxCost > 0) {
            maxCostUsd?.text = PreferenceUtils.maxCost.toString()
        }
        maxCostUsd?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val value = if (p0.toString().isNotEmpty()) p0.toString().toInt() else 0
                PreferenceUtils.maxCost = value

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })
    }

}
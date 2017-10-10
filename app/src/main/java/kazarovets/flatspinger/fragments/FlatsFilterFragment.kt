package kazarovets.flatspinger.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kazarovets.flatspinger.R
import kazarovets.flatspinger.model.RentType
import kazarovets.flatspinger.model.Subway
import kazarovets.flatspinger.utils.PreferenceUtils
import kazarovets.flatspinger.utils.ScheduleUtils
import kazarovets.flatspinger.utils.SubwayUtils
import kazarovets.flatspinger.views.SubwaysSelectorView


class FlatsFilterFragment : Fragment() {

    companion object {
        val TAG = "FlatsFilterFragment"

        val KEYWORDS_FRAGMENT_TAG = "keywords_fragment"

        interface OnNumberTextChangedTextWatcher : TextWatcher {

            fun parseText(text: String)

            override fun afterTextChanged(p0: Editable?) {
                try {
                    parseText(p0.toString())
                } catch (ex: NumberFormatException) {
                    Log.d(TAG, "Exception parsing text in number", ex)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        }

        class OnRentTypeCheckedListener(val rentType: RentType) : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(button: CompoundButton?, checked: Boolean) {
                val set = HashSet(PreferenceUtils.roomNumbers)
                if (checked) {
                    set.add(rentType.name)
                } else {
                    set.remove(rentType.name)
                }
                PreferenceUtils.roomNumbers = set
            }
        }
    }

    private var redSubwaysSelector: SubwaysSelectorView? = null
    private var blueSubwaysSelector: SubwaysSelectorView? = null

    private var enableNotificationsCheckbox: CheckBox? = null
    private var allowAgencyCheckbox: CheckBox? = null
    private var allowOnlyWithPhotosCheckbox: CheckBox? = null

    private var oneRoomCheckbox: CheckBox? = null
    private var twoRoomsCheckbox: CheckBox? = null
    private var threeRoomsCheckbox: CheckBox? = null
    private var fourRoomsCheckbox: CheckBox? = null

    private var minCostUsd: TextView? = null
    private var maxCostUsd: TextView? = null
    private var maxDistanceToSubway: EditText? = null
    private var scrollView: ScrollView? = null
    private var editKeywordsButton: View? = null

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

        selectedSubwaysIds = PreferenceUtils.subwayIds

        redSubwaysSelector = view?.findViewById(R.id.red_line_subway_selector)
        blueSubwaysSelector = view?.findViewById(R.id.blue_line_subway_selector)

        redSubwaysSelector?.subways = SubwayUtils.RED_LINE_SUBWAYS
        redSubwaysSelector?.updateCheckedSubways(selectedSubwaysIds)
        redSubwaysSelector?.onCheckedChangedListener = onSubwaySelectedListener

        blueSubwaysSelector?.subways = SubwayUtils.BLUE_LINE_SUBWAYS
        blueSubwaysSelector?.updateCheckedSubways(selectedSubwaysIds)
        blueSubwaysSelector?.onCheckedChangedListener = onSubwaySelectedListener

        allowAgencyCheckbox = view?.findViewById(R.id.allow_agency_checkbox)
        allowAgencyCheckbox?.isChecked = PreferenceUtils.allowAgency
        allowAgencyCheckbox?.setOnCheckedChangeListener { compoundButton, b -> PreferenceUtils.allowAgency = b }

        allowOnlyWithPhotosCheckbox = view?.findViewById(R.id.allow_only_with_photos_checkbox)
        allowOnlyWithPhotosCheckbox?.isChecked = PreferenceUtils.allowPhotosOnly
        allowOnlyWithPhotosCheckbox?.setOnCheckedChangeListener { compoundButton, b -> PreferenceUtils.allowPhotosOnly = b }

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

        minCostUsd = view?.findViewById(R.id.edit_text_min_cost)
        minCostUsd?.text = PreferenceUtils.minCost?.toString()
        minCostUsd?.addTextChangedListener(object : OnNumberTextChangedTextWatcher {
            override fun parseText(text: String) {
                val value = if (text.isNotEmpty()) text.toInt() else null
                PreferenceUtils.minCost = value
            }
        })

        maxCostUsd = view?.findViewById(R.id.edit_text_max_cost)
        maxCostUsd?.text = PreferenceUtils.maxCost?.toString()
        maxCostUsd?.addTextChangedListener(object : OnNumberTextChangedTextWatcher {
            override fun parseText(text: String) {
                val value = if (text.isNotEmpty()) text.toInt() else null
                PreferenceUtils.maxCost = value
            }
        })

        maxDistanceToSubway = view?.findViewById(R.id.edit_text_distance_to_subway)
        maxDistanceToSubway?.setText(PreferenceUtils.maxDistToSubway?.toString() ?: "")
        maxDistanceToSubway?.addTextChangedListener(object : OnNumberTextChangedTextWatcher {
            override fun parseText(text: String) {
                val value = if (text.isNotEmpty()) text.toDouble() else null
                PreferenceUtils.maxDistToSubway = value
            }
        })

        editKeywordsButton = view?.findViewById(R.id.edit_keywords_button)
        editKeywordsButton?.setOnClickListener {
            KeywordsDialogFragment().show(childFragmentManager, KEYWORDS_FRAGMENT_TAG)
        }

        oneRoomCheckbox = view?.findViewById(R.id.rent_1k)
        oneRoomCheckbox?.isChecked = PreferenceUtils.roomNumbers.contains(RentType.FLAT_1_ROOM.name)
        oneRoomCheckbox?.setOnCheckedChangeListener(OnRentTypeCheckedListener(RentType.FLAT_1_ROOM))

        twoRoomsCheckbox = view?.findViewById(R.id.rent_2k)
        twoRoomsCheckbox?.isChecked = PreferenceUtils.roomNumbers.contains(RentType.FLAT_2_ROOM.name)
        twoRoomsCheckbox?.setOnCheckedChangeListener(OnRentTypeCheckedListener(RentType.FLAT_2_ROOM))

        threeRoomsCheckbox = view?.findViewById(R.id.rent_3k)
        threeRoomsCheckbox?.isChecked = PreferenceUtils.roomNumbers.contains(RentType.FLAT_3_ROOM.name)
        threeRoomsCheckbox?.setOnCheckedChangeListener(OnRentTypeCheckedListener(RentType.FLAT_3_ROOM))

        fourRoomsCheckbox = view?.findViewById(R.id.rent_4k_and_more)
        fourRoomsCheckbox?.isChecked = PreferenceUtils.roomNumbers.contains(RentType.FLAT_4_ROOM_OR_MORE.name)
        fourRoomsCheckbox?.setOnCheckedChangeListener(OnRentTypeCheckedListener(RentType.FLAT_4_ROOM_OR_MORE))
    }


}
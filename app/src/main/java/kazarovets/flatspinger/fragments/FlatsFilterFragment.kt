package kazarovets.flatspinger.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ScrollView
import android.widget.TextView
import kazarovets.flatspinger.R
import kazarovets.flatspinger.model.RentType
import kazarovets.flatspinger.utils.PreferenceUtils
import kazarovets.flatspinger.widgets.OnNumberChangedTextWatcher


class FlatsFilterFragment : Fragment() {

    companion object {
        val KEYWORDS_FRAGMENT_TAG = "keywords_fragment"
        val SUBWAYS_FRAGMENT_TAG = "keywords_fragment"

        class OnRentTypeCheckedListener(private val rentType: RentType) : CompoundButton.OnCheckedChangeListener {
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

    private var allowAgencyCheckbox: CheckBox? = null
    private var allowOnlyWithPhotosCheckbox: CheckBox? = null

    private var oneRoomCheckbox: CheckBox? = null
    private var twoRoomsCheckbox: CheckBox? = null
    private var threeRoomsCheckbox: CheckBox? = null
    private var fourRoomsCheckbox: CheckBox? = null

    private var minCostUsd: TextView? = null
    private var maxCostUsd: TextView? = null
    private var scrollView: ScrollView? = null
    private var editKeywordsButton: View? = null
    private var editSubwaysButton: View? = null


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.fragment_filter, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scrollView = view?.findViewById(R.id.scroll_view)
        scrollView?.setOnTouchListener { v, _ ->
            v.requestFocusFromTouch()
            return@setOnTouchListener false
        }

        allowAgencyCheckbox = view?.findViewById(R.id.allow_agency_checkbox)
        allowAgencyCheckbox?.isChecked = PreferenceUtils.allowAgency
        allowAgencyCheckbox?.setOnCheckedChangeListener { _, b -> PreferenceUtils.allowAgency = b }

        allowOnlyWithPhotosCheckbox = view?.findViewById(R.id.allow_only_with_photos_checkbox)
        allowOnlyWithPhotosCheckbox?.isChecked = PreferenceUtils.allowPhotosOnly
        allowOnlyWithPhotosCheckbox?.setOnCheckedChangeListener { _, b -> PreferenceUtils.allowPhotosOnly = b }

        minCostUsd = view?.findViewById(R.id.edit_text_min_cost)
        minCostUsd?.text = PreferenceUtils.minCost?.toString()
        minCostUsd?.addTextChangedListener(object : OnNumberChangedTextWatcher {
            override fun parseText(text: String) {
                val value = if (text.isNotEmpty()) text.toInt() else null
                PreferenceUtils.minCost = value
            }
        })

        maxCostUsd = view?.findViewById(R.id.edit_text_max_cost)
        maxCostUsd?.text = PreferenceUtils.maxCost?.toString()
        maxCostUsd?.addTextChangedListener(object : OnNumberChangedTextWatcher {
            override fun parseText(text: String) {
                val value = if (text.isNotEmpty()) text.toInt() else null
                PreferenceUtils.maxCost = value
            }
        })


        editKeywordsButton = view?.findViewById(R.id.edit_keywords_button)
        editKeywordsButton?.setOnClickListener {
            KeywordsDialogFragment().show(childFragmentManager, KEYWORDS_FRAGMENT_TAG)
        }

        editSubwaysButton = view?.findViewById(R.id.edit_subways_button)
        editSubwaysButton?.setOnClickListener {
            SubwaysDialogFragment().show(childFragmentManager, SUBWAYS_FRAGMENT_TAG)
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
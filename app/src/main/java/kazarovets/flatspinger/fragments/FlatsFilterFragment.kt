package kazarovets.flatspinger.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import kazarovets.flatspinger.R
import kazarovets.flatspinger.model.RentType
import kazarovets.flatspinger.utils.PreferenceUtils
import kazarovets.flatspinger.widgets.OnNumberChangedTextWatcher
import kotlinx.android.synthetic.main.fragment_filter.*


class FlatsFilterFragment : Fragment() {

    companion object {
        val KEYWORDS_FRAGMENT_TAG = "keywords_fragment"
        val SUBWAYS_FRAGMENT_TAG = "keywords_fragment"

        class OnRentTypeCheckedListener(private val rentType: RentType) : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(button: CompoundButton?, checked: Boolean) {
                val set = HashSet(PreferenceUtils.rentTypes)
                if (checked) {
                    set.add(rentType)
                } else {
                    set.remove(rentType)
                }
                PreferenceUtils.rentTypes = set
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_filter, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        filterScrollView.setOnTouchListener { v, _ ->
            v.requestFocusFromTouch()
            return@setOnTouchListener false
        }

        filterAllowAgency.isChecked = PreferenceUtils.allowAgency
        filterAllowAgency.setOnCheckedChangeListener { _, b -> PreferenceUtils.allowAgency = b }

        filterAllowOnlyWithPhotos.isChecked = PreferenceUtils.allowPhotosOnly
        filterAllowOnlyWithPhotos.setOnCheckedChangeListener { _, b -> PreferenceUtils.allowPhotosOnly = b }

        filterMinCostEditText.setText(PreferenceUtils.minCost?.toString())
        filterMinCostEditText.addTextChangedListener(object : OnNumberChangedTextWatcher {
            override fun parseText(text: String) {
                val value = if (text.isNotEmpty()) text.toInt() else null
                PreferenceUtils.minCost = value
            }
        })

        filterMaxCostEditText.setText(PreferenceUtils.maxCost?.toString())
        filterMaxCostEditText.addTextChangedListener(object : OnNumberChangedTextWatcher {
            override fun parseText(text: String) {
                val value = if (text.isNotEmpty()) text.toInt() else null
                PreferenceUtils.maxCost = value
            }
        })


        filterEditKeywordsButton.setOnClickListener {
            KeywordsDialogFragment().show(childFragmentManager, KEYWORDS_FRAGMENT_TAG)
        }

        filterEditSubwaysButton.setOnClickListener {
            SubwaysDialogFragment().show(childFragmentManager, SUBWAYS_FRAGMENT_TAG)
        }

        filterRent1k.isChecked = PreferenceUtils.rentTypes.contains(RentType.FLAT_1_ROOM)
        filterRent1k.setOnCheckedChangeListener(OnRentTypeCheckedListener(RentType.FLAT_1_ROOM))

        filterRent2k.isChecked = PreferenceUtils.rentTypes.contains(RentType.FLAT_2_ROOM)
        filterRent2k.setOnCheckedChangeListener(OnRentTypeCheckedListener(RentType.FLAT_2_ROOM))

        filterRent3k.isChecked = PreferenceUtils.rentTypes.contains(RentType.FLAT_3_ROOM)
        filterRent3k.setOnCheckedChangeListener(OnRentTypeCheckedListener(RentType.FLAT_3_ROOM))

        filterRent4kAndMore.isChecked = PreferenceUtils.rentTypes.contains(RentType.FLAT_4_ROOM_OR_MORE)
        filterRent4kAndMore.setOnCheckedChangeListener(OnRentTypeCheckedListener(RentType.FLAT_4_ROOM_OR_MORE))
    }
}
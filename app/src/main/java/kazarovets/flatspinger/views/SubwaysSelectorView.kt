package kazarovets.flatspinger.views

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.LinearLayout
import kazarovets.flatspinger.R
import kazarovets.flatspinger.model.Subway
import kotlinx.android.synthetic.main.view_subways_selector.view.*


class SubwaysSelectorView : FrameLayout {

    companion object {
        val BUNDLE_SUPER_STATE = "super_state"
        val BUNDLE_CHECKED_IDS = "checked_ids"
    }

    var subways: Array<Subway> = emptyArray()
        set(value) {
            field = value
            addChildViews()
        }

    val color: Int
    val colorNeutral: Int
    val checkboxDrawable: Drawable

    //    private var viewsBetweenCheckboxes: MutableList<View> = ArrayList()
    private var checkboxesViews: MutableMap<Int, CheckBox> = HashMap()

    var onCheckedChangedListener: OnSubwayCheckedListener? = null

    constructor(context: Context) :
            this(context, null)

    constructor(context: Context, attrs: AttributeSet?) :
            this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        inflate(context, R.layout.view_subways_selector, this)

        colorNeutral = ContextCompat.getColor(context, R.color.colorSubwayNeutral)

        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SubwaysSelectorView)
            color = typedArray.getColor(R.styleable.SubwaysSelectorView_selectedColor, Color.RED)
            checkboxDrawable = typedArray.getDrawable(R.styleable.SubwaysSelectorView_checkboxSelector)
            typedArray.recycle()
        } else {
            color = Color.RED
            checkboxDrawable = ContextCompat.getDrawable(context, R.drawable.selector_check_subway_red)!!
        }
        subwaysSelectorVerticalLine.setBackgroundColor(color)
    }

    private fun addChildViews() {
        checkboxesViews.clear()

        subwaysSelectorMainContainer.removeAllViews()

        for (i in subways.indices) {
            addItemView(subways.get(i))

            if (i < subways.size - 1) {
                addLineView()
            }
        }
        invalidate()
    }

    fun updateCheckedSubways(checkedIds: MutableSet<Int>) {
        for (i in subways.indices) {
            val subway = subways.get(i)
            val checkbox = checkboxesViews.get(subway.id)
            checkbox?.isChecked = checkedIds.contains(subway.id)
        }
    }

    private fun colorizeItem(id: Int, isChecked: Boolean) {
        val checkbox = checkboxesViews.get(id)
        checkbox?.setTextColor(if (isChecked) color else colorNeutral)
    }

    private fun addItemView(subway: Subway) {
        val checkBox = View.inflate(context, R.layout.item_subway, null) as CheckBox
        checkBox.buttonDrawable = checkboxDrawable.getConstantState().newDrawable()
        checkBox.setOnCheckedChangeListener { compoundButton, b
            ->
            colorizeItem(subway.id, b)
            onCheckedChangedListener?.onPositionChecked(subway, b)
        }
        checkBox.setText(subway.name)
        checkBox.isSaveEnabled = false

        checkboxesViews.put(subway.id, checkBox)
        subwaysSelectorMainContainer.addView(checkBox)
    }

    private fun addLineView() {
        val view = View(context)
        val lp = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0)
        lp.weight = 1.0F
        view.layoutParams = lp
    }

    interface OnSubwayCheckedListener {
        fun onPositionChecked(subway: Subway, checked: Boolean)
    }
}
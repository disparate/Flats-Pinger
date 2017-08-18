package kazarovets.flatspinger.views

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RelativeLayout
import android.widget.TextView
import kazarovets.flatspinger.R
import kazarovets.flatspinger.model.Subway


class SubwaysSelectorView : RelativeLayout {

    var subways: Array<Subway> = emptyArray()
        set(value) {
            field = value
            addChildViews()
        }

    val color: Int
    val colorNeutral: Int
    val checkboxDrawable: Drawable

    var viewsBetweenCheckboxes: MutableList<View> = ArrayList()
    var checkboxesViews: MutableList<CheckBox> = ArrayList()
    var names: MutableList<TextView> = ArrayList()

    val mainContainer: ViewGroup
    val linesContainer: ViewGroup

    private var onCheckedChangedListener = object : OnPositionCheckedListener {
        override fun onPositionChecked(pos: Int, checked: Boolean) {
            colorizeItem(pos, checked)
        }
    }


    constructor(context: Context) :
            this(context, null)

    constructor(context: Context, attrs: AttributeSet?) :
            this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        inflate(context, R.layout.view_subways_selector, this)

        mainContainer = findViewById(R.id.main_container)
        linesContainer = findViewById(R.id.lines_container)

        colorNeutral = ContextCompat.getColor(context, R.color.colorSubwayNeutral)

        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SubwaysSelectorView)
            color = typedArray.getColor(R.styleable.SubwaysSelectorView_selectedColor, Color.RED)
            checkboxDrawable = ContextCompat.getDrawable(context,
                    typedArray.getInt(R.styleable.SubwaysSelectorView_checkboxSelector,
                            R.drawable.selector_check_subway_red))
            typedArray.recycle()
        } else {
            color = Color.RED
            checkboxDrawable = ContextCompat.getDrawable(context, R.drawable.selector_check_subway_red)
        }
    }

    private fun addChildViews() {
        checkboxesViews.clear()
        names.clear()
        viewsBetweenCheckboxes.clear()

        linesContainer.removeAllViews()
        mainContainer.removeAllViews()

        for (i in subways.indices) {
            addItemView(i, subways.get(i))

            if (i < subways.size - 1) {
                addLineView()
            }
        }
    }

    private fun updateCheckedSubways(checkedIds: List<Int>) {
        for (i in subways.indices) {
            val subway = subways.get(i)
            colorizeItem(i, checkedIds.contains(subway.id))
        }
    }

    private fun colorizeItem(pos: Int, isChecked: Boolean) {
        if (pos < checkboxesViews.size) {
            val checkbox = checkboxesViews.get(pos)
            checkbox.isChecked = isChecked
            val text = names.get(pos)
            text.setTextColor(if (isChecked) color else colorNeutral)
            if (pos < viewsBetweenCheckboxes.size) {
                val view = viewsBetweenCheckboxes.get(pos)
                view.setBackgroundColor(if (isChecked) color else colorNeutral)
            }
        }
    }

    private fun addItemView(pos: Int, subway: Subway) {
        val container = View.inflate(context, R.layout.item_subway, null)

        val checkBox = container.findViewById<CheckBox>(R.id.checkbox)
        checkBox.buttonDrawable = checkboxDrawable
        checkBox.setOnCheckedChangeListener { compoundButton, b
            ->
            onCheckedChangedListener.onPositionChecked(pos, b)
        }

        val name = container.findViewById<TextView>(R.id.name)
        name.setText(subway.name)

        checkboxesViews.add(checkBox)
        names.add(name)
        linesContainer.addView(container)
    }

    private fun addLineView() {
        val view = View(context)
        view.layoutParams.height = LayoutParams.MATCH_PARENT
        linesContainer.addView(view)
        viewsBetweenCheckboxes.add(view)
    }

    private interface OnPositionCheckedListener {
        fun onPositionChecked(pos: Int, checked: Boolean)
    }
}
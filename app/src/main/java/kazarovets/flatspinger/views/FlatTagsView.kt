package kazarovets.flatspinger.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import kazarovets.flatspinger.R
import kazarovets.flatspinger.model.Tag


class FlatTagsView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FlexboxLayout(context, attrs, defStyleAttr) {

    init {
        flexWrap = FlexWrap.WRAP
        setShowDivider(SHOW_DIVIDER_BEGINNING or SHOW_DIVIDER_MIDDLE)
        setDividerDrawable(context.getDrawable(R.drawable.divider_tags))
    }

    var tags: List<Tag>? = null
        set(value) {
            field = value
            updateViews(tags)
        }

    private fun updateViews(list: List<Tag>?) {
        removeAllViews()
        if (list != null) {
            for (tag in list) {
                if(tag.isNonEmpty()) {
                    addTagView(tag)
                }
            }
        }
    }

    private fun addTagView(tag: Tag) {
        val tagView = LayoutInflater.from(context).inflate(R.layout.item_tag, this, false) as TextView
        tagView.text = tag.getString(context)
        addView(tagView)
    }

}
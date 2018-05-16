package kazarovets.flatspinger.views

import android.content.Context
import android.support.annotation.StringRes
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.ViewFlipper
import kazarovets.flatspinger.R


class ContentViewFlipper : ViewFlipper {
    private val emptyText: TextView by lazy { findViewById<TextView>(R.id.empty) }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        if (!isInEditMode) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.ContentViewFlipper)

            try {

                // ignore custom loader layout
                /*val contentLoadingProgress = array?.getResourceId(R.styleable.ContentViewFlipper_content_loading_progress, 0)
                        ?: 0
                if (contentLoadingProgress > 0) {
                    LayoutInflater.from(context).inflate(contentLoadingProgress, this, true)
                } else {
                    LayoutInflater.from(context).inflate(R.layout.include_loader, this, true)
                }*/
                LayoutInflater.from(context).inflate(R.layout.include_loader, this, true)

                LayoutInflater.from(context).inflate(R.layout.include_no_content, this, true)

                val emptyText = array?.getString(R.styleable.ContentViewFlipper_content_empty_text)

                if (!emptyText.isNullOrBlank()) {
                    setNoContentText(emptyText ?: "")
                }
            } finally {
                array?.recycle()
            }
        }
    }

    fun showLoader() {
        displayedChild = PROGRESS_INDEX
    }

    fun showNoContent() {
        displayedChild = NO_CONTENT_INDEX
    }

    fun showNoContent(@StringRes text: Int) {
        emptyText.setText(text)
        displayedChild = NO_CONTENT_INDEX
    }

    fun showNoContent(text: CharSequence?) {
        emptyText.text = text
        displayedChild = NO_CONTENT_INDEX
    }

    fun showContent() {
        if (displayedChild != CONTENT_INDEX) {
            displayedChild = CONTENT_INDEX
        }
    }

    fun showContentIfNotEmpty(data: List<Any>) {
        if (data.isEmpty()) {
            showNoContent()
        } else {
            showContent()
        }
    }

    fun setNoContentText(@StringRes resId: Int) {
        emptyText.setText(resId)
    }

    fun setNoContentText(text: CharSequence) {
        emptyText.text = text
    }

    fun setNoContentTextColor(color: Int) {
        emptyText.setTextColor(color)
    }

    fun isShowLoading(): Boolean = displayedChild == PROGRESS_INDEX


    companion object {
        val PROGRESS_INDEX = 0
        val NO_CONTENT_INDEX = 1
        val CONTENT_INDEX = 2
    }

}

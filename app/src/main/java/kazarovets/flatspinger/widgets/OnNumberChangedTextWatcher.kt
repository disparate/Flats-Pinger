package kazarovets.flatspinger.widgets

import android.text.Editable
import android.text.TextWatcher
import android.util.Log

interface OnNumberChangedTextWatcher : TextWatcher {


    fun parseText(text: String)

    override fun afterTextChanged(p0: Editable?) {
        try {
            parseText(p0.toString())
        } catch (ex: NumberFormatException) {
            Log.d("OnNumberChangedWatcher", "Exception parsing text in number", ex)

        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }
}

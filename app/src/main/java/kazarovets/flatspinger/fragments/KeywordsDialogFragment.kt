package kazarovets.flatspinger.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import kazarovets.flatspinger.R
import kazarovets.flatspinger.utils.PreferenceUtils
import kotlinx.android.synthetic.main.dialog_keywords.*


class KeywordsDialogFragment : DialogFragment() {

    private var adapter: KeywordsAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_keywords, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme)

        dialogKeywordClose.setOnClickListener { dismiss() }

        dialogKeywordAdd.setOnClickListener {
            addKeyword(dialogKeywordEditText.text?.toString())
            dialogKeywordEditText.setText("")
        }

        dialogKeywordEditText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addKeyword(dialogKeywordEditText.text?.toString())
                dialogKeywordEditText.setText("")
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        dialogKeywordsRecycler.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        adapter = KeywordsAdapter()
        dialogKeywordsRecycler.adapter = adapter
        adapter?.setData(PreferenceUtils.keywords)
    }

    private fun addKeyword(keyword: String?) {
        if (keyword == null || keyword.isEmpty()) {
            return
        }

        adapter?.addFirst(keyword.trim())
        val set = HashSet(PreferenceUtils.keywords)
        set.add(keyword)
        PreferenceUtils.keywords = set
    }

    companion object {

        private class KeywordsAdapter : RecyclerView.Adapter<KeywordsAdapter.KeywordsHolder>() {

            private var keywords: MutableList<String> = ArrayList()

            fun setData(keywordsSet: MutableSet<String>) {
                keywords.clear()
                keywords.addAll(keywordsSet)
            }

            fun addFirst(new: String) {
                if (!keywords.contains(new)) {
                    this.keywords.add(0, new)
                    notifyItemInserted(0)
                }
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeywordsHolder {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.item_keyword_with_x, parent, false)
                return KeywordsHolder(v)
            }

            override fun onBindViewHolder(holder: KeywordsHolder, position: Int) {
                val item = keywords[position]
                holder.name?.text = item
                holder.close?.setOnClickListener {
                    keywords.remove(item)
                    val set = HashSet<String>(keywords)
                    PreferenceUtils.keywords = set
                    notifyItemRemoved(position)
                }
            }

            override fun getItemCount(): Int = keywords.size

            class KeywordsHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
                val name: TextView? = itemView?.findViewById(R.id.keyword_name)
                val close: View? = itemView?.findViewById(R.id.close_button)
            }
        }
    }
}
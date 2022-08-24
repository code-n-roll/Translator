package com.romankaranchuk.translator.ui.translator.selectlang

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.romankaranchuk.translator.R
import com.romankaranchuk.translator.data.database.model.Language

/**
 * Created by roman on 11.4.17.
 */
class SelectLanguageRecyclerAdapter(
    private val itemClickListener: (Language) -> Unit
) : RecyclerView.Adapter<SelectLanguageRecyclerAdapter.ViewHolder>() {

    // if checkedPosition = -1, there is no default selection
    // if checkedPosition = 0, 1st item is selected by default
    private var checkedPosition = 0

    private val mItems: MutableList<Language> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_src_trg_lang, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mItems[position], position)
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    fun updateAll(newItems: List<Language>, selectedPosition: Int) {
        checkedPosition = selectedPosition
        mItems.clear()
        mItems.addAll(newItems)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mIsSelected: ImageView = itemView.findViewById(R.id.selected_choose_src_trg_lang)
        private val mLanguage: TextView = itemView.findViewById(R.id.choose_src_trg_lang)
        private val context: Context = itemView.context

        fun bind(item: Language, position: Int) {
            mLanguage.text = item.name

            itemView.setOnClickListener {
                itemClickListener.invoke(item)

                setSelected(true)
                if (checkedPosition != position) {
                    notifyItemChanged(checkedPosition)
                    checkedPosition = position
                }
            }

            if (checkedPosition == -1) {
                setSelected(false)
            } else {
                if (checkedPosition == position) {
                    setSelected(true)
                } else {
                    setSelected(false)
                }
            }
        }

        fun setSelected(isSelected: Boolean) {
            if (isSelected) {
                mIsSelected.visibility = View.VISIBLE
                itemView.setBackgroundColor(
                    ContextCompat.getColor(context, R.color.colorSelectedItem)
                )
            } else {
                mIsSelected.visibility = View.INVISIBLE
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
            }
        }
    }
}
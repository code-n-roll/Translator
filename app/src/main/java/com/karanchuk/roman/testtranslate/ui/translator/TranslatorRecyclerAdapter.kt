package com.karanchuk.roman.testtranslate.ui.translator

import android.graphics.Typeface
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.karanchuk.roman.testtranslate.R
import com.karanchuk.roman.testtranslate.data.database.model.PartOfSpeech
import com.karanchuk.roman.testtranslate.data.database.model.Translation
import com.karanchuk.roman.testtranslate.ui.view.CustomSynonyms
import com.karanchuk.roman.testtranslate.ui.view.TextGenLayout
import java.util.ArrayList

/**
 * Created by roman on 11.4.17.
 */
class TranslatorRecyclerAdapter(
    private val mOnItemClickListener: (View, String) -> Unit
) : RecyclerView.Adapter<TranslatorRecyclerAdapter.ViewHolder>() {

    private val mPartsOfSpeech: MutableList<PartOfSpeech> = mutableListOf()
    private val mItems: MutableList<Translation> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dict_definition, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mItems[position])
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    fun updateAll(
        newItems: List<Translation>,
        partsOfSpeech: List<PartOfSpeech>
    ) {
        mPartsOfSpeech.clear()
        mPartsOfSpeech.addAll(partsOfSpeech)

        mItems.clear()
        mItems.addAll(newItems)

        notifyDataSetChanged()
    }

    inner class ViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mNumberDictDefinItem: TextView = itemView.findViewById(R.id.number_dict_defin_item)
        private val mTranslDictDefinItem: TextView = itemView.findViewById(R.id.transl_dict_defin_item)
        private val mMeanDictDefinItem: TextView = itemView.findViewById(R.id.mean_dict_defin_item)
        private val mExprDictDefinItem: TextView = itemView.findViewById(R.id.expr_dict_defin_item)
        private val mLabelPartOfSpeech: TextView = itemView.findViewById(R.id.tv_label_part_of_speech)
        private val mTextTranscription: TextGenLayout = itemView.findViewById(R.id.layout_text_transcription)

        fun bind(item: Translation) {
            initHeaderAndSubHeaders(item)
            mNumberDictDefinItem.text = item.number
            initSynonyms(item)
            if (item.meanings != null && !item.meanings.isEmpty()) {
                mMeanDictDefinItem.text = item.representMeanings
                mMeanDictDefinItem.visibility = View.VISIBLE
            } else {
                mMeanDictDefinItem.visibility = View.GONE
            }
            if (item.expressions != null && !item.expressions.isEmpty()) {
                mExprDictDefinItem.text = item.representExpressions
                mExprDictDefinItem.visibility = View.VISIBLE
            } else {
                mExprDictDefinItem.visibility = View.GONE
            }
        }

        private fun initHeaderAndSubHeaders(item: Translation) {
            if (item.number == "1") {
                if (mItems!!.indexOf(item) == 0) {
                    if (mPartsOfSpeech != null) {
                        mTextTranscription.text = mPartsOfSpeech!![0].text
                        mTextTranscription.genStyle = Typeface.NORMAL
                        if (mPartsOfSpeech!![0].transcription != null) {
                            mTextTranscription.gen = " [" + mPartsOfSpeech!![0].transcription + "]"
                        }
                    }
                    mTextTranscription.visibility = View.VISIBLE
                } else {
                    mTextTranscription.visibility = View.GONE
                }
                if (mPartsOfSpeech != null) {
                    for (partOfSpeech in mPartsOfSpeech!!) {
                        if (partOfSpeech.translations.contains(item)) {
                            mLabelPartOfSpeech.text = partOfSpeech.name
                            break
                        }
                    }
                }
                mLabelPartOfSpeech.visibility = View.VISIBLE
            } else {
                mLabelPartOfSpeech.visibility = View.GONE
                mTextTranscription.visibility = View.GONE
            }
        }

        private fun initSynonyms(item: Translation) {
            val synonyms = CustomSynonyms(itemView.context, item)
            mTranslDictDefinItem.text = synonyms.toSpannable(mOnItemClickListener)
            mTranslDictDefinItem.movementMethod = LinkMovementMethod.getInstance()
        }
    }
}
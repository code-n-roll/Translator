package com.karanchuk.roman.testtranslate.presentation.view.adapter;

import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.karanchuk.roman.testtranslate.R;
import com.karanchuk.roman.testtranslate.presentation.model.PartOfSpeech;
import com.karanchuk.roman.testtranslate.presentation.model.Synonym;
import com.karanchuk.roman.testtranslate.presentation.model.Translation;
import com.karanchuk.roman.testtranslate.presentation.view.custom.TextGenLayout;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roman on 11.4.17.
 */

public class TranslatorRecyclerAdapter extends RecyclerView.Adapter<TranslatorRecyclerAdapter.ViewHolder>{
    private final ArrayList<Translation> mItems;
    private List<PartOfSpeech> mPartsOfSpeech;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener{
        void onItemClick(View view, String text);
    }

    public TranslatorRecyclerAdapter(final ArrayList<Translation> items,
                                     List<PartOfSpeech> partsOfSpeech,
                                     OnItemClickListener onItemClickListenerlistener){
        mItems = items;
        mPartsOfSpeech = partsOfSpeech;
        mOnItemClickListener = onItemClickListenerlistener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.content_dict_definition_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView mNumberDictDefinItem;
        private FlowLayout mTranslDictDefinItem;
        private TextView mMeanDictDefinItem;
        private TextView mExprDictDefinItem;
        private TextView mLabelPartOfSpeech;
        private TextGenLayout mTextTranscription;
        private View mView;

        public ViewHolder(final View view){
            super(view);
            mView = view;


            mLabelPartOfSpeech = mView.findViewById(R.id.tv_label_part_of_speech);
            mTextTranscription = mView.findViewById(R.id.layout_text_transcription);
            mNumberDictDefinItem = mView.findViewById(R.id.number_dict_defin_item);
            mTranslDictDefinItem = mView.findViewById(R.id.transl_dict_defin_item);
            mMeanDictDefinItem = mView.findViewById(R.id.mean_dict_defin_item);
            mExprDictDefinItem = mView.findViewById(R.id.expr_dict_defin_item);
        }

        public void bind(final Translation item){
            initHeaderAndSubHeaders(item);
            mNumberDictDefinItem.setText(item.getNumber());


            initSynonyms(item);
//            mTranslDictDefinItem.setText(item.getRepresentSynonyms());


            if (item.getMeanings() != null && !item.getMeanings().isEmpty()) {
                mMeanDictDefinItem.setText(item.getRepresentMeanings());
                mMeanDictDefinItem.setVisibility(View.VISIBLE);
            } else {
                mMeanDictDefinItem.setVisibility(View.GONE);
            }
            if (item.getExpressions() != null && !item.getExpressions().isEmpty()){
                mExprDictDefinItem.setText(item.getRepresentExpressions());
                mExprDictDefinItem.setVisibility(View.VISIBLE);
            } else {
                mExprDictDefinItem.setVisibility(View.GONE);
            }
        }

        private void initHeaderAndSubHeaders(Translation item){
            if (item.getNumber().equals("1")){
                if (mItems.indexOf(item) == 0) {
                    if (mPartsOfSpeech != null) {
                        mTextTranscription.setText(mPartsOfSpeech.get(0).getText());
                        mTextTranscription.setGenStyle(Typeface.NORMAL);
                        if (mPartsOfSpeech.get(0).getTranscription() != null) {
                            mTextTranscription.setGen(" [" + mPartsOfSpeech.get(0).getTranscription() + "]");
                        }
                    }
                    mTextTranscription.setVisibility(View.VISIBLE);
                } else {
                    mTextTranscription.setVisibility(View.GONE);
                }

                if (mPartsOfSpeech != null) {
                    for (PartOfSpeech partOfSpeech : mPartsOfSpeech) {
                        if (partOfSpeech.getTranslations().contains(item)) {
                            mLabelPartOfSpeech.setText(partOfSpeech.getName());
                            break;
                        }
                    }
                }
                mLabelPartOfSpeech.setVisibility(View.VISIBLE);
            } else {
                mLabelPartOfSpeech.setVisibility(View.GONE);
                mTextTranscription.setVisibility(View.GONE);
            }
        }


        private void initSynonyms(Translation item) {
            TextView textView = new TextView(mView.getContext());
            String result = item.getText();
            if (item.getGen() != null) {
//                textGenLayout.setGen(" " + item.getGen());
                result = result.concat(" " + item.getGen());
            }
            if (item.getSynonyms() != null) {
//                textGenLayout.setComma(", ");
                result = result.concat(", ");
            }
            Spannable spanText = Spannable.Factory.getInstance().newSpannable(result);
            ClickableSpan spanTextListener = new ClickableSpan() {

                @Override
                public void onClick(View view) {
                    Toast.makeText(mView.getContext(), "span text clicked", Toast.LENGTH_LONG).show();
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setUnderlineText(false);
//                    ds.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
                    ds.setColor(ContextCompat.getColor(mView.getContext(), R.color.colorTransl));
                    ds.setTextSize(50f);
                }
            };
            ClickableSpan spanCommaListener = new ClickableSpan() {
                @Override
                public void onClick(View view) {

                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setUnderlineText(false);
                    ds.setTextSize(50f);
                    ds.setColor(ContextCompat.getColor(mView.getContext(), R.color.colorTransl));
                }
            };
            spanText.setSpan(spanTextListener, 0, item.getText().length(), Spanned.SPAN_COMPOSING);
            spanText.setSpan(spanCommaListener, item.getText().length(), spanText.length(), Spanned.SPAN_COMPOSING);
            textView.setText(spanText);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            mTranslDictDefinItem.removeAllViews();
            mTranslDictDefinItem.addView(textView);
//            TextGenLayout textGenLayout = new TextGenLayout(mView.getContext());
//            textGenLayout.setTextColor(R.color.colorTransl);
//            textGenLayout.setGenColor(android.R.color.darker_gray);
//            textGenLayout.setGenStyle(Typeface.ITALIC);
//            textGenLayout.setCommaColor(R.color.colorTransl);
//            textGenLayout.setBackgroundResource(R.drawable.selector_synonym);
//
//            textGenLayout.setText(item.getText());

            // adding comma or space were here

//            textGenLayout.setOnClickListener(view ->
//                    mOnItemClickListener.onItemClick(view, item.getText()));
//            mTranslDictDefinItem.removeAllViews();
//            mTranslDictDefinItem.addView(textGenLayout);


            if (item.getSynonyms() != null) {
                int index = 0;
                for (Synonym synonym : item.getSynonyms()) {
                    index++;
//                    textGenLayout = new TextGenLayout(mView.getContext());
//                    textGenLayout.setTextColor(R.color.colorTransl);
//                    textGenLayout.setGenColor(android.R.color.darker_gray);
//                    textGenLayout.setGenStyle(Typeface.ITALIC);
//                    textGenLayout.setCommaColor(R.color.colorTransl);
//                    textGenLayout.setBackgroundResource(R.drawable.selector_synonym);

                    if (synonym.getGen() == null) {
//                        textGenLayout.setText(synonym.getText() + "");
                    } else {
//                        textGenLayout.setText(synonym.getText() + " ");
                    }
//                    textGenLayout.setGen(synonym.getGen());
                    if (item.getSynonyms().size() != index) {
//                        textGenLayout.setComma(", ");
                    } else {
//                        textGenLayout.setComma("");
                    }
//                    textGenLayout.setOnClickListener(view ->
//                            mOnItemClickListener.onItemClick(view, synonym.getText()));
//                    mTranslDictDefinItem.addView(textGenLayout);
                }
            }
        }
    }



    public void updateData(final List<Translation> translations,
                           final List<PartOfSpeech> partsOfSpeech){
        mPartsOfSpeech = partsOfSpeech;
        mItems.clear();
        mItems.addAll(translations);
        notifyDataSetChanged();
    }
}

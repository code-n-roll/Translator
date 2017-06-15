package com.karanchuk.roman.testtranslate.presentation.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.karanchuk.roman.testtranslate.R;
import com.karanchuk.roman.testtranslate.presentation.model.Translation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roman on 11.4.17.
 */

public class TranslatorRecyclerAdapter extends RecyclerView.Adapter<TranslatorRecyclerAdapter.ViewHolder>{
    private final ArrayList<Translation> mItems;

    public TranslatorRecyclerAdapter(final ArrayList<Translation> items){
        mItems = items;
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
        private final TextView mNumberDictDefinItem,
                         mTranslDictDefinItem,
                         mMeanDictDefinItem,
                         mExprDictDefinItem;
        private final View mView;

        public ViewHolder(final View view){
            super(view);
            mView = view;

            mNumberDictDefinItem = (TextView) mView.findViewById(R.id.number_dict_defin_item);
            mTranslDictDefinItem = (TextView) mView.findViewById(R.id.transl_dict_defin_item);
            mMeanDictDefinItem = (TextView) mView.findViewById(R.id.mean_dict_defin_item);
            mExprDictDefinItem = (TextView) mView.findViewById(R.id.expr_dict_defin_item);
        }

        public void bind(final Translation item){
            mNumberDictDefinItem.setText(item.getNumber());
            mTranslDictDefinItem.setText(item.getRepresentSynonyms());
            if (!item.getMeanings().isEmpty()) {
                mMeanDictDefinItem.setText(item.getMeanings());
                mMeanDictDefinItem.setVisibility(View.VISIBLE);
            } else {
                mMeanDictDefinItem.setVisibility(View.GONE);
            }
            if (!item.getExpressions().isEmpty()){
                mExprDictDefinItem.setText(item.getExpressions());
                mExprDictDefinItem.setVisibility(View.VISIBLE);
            } else {
                mExprDictDefinItem.setVisibility(View.GONE);
            }
        }
    }
    public void updateData(final List<Translation> translations){
        mItems.clear();
        mItems.addAll(translations);
        notifyDataSetChanged();
    }
}

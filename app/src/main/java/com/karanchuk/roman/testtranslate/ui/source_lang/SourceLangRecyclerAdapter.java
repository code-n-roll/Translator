package com.karanchuk.roman.testtranslate.ui.source_lang;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.karanchuk.roman.testtranslate.R;
import com.karanchuk.roman.testtranslate.data.Language;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roman on 11.4.17.
 */

public class SourceLangRecyclerAdapter extends RecyclerView.Adapter<SourceLangRecyclerAdapter.ViewHolder>{
    public interface OnItemClickListener {
        void onItemClick(Language item);
    }

    private final List<Language> mItems;
    private final OnItemClickListener mListener;
    private Context mContext;

    public SourceLangRecyclerAdapter(List<Language> items, OnItemClickListener listener, Context context){
        mItems = items;
        mListener = listener;
        mContext = context;

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.content_src_trg_lang_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(mItems.get(position), mListener);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView mIsSelected;
        private TextView mLanguage;
        private View mView;

        public ViewHolder(View view){
            super(view);
            mView = view;
            mLanguage = (TextView) view.findViewById(R.id.choose_src_trg_lang);
            mIsSelected = (ImageView) view.findViewById(R.id.selected_choose_src_trg_lang);
        }


        public void bind(final Language item,
                         final SourceLangRecyclerAdapter.OnItemClickListener listener){

            mLanguage.setText(item.getName());
            if (item.isSelected()){
                mIsSelected.setVisibility(View.VISIBLE);
                mView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorSelectedItem));
            } else {
                mIsSelected.setVisibility(View.INVISIBLE);
                mView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            }
            mView.setOnClickListener(new View.OnClickListener(){
                @Override public void onClick(View v){
                    listener.onItemClick(item);
                }
            });

        }
    }
}



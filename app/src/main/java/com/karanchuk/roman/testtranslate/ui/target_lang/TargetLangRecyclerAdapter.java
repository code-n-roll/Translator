package com.karanchuk.roman.testtranslate.ui.target_lang;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.karanchuk.roman.testtranslate.R;
import com.karanchuk.roman.testtranslate.data.Language;

import java.util.ArrayList;

/**
 * Created by roman on 17.4.17.
 */

public class TargetLangRecyclerAdapter extends RecyclerView.Adapter<TargetLangRecyclerAdapter.ViewHolder>{
    public interface OnItemClickListener {
        void onItemClick(Language item);
    }

    private final ArrayList<Language> mItems;
    private final OnItemClickListener mListener;

    public TargetLangRecyclerAdapter(ArrayList<Language> items, OnItemClickListener listener){
        mItems = items;
        mListener = listener;
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
                         final OnItemClickListener listener){

            mLanguage.setText(item.getName());
            mIsSelected.setVisibility(View.GONE);
            mView.setOnClickListener(new View.OnClickListener(){
                @Override public void onClick(View v){
                    listener.onItemClick(item);
                }
            });

        }
    }
}

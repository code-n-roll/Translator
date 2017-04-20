package com.karanchuk.roman.testtranslate.ui.stored.favorites;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.karanchuk.roman.testtranslate.R;
import com.karanchuk.roman.testtranslate.data.TranslatedItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roman on 9.4.17.
 */

public class SearchListRecyclerAdapter extends RecyclerView.Adapter<SearchListRecyclerAdapter.ViewHolder>{
    public interface OnItemClickListener {
        void onItemClick(TranslatedItem item);
    }

    private List<TranslatedItem> mItems;
    private final OnItemClickListener mItemListener, mIsFavoriteListener;

    public SearchListRecyclerAdapter(List<TranslatedItem> items,
                                     OnItemClickListener itemListener,
                                     OnItemClickListener isFavoriteListener){
        mItems = items;
        mItemListener = itemListener;
        mIsFavoriteListener = isFavoriteListener;
    }

    @Override
    public SearchListRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.content_favorite_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(mItems.get(position), mItemListener, mIsFavoriteListener);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageButton mIsFavoriteView;
        private TextView mSrcTrgLanguage, mSrcMeaning, mTrgMeaning;
        private View mView;

        public ViewHolder(View view){
            super(view);
            mView = view;
            mIsFavoriteView = (ImageButton) view.findViewById(R.id.imagebutton_isfavorite_favorite_item);
            mSrcTrgLanguage = (TextView) view.findViewById(R.id.src_trg_languages);
            mSrcMeaning = (TextView) view.findViewById(R.id.src_meaning);
            mTrgMeaning = (TextView) view.findViewById(R.id.trg_meaning);
        }

        public void bind(final TranslatedItem item,
                         final OnItemClickListener itemListener,
                         final OnItemClickListener isFavoriteListener){



            if (item.isFavorite()){
                mIsFavoriteView.setImageResource(R.drawable.bookmark_black_shape_gold512);
            } else {
                mIsFavoriteView.setImageResource(R.drawable.bookmark_black_shape_light512);
            }
            mSrcTrgLanguage.setText(item.getSrcLanguage() +" - " + item.getTrgLanguage());
            mSrcMeaning.setText(item.getSrcMeaning());
            mTrgMeaning.setText(item.getTrgMeaning());

            mView.setOnClickListener(new View.OnClickListener(){
               @Override public void onClick(View v){
                   itemListener.onItemClick(item);
               }
            });

            mIsFavoriteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isFavoriteListener.onItemClick(item);
                }
            });

        }
    }
    public void setFilter(ArrayList<TranslatedItem> items){
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }
}

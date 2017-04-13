package com.karanchuk.roman.testtranslate.translate;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.karanchuk.roman.testtranslate.R;
import com.karanchuk.roman.testtranslate.data.DictDefinition;

import java.util.ArrayList;

/**
 * Created by roman on 11.4.17.
 */

public class TranslateRecyclerAdapter extends RecyclerView.Adapter<TranslateRecyclerAdapter.ViewHolder>{
    private ArrayList<DictDefinition> mItems;

    public TranslateRecyclerAdapter(ArrayList<DictDefinition> items){
        mItems = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.content_dict_defin_item, parent, false);
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
        private TextView mNumberDictDefinItem,
                         mTranslDictDefinItem,
                         mMeanDictDefinItem,
                         mExprDictDefinItem;
        private View mView;

        public ViewHolder(View view){
            super(view);
            mView = view;
            mNumberDictDefinItem = (TextView) mView.findViewById(R.id.number_dict_defin_item);
            mTranslDictDefinItem = (TextView) mView.findViewById(R.id.transl_dict_defin_item);
            mMeanDictDefinItem = (TextView) mView.findViewById(R.id.mean_dict_defin_item);
            mExprDictDefinItem = (TextView) mView.findViewById(R.id.expr_dict_defin_item);
        }

        public void bind(DictDefinition item){
            mNumberDictDefinItem.setText(item.getNumber());
            mTranslDictDefinItem.setText(item.getTranslation());
            mMeanDictDefinItem.setText(item.getMeaning());
            mExprDictDefinItem.setText(item.getExpression());
        }
    }
}

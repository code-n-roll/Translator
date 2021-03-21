package com.karanchuk.roman.testtranslate.ui.stored;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.karanchuk.roman.testtranslate.R;
import com.karanchuk.roman.testtranslate.data.database.model.TranslatedItem;

import java.util.List;

/**
 * Created by roman on 9.4.17.
 */

public class StoredRecyclerAdapter extends RecyclerView.Adapter<StoredRecyclerAdapter.ViewHolder>{
    private List<TranslatedItem> mItems;
    private final OnItemClickListener mItemClickListener, mIsFavoriteListener;
    private int mPosition;
    private int mUniqueFragmentId;

    public interface OnItemClickListener {
        void onItemClick(TranslatedItem item);
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        this.mPosition = position;
    }

    public StoredRecyclerAdapter(List<TranslatedItem> items,
                                 OnItemClickListener itemClickListener,
                                 OnItemClickListener isFavoriteListener,
                                 int uniqueFragmentId){
        mItems = items;
        mItemClickListener = itemClickListener;
        mIsFavoriteListener = isFavoriteListener;
        mUniqueFragmentId = uniqueFragmentId;
    }

    @NonNull
    @Override
    public StoredRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_favorite, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.bind(mItems.get(position), mItemClickListener, mIsFavoriteListener);

        holder.itemView.setOnLongClickListener(v -> {
            setPosition(holder.getAdapterPosition());
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        private ImageButton mIsFavoriteView;
        private TextView mSrcTrgLanguage, mSrcMeaning, mTrgMeaning;
        private View mView;

        public ViewHolder(View view){
            super(view);
            mView = view;
            view.setOnCreateContextMenuListener(this);

            mIsFavoriteView = mView.findViewById(R.id.imagebutton_isfavorite_favorite_item);
            mSrcTrgLanguage = mView.findViewById(R.id.src_trg_languages);
            mSrcMeaning = mView.findViewById(R.id.src_meaning);
            mTrgMeaning = mView.findViewById(R.id.trg_meaning);
        }

        public void bind(final TranslatedItem item,
                         final OnItemClickListener itemListener,
                         final OnItemClickListener isFavoriteListener){
            if (item.isFavorite()){
                mIsFavoriteView.setImageResource(R.drawable.bookmark_black_shape_gold512);
            } else {
                mIsFavoriteView.setImageResource(R.drawable.bookmark_black_shape_light512);
            }
            mSrcTrgLanguage.setText(item.getSrcLanguageForAPI().toUpperCase() +
                    " - " + item.getTrgLanguageForAPI().toUpperCase());
            mSrcMeaning.setText(item.getSrcMeaning());
            mTrgMeaning.setText(item.getTrgMeaning());

            mView.setOnClickListener(v -> itemListener.onItemClick(item));
            mIsFavoriteView.setOnClickListener(v -> isFavoriteListener.onItemClick(item));
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(mUniqueFragmentId,
                    R.id.menu_item_delete,
                    0,
                    R.string.menu_item_delete_option);
        }
    }
    public void setFilter(List<TranslatedItem> items){
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }
}

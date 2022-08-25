package com.romankaranchuk.translator.ui.stored;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.romankaranchuk.translator.R;


public class ClearStoredDialogFragment extends DialogFragment {
    private ClearStoredDialogListener mListener;
    private String mCurTitle;

    public interface ClearStoredDialogListener {
        void onDialogPositiveClick(final ClearStoredDialogFragment dialog);
        void onDialogNegativeClick(final ClearStoredDialogFragment dialog);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            mListener = (ClearStoredDialogListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString() +
            " must implement ClearStoredDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mCurTitle = getArguments().getString("title");
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(mCurTitle)
                .setMessage(getResources().getString(R.string.dialog_clear)+mCurTitle+"?")
                .setPositiveButton(R.string.yes,
                        (dialog, which) -> mListener.onDialogPositiveClick(ClearStoredDialogFragment.this))
                .setNegativeButton(R.string.cancel,
                        (dialog, which) -> mListener.onDialogNegativeClick(ClearStoredDialogFragment.this));
        return builder.create();
    }
}

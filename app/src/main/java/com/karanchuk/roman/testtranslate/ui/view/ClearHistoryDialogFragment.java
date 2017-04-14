package com.karanchuk.roman.testtranslate.ui.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.karanchuk.roman.testtranslate.R;

/**
 * Created by roman on 14.4.17.
 */

public class ClearHistoryDialogFragment extends DialogFragment {
    public interface ClearHistoryDialogListener {
        void onDialogPositiveClick(ClearHistoryDialogFragment dialog);
        void onDialogNegativeClick(ClearHistoryDialogFragment dialog);
    }

    ClearHistoryDialogListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            mListener = (ClearHistoryDialogListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString() +
            " must implement ClearHistoryDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("History")
                .setMessage(R.string.dialog_clear_history)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onDialogPositiveClick(ClearHistoryDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onDialogNegativeClick(ClearHistoryDialogFragment.this);
                    }
                });
        return builder.create();
    }
}

package com.emdevsite.todayhist;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

// TODO: Clean this up
public class LicenseFragment extends DialogFragment implements DialogInterface.OnClickListener {
    public static final String TAG = "com.emdevsite.todayhist.LICENSE_DIALOG";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int paddingSz = getResources().getDimensionPixelSize(R.dimen.license_padding);
        TextView content = new TextView(getContext());
        content.setText(R.string.license_content);
        content.setPadding(paddingSz, 0, paddingSz, 0);
        content.setMovementMethod(LinkMovementMethod.getInstance());

        Activity parent = getActivity();
        if (parent != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            return builder.setView(content)
                .setTitle(R.string.license)
                .setNeutralButton(R.string.license_ok, this)
                .create();
        } else {
            throw new IllegalStateException("Parent activity is null");
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_NEUTRAL: {
                dismiss();
                break;
            }

            default: {
                break;
            }
        }
    }
}

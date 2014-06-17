package com.jmartin.writeily.dialog;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;

import com.jmartin.writeily.R;

/**
 * Created by jeff on 2014-04-11.
 */
public class AboutDialog extends DialogPreference {

    public AboutDialog(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.text_dialog);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(null);
        setDialogIcon(null);
    }
}

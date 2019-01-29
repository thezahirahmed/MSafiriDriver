package com.eleganzit.msafiridriver.lib;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by eleganz on 12/2/18.
 */

public class RobotoMediumTextView extends android.support.v7.widget.AppCompatTextView {

    public RobotoMediumTextView(Context context) {
        super(context);
    }

    public RobotoMediumTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setTypeface(Typeface tf) {
        super.setTypeface(Typeface.createFromAsset(getContext().getAssets(),"fonts/Roboto-Regular.ttf"));
    }
}

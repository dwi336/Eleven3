package com.cyanogenmod.eleven.widgets;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.support.v7.widget.AppCompatImageButton;

import com.cyanogenmod.eleven.R;
import com.cyanogenmod.eleven.utils.ApolloUtils;

public abstract class AudioButton extends AppCompatImageButton implements OnClickListener, OnLongClickListener {
    public static float ACTIVE_ALPHA = 1.0f;
    public static float INACTIVE_ALPHA = 0.4f;

    @SuppressWarnings("deprecation")
    public AudioButton(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        setPadding(0, 0, 0, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        	setBackground(ContextCompat.getDrawable(context, R.drawable.selectable_background));
        } else {
            setBackgroundResource(R.drawable.selectable_background);
        }
        // Control playback (cycle shuffle)
        setOnClickListener(this);
        // Show the cheat sheet
        setOnLongClickListener(this);
    }

    @Override
    public boolean onLongClick(final View view) {
        if (TextUtils.isEmpty(view.getContentDescription())) {
            return false;
        } else {
            ApolloUtils.showCheatSheet(view);
            return true;
        }
    }
}

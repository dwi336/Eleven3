package com.cyanogenmod.eleven.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import com.cyanogenmod.eleven.R;

public class RobotoTextView extends TextView {

    public RobotoTextView(Context context) {
        super(context);
    }
    
    public RobotoTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!isInEditMode()){ 
            applyCustomFont(context, attrs);
        }
    }

    public RobotoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()){ 
            applyCustomFont(context, attrs);
        }
    }

    public RobotoTextView(Context context, int typefaceValue) {
        super(context);
        if (!isInEditMode()){ 
            setTypeface(Roboto.getTypeface(context, typefaceValue));
        }
    }
    
    private void applyCustomFont(Context context, AttributeSet attrs) {
        TypedArray attributeArray = context.obtainStyledAttributes(
                  attrs, R.styleable.RobotoTextView);

        if (attributeArray.hasValue(R.styleable.RobotoTextView_typeface)) {
            int typefaceValue = attributeArray.getInt(R.styleable.RobotoTextView_typeface,0);
            
            setTypeface(Roboto.getTypeface(context, typefaceValue));
        } 
    
        attributeArray.recycle();
    }
}
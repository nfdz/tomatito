package io.github.nfdz.tomatito.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import io.github.nfdz.tomatito.R;

public class NumberPickerPreference extends DialogPreference {

    // default range
    public static final int DEFAULT_MAX_VALUE = 60;
    public static final int DEFAULT_MIN_VALUE = 1;
    public static final String DEFAULT_SUM_SUFFIX = "";

    public static final boolean WRAP_SELECTOR_WHEEL = true;

    private NumberPicker mPicker;
    private int mValue;
    private int mMinValue;
    private int mMaxValue;
    private String mSummarySuffix;

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttributes(context, attrs);
    }

    public NumberPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parseAttributes(context, attrs);
    }

    private void parseAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.number_picker_attrs);
        mMaxValue = a.getInteger(R.styleable.number_picker_attrs_max_number, DEFAULT_MAX_VALUE);
        mMinValue = a.getInteger(R.styleable.number_picker_attrs_min_number, DEFAULT_MIN_VALUE);
        String suffix = a.getString(R.styleable.number_picker_attrs_summary_suffix);
        mSummarySuffix = TextUtils.isEmpty(suffix) ? DEFAULT_SUM_SUFFIX : suffix;
        a.recycle();
    }

    @Override
    protected View onCreateDialogView() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;

        mPicker = new NumberPicker(getContext());
        mPicker.setLayoutParams(layoutParams);

        FrameLayout dialogView = new FrameLayout(getContext());
        dialogView.addView(mPicker);

        return dialogView;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        mPicker.setMinValue(mMinValue);
        mPicker.setMaxValue(mMaxValue);
        mPicker.setWrapSelectorWheel(WRAP_SELECTOR_WHEEL);
        mPicker.setValue(getValue());
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            mPicker.clearFocus();
            int newValue = mPicker.getValue();
            if (callChangeListener(newValue)) {
                setValue(newValue);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, mMinValue);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setValue(restorePersistedValue ? getPersistedInt(mMinValue) : (Integer) defaultValue);
    }

    public void setValue(int value) {
        mValue = value;
        persistInt(mValue);
        notifyChanged();
    }

    public int getValue() {
        return mValue;
    }

    @Override
    public CharSequence getSummary() {
        return Integer.toString(mValue) + " " + mSummarySuffix;
    }
}
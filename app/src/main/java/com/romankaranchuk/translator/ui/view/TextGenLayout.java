package com.romankaranchuk.translator.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.romankaranchuk.translator.R;


public class TextGenLayout extends LinearLayout {
    private TextView mTextTextView;
    private TextView mGenTextView;
    private TextView mCommaTextView;
    private String mText;
    private String mGen;
    private String mComma;
    private int mTextColor;
    private int mGenColor;
    private int mCommaColor;
    private float mTextSize;
    private float mGenSize;
    private float mCommaSize;
    private int mTextStyle;
    private int mGenStyle;
    private int mCommaStyle;

    public TextGenLayout(Context context) {
        super(context);
        initComponents(context);
        initAttrs(context, null);
    }

    public TextGenLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initComponents(context);
        initAttrs(context,attrs);
    }

    public TextGenLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initComponents(context);
        initAttrs(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TextGenLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initComponents(context);
        initAttrs(context, attrs);
    }

    private void initComponents(Context context){
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_textgen, this);

        mTextTextView = findViewById(R.id.tv_text_layout_textgen);
        mGenTextView = findViewById(R.id.tv_gen_layout_textgen);
        mCommaTextView = findViewById(R.id.tv_comma_layout_textgen);
    }

    private void initAttrs(Context context, AttributeSet attributeSet){
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attributeSet, R.styleable.TextGenLayout, 0, 0
        );

        try {
            mText = typedArray.getString(R.styleable.TextGenLayout_textText);
            mGen = typedArray.getString(R.styleable.TextGenLayout_genText);
            mComma = typedArray.getString(R.styleable.TextGenLayout_commaText);
            mTextColor = typedArray.getColor(R.styleable.TextGenLayout_textColor,
                    ContextCompat.getColor(context, android.R.color.black));
            mGenColor = typedArray.getColor(R.styleable.TextGenLayout_genColor,
                    ContextCompat.getColor(context, android.R.color.darker_gray));
            mCommaColor = typedArray.getColor(R.styleable.TextGenLayout_commaColor,
                    ContextCompat.getColor(context, android.R.color.black));
            mTextSize = typedArray.getDimension(R.styleable.TextGenLayout_textSize, 20);
            mGenSize = typedArray.getDimension(R.styleable.TextGenLayout_genSize, 20);
            mCommaSize = typedArray.getDimension(R.styleable.TextGenLayout_commaSize, 20);


            Typeface textStyle = chooseTextStyle(typedArray.getInt(R.styleable.TextGenLayout_textTextStyle, 0));
            Typeface genStyle = chooseTextStyle(typedArray.getInt(R.styleable.TextGenLayout_genTextStyle, 0));
            Typeface commaStyle = chooseTextStyle(typedArray.getInt(R.styleable.TextGenLayout_commaTextStyle, 0));

            mTextTextView.setText(mText);
            mGenTextView.setText(mGen);
            mCommaTextView.setText(mComma);

            mTextTextView.setTextColor(mTextColor);
            mGenTextView.setTextColor(mGenColor);
            mCommaTextView.setTextColor(mCommaColor);

            mTextTextView.setTextSize(mTextSize);
            mGenTextView.setTextSize(mGenSize);
            mCommaTextView.setTextSize(mCommaSize);

            mTextTextView.setTypeface(textStyle);
            mGenTextView.setTypeface(genStyle);
            mCommaTextView.setTypeface(commaStyle);
        } finally {
            typedArray.recycle();
        }
    }

    public void setTextLink(){

    }

    private Typeface chooseTextStyle(int style){
        Typeface typeface;
        switch (style){
            case Typeface.NORMAL:
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL);
                break;
            case Typeface.BOLD:
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);
                break;
            case Typeface.ITALIC:
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC);
                break;
            case Typeface.BOLD_ITALIC:
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC);
                break;
            default:
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL);
                break;
        }
        return typeface;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
        mTextTextView.setTextColor(ContextCompat.getColor(getContext(), mTextColor));
    }

    public int getGenColor() {
        return mGenColor;
    }

    public void setGenColor(int genColor) {
        mGenColor = genColor;
        mGenTextView.setTextColor(ContextCompat.getColor(getContext(), mGenColor));
    }

    public int getCommaColor() {
        return mCommaColor;
    }

    public void setCommaColor(int commaColor) {
        mCommaColor = commaColor;
        mCommaTextView.setTextColor(ContextCompat.getColor(getContext(), mCommaColor));
    }

    public float getTextSize() {
        return mTextSize;
    }

    public void setTextSize(float textSize) {
        mTextSize = textSize;
        mTextTextView.setTextSize(mTextSize);
    }

    public float getGenSize() {
        return mGenSize;
    }

    public void setGenSize(float genSize) {
        mGenSize = genSize;
        mGenTextView.setTextSize(mGenSize);
    }

    public float getCommaSize() {
        return mCommaSize;
    }

    public void setCommaSize(float commaSize) {
        mCommaSize = commaSize;
        mCommaTextView.setTextSize(mCommaSize);
    }

    public int getTextStyle() {
        return mTextStyle;
    }

    public void setTextStyle(int textStyle) {
        mTextStyle = textStyle;
        mTextTextView.setTypeface(chooseTextStyle(mTextStyle));
    }

    public int getGenStyle() {
        return mGenStyle;
    }

    public void setGenStyle(int genStyle) {
        mGenStyle = genStyle;
        mGenTextView.setTypeface(chooseTextStyle(mGenStyle));
    }

    public int getCommaStyle() {
        return mCommaStyle;
    }

    public void setCommaStyle(int commaStyle) {
        mCommaStyle = commaStyle;
        mCommaTextView.setTypeface(chooseTextStyle(mCommaStyle));
    }


    public String getComma() {
        return mComma;
    }

    public void setComma(String comma) {
        mComma = comma;
        mCommaTextView.setText(mComma);
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
        mTextTextView.setText(mText);
    }

    public String getGen() {
        return mGen;
    }

    public void setGen(String gen) {
        mGen = gen;
        mGenTextView.setText(mGen);
    }

    public TextView getTextTextView() {
        return mTextTextView;
    }

    public void setTextTextView(TextView textTextView) {
        mTextTextView = textTextView;
    }

    public TextView getGenTextView() {
        return mGenTextView;
    }

    public void setGenTextView(TextView genTextView) {
        mGenTextView = genTextView;
    }
}

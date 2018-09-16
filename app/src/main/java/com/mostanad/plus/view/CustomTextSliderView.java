package com.mostanad.plus.view;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.slider.library.SliderTypes.BaseSliderView;

import com.mostanad.plus.R;


public class CustomTextSliderView  extends BaseSliderView {
    private static Typeface font = null;
    private Context context ;
    public CustomTextSliderView(Context context) {
        super(context);
        this.context = context;
    }
    @Override
    public View getView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.render_type_text,null);
        ImageView target = v.findViewById(R.id.daimajia_slider_image);
        TextView description = v.findViewById(R.id.description);
        description.setText(getDescription());
        if(font == null){
            font = Typeface.createFromAsset(context.getAssets(), "fonts/shabnam.ttf");
        }
        description.setTypeface(font);
        description.setTextSize(10f);
        description.setPadding(0,0,10,0);
        bindEventAndShow(v, target);
        return v;
    }
}
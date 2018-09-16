package com.mostanad.plus.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mostanad.plus.R;

public class DownloadedVideoAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] name;
    private final Bitmap[] imageId;

    public DownloadedVideoAdapter(Activity context, String[] name, Bitmap[] imageId) {
        super(context, R.layout.row_download, name);
        this.context = context;
        this.name = name;
        this.imageId = imageId;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.row_download, null, true);
        TextView txtTitle = rowView.findViewById(R.id.row_download_name);

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/shabnam.ttf");
        ImageView imageView = rowView.findViewById(R.id.row_download_img);
        txtTitle.setText(name[position]);
        txtTitle.setTypeface(typeface);

        imageView.setImageBitmap(imageId[position]);
        return rowView;
    }
}

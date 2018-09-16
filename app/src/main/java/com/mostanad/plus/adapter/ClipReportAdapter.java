package com.mostanad.plus.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mostanad.plus.R;


public class ClipReportAdapter extends ArrayAdapter<String> {

    private final String[] list;
    private final Activity context;

    static class ViewHolder {
        protected TextView name;
    }

    public ClipReportAdapter(Activity context, String[] list) {
        super(context, R.layout.row_list, list);
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.row_list, null);
            final ViewHolder viewHolder = new ViewHolder();

           Typeface typeface= Typeface.createFromAsset(getContext().getAssets(),"fonts/shabnam.ttf");

            viewHolder.name = view.findViewById(R.id.txtreport);
            viewHolder.name.setTypeface(typeface);

            view.setTag(viewHolder);
        } else {
            view = convertView;
        }

        ViewHolder holder = (ViewHolder) view.getTag();
        holder.name.setText(list[position]);
        return view;
    }
}
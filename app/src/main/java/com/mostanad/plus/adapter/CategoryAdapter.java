package com.mostanad.plus.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.mostanad.plus.R;
import com.mostanad.plus.activity.CategoryActivity;
import com.mostanad.plus.pojo.SingleCategoryModel;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private ArrayList<SingleCategoryModel> category;
    private Context context;

    public CategoryAdapter(ArrayList<SingleCategoryModel> category, Context context) {
        this.category = category;
        this.context = context;
    }

    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_category, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryAdapter.ViewHolder viewHolder, final int i) {

        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        Picasso.with(context).load(category.get(i).getImage_file()).fit().placeholder(new ColorDrawable(color)).into(viewHolder.img);

        viewHolder.txtcat.setText(category.get(i).getName_fa());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, CategoryActivity.class);
                intent.putExtra("categoryId", category.get(i).getId());
                intent.putExtra("categoryName", category.get(i).getName_fa());

                context.startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {

        List<String> al = new ArrayList<>();
        for (int i = 0; i < category.size(); i++) {
            al.add(category.get(i).getId());
        }
        Set<String> hs = new HashSet<>();
        hs.addAll(al);
        al.clear();
        al.addAll(hs);
        return al.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtcat;
        private ImageView img;

        public ViewHolder(View view) {
            super(view);

            txtcat = view.findViewById(R.id.txtcategory);
            img = view.findViewById(R.id.imgcat);


        }
    }

}
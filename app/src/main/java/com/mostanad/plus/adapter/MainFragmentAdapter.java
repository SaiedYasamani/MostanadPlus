package com.mostanad.plus.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.mostanad.plus.R;
import com.mostanad.plus.activity.CategoryActivity;
import com.mostanad.plus.activity.VideoActivity;
import com.mostanad.plus.fragment.MainFragment;
import com.mostanad.plus.pojo.SingleVideoModel;
import com.mostanad.plus.view.CustomTextSliderView;

public class MainFragmentAdapter extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<SingleVideoModel> featured = new ArrayList<>();
    private LinkedList<SingleVideoModel> newest = new LinkedList<>();
    private ArrayMap<String, LinkedList<SingleVideoModel>> otherCategoriesClip = new ArrayMap<>();

    private GridLayoutManager layoutManager;

    private HomeCategoryVideosAdapter categoryVideosAdapter;

    public static class SliderViewHolder extends RecyclerView.ViewHolder {
        SliderLayout slider;
        PagerIndicator custom_indicator;

        public SliderViewHolder(View itemView) {
            super(itemView);
            this.slider = itemView.findViewById(R.id.slider);
            this.custom_indicator = itemView.findViewById(R.id.custom_indicator);
        }
    }

    public static class NewestViewHolder extends RecyclerView.ViewHolder {
        TextView txt_category_name, txt_more;
        RecyclerView rv_category_clips;

        public NewestViewHolder(View itemView) {
            super(itemView);
            this.rv_category_clips = itemView.findViewById(R.id.rv_category_clips);
            this.txt_category_name = itemView.findViewById(R.id.txt_category_name);
            this.txt_more = itemView.findViewById(R.id.txt_more);
        }
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView txt_more, txt_category_name;
        RecyclerView rv_category_clips;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            this.rv_category_clips = itemView.findViewById(R.id.rv_category_clips);
            this.txt_more = itemView.findViewById(R.id.txt_more);
            this.txt_category_name = itemView.findViewById(R.id.txt_category_name);
        }
    }

    public MainFragmentAdapter(Context context, ArrayMap<String, LinkedList<SingleVideoModel>> otherCategoriesClip) {
        this.context = context;
        if (otherCategoriesClip.containsKey("featured")) {
            featured.addAll(otherCategoriesClip.get("featured"));
            otherCategoriesClip.remove("featured");
        }

        if (otherCategoriesClip.containsKey("new")) {
            newest.addAll(otherCategoriesClip.get("new"));
            otherCategoriesClip.remove("new");
        }
        this.otherCategoriesClip = otherCategoriesClip;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_home_slider, parent, false);
                return new SliderViewHolder(view);

            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_home_category, parent, false);
                return new NewestViewHolder(view);

            case 2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_home_category, parent, false);
                return new CategoryViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case 0: {
                SliderViewHolder viewHolder = ((SliderViewHolder) holder);
                viewHolder.slider.setPresetTransformer(SliderLayout.Transformer.Default);
                viewHolder.slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                viewHolder.slider.setCustomAnimation(new DescriptionAnimation());
                viewHolder.slider.setDuration(5000);
                viewHolder.slider.setSliderTransformDuration(2000, null);
                viewHolder.slider.setCustomIndicator(viewHolder.custom_indicator);

                viewHolder.slider.removeAllSliders();

                HashMap<String, String> file_maps = new HashMap<>();
                for (int i = 0; i < featured.size(); i++) {
                    file_maps.put(featured.get(i).getTitle_fa(), featured.get(i).getImage());
                }
                for (final String name : file_maps.keySet()) {
                    CustomTextSliderView textSliderView = new CustomTextSliderView(context);

                    textSliderView
                            .description(name)
                            .image(file_maps.get(name))
                            .setScaleType(BaseSliderView.ScaleType.Fit)
                            .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {
                                    Intent intent = new Intent(context, VideoActivity.class);
                                    int cn = 0;
                                    for (int g = 0; g < featured.size(); g++)
                                        if (name.contentEquals(featured.get(g).getTitle_fa())) {
                                            cn = g;
                                            Log.e("Index Clicked", String.valueOf(cn));
                                        }

                                    intent.putExtra("name", featured.get(cn).getTitle_fa());
                                    intent.putExtra("description", featured.get(cn).getDescription());
                                    intent.putExtra("like", featured.get(cn).getLike());
                                    intent.putExtra("visit", featured.get(cn).getVisit());
                                    intent.putExtra("id", featured.get(cn).getId());
                                    intent.putExtra("file_url", featured.get(cn).getFile_url());
                                    context.startActivity(intent);
                                }
                            });
                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle()
                            .putString("extra", name);

                    viewHolder.slider.addSlider(textSliderView);
                }
                break;
            }
            case 1: {
                NewestViewHolder viewHolder = ((NewestViewHolder) holder);
                viewHolder.txt_category_name.setText("تازه ها");
                viewHolder.rv_category_clips.setHasFixedSize(true);
                viewHolder.rv_category_clips.setItemViewCacheSize(20);
                viewHolder.rv_category_clips.setDrawingCacheEnabled(true);
                viewHolder.rv_category_clips.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                layoutManager = new GridLayoutManager(context, 2);
                viewHolder.rv_category_clips.setLayoutManager(layoutManager);
                categoryVideosAdapter = new HomeCategoryVideosAdapter(newest, context);
                viewHolder.rv_category_clips.setAdapter(categoryVideosAdapter);
                viewHolder.txt_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (context!=null)
                        ((MainFragment.OnFragmentInteractionListener) context).goToPage(2);
                    }
                });
                break;
            }
            case 2: {
                CategoryViewHolder viewHolder = ((CategoryViewHolder) holder);
                int newPosition = position;
                if (getItemCount() > otherCategoriesClip.size())
                    newPosition = newPosition - (getItemCount() - otherCategoriesClip.size());
                if (otherCategoriesClip.keyAt(newPosition) != null) {
                    viewHolder.txt_category_name.setText(otherCategoriesClip.keyAt(newPosition));
                    viewHolder.rv_category_clips.setHasFixedSize(true);
                    viewHolder.rv_category_clips.setItemViewCacheSize(20);
                    viewHolder.rv_category_clips.setDrawingCacheEnabled(true);
                    viewHolder.rv_category_clips.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                    layoutManager = new GridLayoutManager(context, 2);
                    viewHolder.rv_category_clips.setLayoutManager(layoutManager);
                    categoryVideosAdapter = new HomeCategoryVideosAdapter(otherCategoriesClip.get(otherCategoriesClip.keyAt(newPosition)), context);
                    viewHolder.rv_category_clips.setAdapter(categoryVideosAdapter);
                    final int finalNewPosition = newPosition;
                    viewHolder.txt_more.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, CategoryActivity.class);
                            intent.putExtra("categoryId", otherCategoriesClip.get(otherCategoriesClip.keyAt(finalNewPosition)).getFirst().getCategory());
                            intent.putExtra("categoryName", otherCategoriesClip.keyAt(finalNewPosition));
                            context.startActivity(intent);
                        }
                    });
                } else {
                    viewHolder.itemView.setVisibility(View.GONE);
                }
                break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        int type = 2;

        switch (position) {
            case 0:
                if (!featured.isEmpty()) type = 0;
                else {
                    if (!newest.isEmpty()) type = 1;
                    else type = 2;
                }
                break;
            case 1:
                if (!featured.isEmpty()) {
                    if (!newest.isEmpty()) type = 1;
                    else type = 2;
                } else {
                    type = 2;
                }
                break;
            case 2:
                if (!otherCategoriesClip.isEmpty())
                    type = 2;
                break;
        }

        return type;

    }

    @Override
    public int getItemCount() {
        int featuredSize = 0;
        if (!featured.isEmpty()) featuredSize = 1;
        int newestSize = 0;
        if (!newest.isEmpty()) newestSize = 1;
        int otherCategoriesSize = 0;
        if (!otherCategoriesClip.isEmpty()) otherCategoriesSize = otherCategoriesClip.size();

        int size = featuredSize + newestSize + otherCategoriesSize;

        return size;
    }


}

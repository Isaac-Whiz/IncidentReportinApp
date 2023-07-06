package com.example.incidentreportingapp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

public class ViewPageAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<Uri> uriList;
    private LayoutInflater inflater;

    public ViewPageAdapter(Context context, ArrayList<Uri> imageUrls){
        this.context = context;
        uriList = imageUrls;
    }
    @Override
    public int getCount() {
        return uriList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = inflater.inflate(R.layout.image_layout, container, false);
        ImageView imageView = view.findViewById(R.id.uploadImage);
        imageView.setImageURI(uriList.get(position));
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull View container, int position, @NonNull Object object) {
        ((RelativeLayout)object).removeView(container);
    }
}

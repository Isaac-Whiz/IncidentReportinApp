package com.example.incidentreportingapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImagePagerAdapter extends PagerAdapter {
    private Context context;
    public static ArrayList<String> imagePaths;

    public ImagePagerAdapter(Context context, ArrayList<String> imagePaths) {
        this.context = context;
        this.imagePaths = imagePaths;
    }

    public ImagePagerAdapter() {
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_image, container, false);

        ImageView imageView = view.findViewById(R.id.imageView);
        String imagePath = imagePaths.get(position);
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        imageView.setImageBitmap(bitmap);

        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return imagePaths.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    public  View getView(int position) {
        return (View) instantiateItem(null, position);
    }
}
package com.example.photosandroid16.models;

import android.content.Context;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.photosandroid16.MainActivity;
import com.example.photosandroid16.R;
import com.example.photosandroid16.models.Photo;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Photo> photos;

    // Constructor
    public ImageAdapter(Context c, ArrayList<Photo> photos) {
        mContext = c;
        this.photos = photos;
    }

    public int getCount() {
        return photos.size();
    }

    public Photo getItem(int position) {
        return photos.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public void updateList(ArrayList<Photo> photos, ArrayList<Photo> otherList) {
        photos.clear();
        photos.addAll(otherList);
        this.notifyDataSetChanged();

    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(400, 400));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        }
        else
        {
            imageView = (ImageView) convertView;
        }
        Uri image = Uri.parse(photos.get(position).getPathName());
        System.out.println(image.toString());
        imageView.setImageURI(image);

        return imageView;
    }
}
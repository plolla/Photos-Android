package com.example.photosandroid16;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.photosandroid16.models.Album;
import com.example.photosandroid16.models.AlbumList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SlideShow extends AppCompatActivity {
    Button previous;
    Button next;
    ImageView imageView;
    ListView tagList;

    AlbumList albumList;

    int index;
    ArrayList<String> loadedTagStrings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slide_show);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        try {
            albumList = new AlbumList();
        } catch (IOException e) {
            e.printStackTrace();
        }

        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);

        index = 0;
        Album album = albumList.getMap().get(MainActivity.selectedAlbumName);
        imageView = findViewById(R.id.imageView2);
        Uri image = Uri.parse(album.getPhotos().get(0).getPathName());
        System.out.println("start: " + index);
        imageView.setImageURI(image);

        tagList = findViewById(R.id.listView);
        loadedTagStrings = new ArrayList<>(album.getPhotos().get(0).getTagStrings());
        sort(loadedTagStrings);
        ArrayAdapter<String> tagAdapter = new ArrayAdapter<>(this, R.layout.tag_text, loadedTagStrings);
        tagList.setAdapter(tagAdapter);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index < album.getPhotos().size()-1) {
                    Uri image = Uri.parse(album.getPhotos().get(index + 1).getPathName());
                    imageView.setImageURI(image);
                    updateList(loadedTagStrings, album.getPhotos().get(index+1).getTagStrings());
                    tagAdapter.notifyDataSetChanged();
                    tagList.setAdapter(tagAdapter);
                    System.out.println("next " + (index + 1));
                    index++;

                }
                else {
                    index=0;
                    Uri image = Uri.parse(album.getPhotos().get(index).getPathName());
                    imageView.setImageURI(image);
                    updateList(loadedTagStrings, album.getPhotos().get(index).getTagStrings());
                    tagAdapter.notifyDataSetChanged();
                    System.out.println("next " + index);
                }

            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (0 < index) {
                    Uri image = Uri.parse(album.getPhotos().get(index-1).getPathName());
                    imageView.setImageURI(image);
                    updateList(loadedTagStrings, album.getPhotos().get(index-1).getTagStrings());
                    tagAdapter.notifyDataSetChanged();
                    System.out.println("previous " + (index-1));
                    index--;
                }
                else {
                    index = album.getPhotos().size()-1;
                    Uri image = Uri.parse(album.getPhotos().get(index).getPathName());
                    imageView.setImageURI(image);
                    updateList(loadedTagStrings, album.getPhotos().get(index).getTagStrings());
                    tagAdapter.notifyDataSetChanged();
                    System.out.println("previous " + index);
                }

            }
        });

    }
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent = new Intent(this, AlbumView.class);
        startActivity(intent);
        return true;
    }
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, AlbumView.class);
        startActivity(intent);
    }
    private void sort(ArrayList<String> albums){
        Collections.sort(albums, new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                return s.toLowerCase().compareTo(t1.toLowerCase());
            }
        });
    }
    private void updateList(ArrayList<String> list1, ArrayList<String> list2) {
        list1.clear();
        list1.addAll(list2);
    }


}
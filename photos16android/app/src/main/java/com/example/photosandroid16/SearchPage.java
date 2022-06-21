package com.example.photosandroid16;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.photosandroid16.models.Album;
import com.example.photosandroid16.models.AlbumList;
import com.example.photosandroid16.models.ImageAdapter;
import com.example.photosandroid16.models.Photo;
import com.example.photosandroid16.models.Tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class SearchPage extends AppCompatActivity {


    EditText value;
    Button searchButton;
    GridView gridView;
    AlbumList albumList;

    private ImageAdapter imageAdapter;

    boolean foundDuplicate = false;

    public static ArrayList<Photo> searchResults;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchResults = new ArrayList<>();

        searchButton = findViewById(R.id.searchButton);
        value = findViewById(R.id.value);
        gridView = findViewById(R.id.gridView2);
        try {
            albumList = new AlbumList();
        } catch (IOException e) {
            e.printStackTrace();
        }

        searchButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                if(value.getText().toString().trim().length() == 0){
                    Toast toast = Toast.makeText(getApplicationContext(), "Please enter all fields to search.", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                searchResults.clear();
                String valueString = value.getText().toString().trim();
                getSearchResults(valueString);
                imageAdapter = new ImageAdapter(SearchPage.this, searchResults);
                gridView.setAdapter(imageAdapter);
                System.out.println(searchResults);
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                if(searchResults.size() == 0){
                    Toast toast = Toast.makeText(getApplicationContext(), "No results. Please make sure key is either person or location.", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });


    }
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        return true;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getSearchResults(String v){
        if (searchResults.size() != 0) {
            searchResults.clear();
        }
        System.out.println(v);
        HashSet<Photo> photos = new HashSet<>();
        String[] tagsAnd = v.split("AND");
        if(tagsAnd.length!=1){
            String[] tag1 = tagsAnd[0].split("=");
            String[] tag2 = tagsAnd[1].split("=");
            //Map ArrayList tag to Photo List
            Tag t1 = new Tag(tag1[0].toLowerCase(), tag1[1].toLowerCase());
            Tag t2 = new Tag(tag2[0].toLowerCase(), tag2[1].toLowerCase());
            ArrayList<Photo> contenders = new ArrayList<>();
            for(Album album : albumList.getMap().values()){
                for(Photo photo : album.getPhotos()) {
                    if( photo.checkIfTagIsInList(t1.getKey(), t1.getValue()) && photo.checkIfTagIsInList(t2.getKey(), t2.getValue() )){
                        photos.add(photo);
                    }
                }
            }
            searchResults = new ArrayList<>(photos);
            return;
        }
        String[] tagsOr = v.split("OR");
        if(tagsOr.length!=1){
            String[] tag1 = tagsOr[0].split("=");
            String[] tag2 = tagsOr[1].split("=");
            //Map ArrayList tag to Photo List
            Tag t1 = new Tag(tag1[0].toLowerCase(), tag1[1].toLowerCase());
            Tag t2 = new Tag(tag2[0].toLowerCase(), tag2[1].toLowerCase());
            ArrayList<Photo> contenders = new ArrayList<>();
            for(Album album : albumList.getMap().values()){
                for(Photo photo : album.getPhotos()) {
                    if( photo.checkIfTagIsInList(t1.getKey(), t1.getValue()) || photo.checkIfTagIsInList(t2.getKey(), t2.getValue() )){
                        photos.add(photo);
                    }
                }
            }
            searchResults = new ArrayList<>(photos);
            return;
        }
        String[] tag1 = v.split("=");
        Tag t1 = new Tag(tag1[0], tag1[1]);
        for(Album album : albumList.getMap().values()){
            for(Photo photo : album.getPhotos()){
                if(photo.checkIfTagIsInList(t1.getKey(), t1.getValue())){
                    photos.add(photo);
                }
            }
        }
        searchResults = new ArrayList<>(photos);
    }
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

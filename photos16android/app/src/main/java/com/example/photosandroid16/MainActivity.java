package com.example.photosandroid16;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.photosandroid16.models.Album;
import com.example.photosandroid16.models.AlbumList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private AlbumList albumList;
    private Button button;
    private EditText input;

    public static String selectedAlbumName = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        File file = new File("/data/user/0/com.example.photosandroid16/files/data.dat");
        if(!file.getParentFile().exists()){
            try {
                file.getParentFile().mkdir();
                if(!file.exists()){
                    file.createNewFile();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            albumList = new AlbumList();
        } catch (IOException e) {
            e.printStackTrace();
        }
        HashMap<String, Album> loadedAlbumMap = albumList.getMap();
        listView = findViewById(R.id.albumsList);
        input = findViewById(R.id.newAlbumText);

        ArrayList<String> loadedAlbumStrings = new ArrayList<>(loadedAlbumMap.keySet());
        sort(loadedAlbumStrings);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.album_text_home, loadedAlbumStrings);
        listView.setAdapter(adapter);

        button = findViewById(R.id.submitAlbum);
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(input.getText().toString().trim().length() == 0){
                    Toast toast = Toast.makeText(getApplicationContext(), "Please enter an album name", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else{
                    String albumName = input.getText().toString().trim();
                    if(loadedAlbumMap.containsKey(albumName)){
                        Toast toast = Toast.makeText(getApplicationContext(), "This album already exists", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    else {
                        Album newAlbum = new Album(albumName);
                        try {
                            albumList.addAlbum(newAlbum);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        loadedAlbumStrings.add(albumName);
                        sort(loadedAlbumStrings);
                        adapter.notifyDataSetChanged();
                    }
                    input.getText().clear();
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = ((TextView) view).getText().toString();
                System.out.println(selected);
                selectedAlbumName = selected;
                albumView();

            }
        });
        Button search = findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(loadedAlbumStrings.size() == 0){
                    Toast toast = Toast.makeText(getApplicationContext(), "Cannot search for photos without albums", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                else{
                    Intent intent = new Intent(v.getContext(), SearchPage.class);
                    startActivity(intent);
                }
            }
        });

    }
    private void sort(ArrayList<String> albums){
        Collections.sort(albums, new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                return s.toLowerCase().compareTo(t1.toLowerCase());
            }
        });
    }
    private void albumView() {
        Intent intent = new Intent(this, AlbumView.class);
        startActivity(intent);
    }
    @Override
    public void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }
}
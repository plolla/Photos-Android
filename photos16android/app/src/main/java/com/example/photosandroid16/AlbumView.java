package com.example.photosandroid16;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.photosandroid16.models.Album;
import com.example.photosandroid16.models.AlbumList;
import com.example.photosandroid16.models.ImageAdapter;
import com.example.photosandroid16.models.Photo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class AlbumView extends AppCompatActivity {
    TextView albumName;
    Button rename;
    Button delete;
    Button add;
    Button slideShow;
    GridView gridView;

    private ImageAdapter imageAdapter;
    AlbumList albumList;
    public static Photo photoClicked = null;
    ArrayList<Photo> photoList;
    boolean renameClicked = false;
    boolean deleteClicked = false;

    public String userInputForRename = "";

    public static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        try {
             albumList = new AlbumList();
        } catch (IOException e) {
            e.printStackTrace();
        }


        albumName = findViewById(R.id.albumName);
        rename = findViewById(R.id.rename);
        delete = findViewById(R.id.delete);
        add = findViewById(R.id.addPhoto);
        slideShow = findViewById(R.id.slideShow);
        gridView = findViewById(R.id.gridView);


        albumName.setText(MainActivity.selectedAlbumName);
        Album album = albumList.getMap().get(MainActivity.selectedAlbumName);

        photoList = albumList.getMap().get(MainActivity.selectedAlbumName).getPhotos();
        imageAdapter = new ImageAdapter(this, photoList);
        gridView.setAdapter(imageAdapter);

        slideShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photoList.size() == 0) {
                    Toast.makeText(AlbumView.this, "No photos in this album", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(AlbumView.this, SlideShow.class);
                startActivity(intent);
            }
        });

        rename.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                renameClicked = true;
                popUpEditText();
                renameClicked = false;
            }
        });

        delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                deleteClicked = true;
                popUpEditText();
                deleteClicked = false;
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                photoClicked = photoList.get(position);
                Intent intent = new Intent(AlbumView.this, PhotoView.class);
                startActivity(intent);

            }
        });

    }
    private void popUpEditText() {
        if (renameClicked) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Rename album");

            final EditText input = new EditText(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // do something here on OK
                    userInputForRename = input.getText().toString();
                    if (albumList.getMap().get(userInputForRename) != null) {
                        Toast.makeText(AlbumView.this, "There is another album with the same name", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Album reference = albumList.getMap().get(MainActivity.selectedAlbumName);
                        albumList.getMap().remove(MainActivity.selectedAlbumName);
                        MainActivity.selectedAlbumName = userInputForRename;
                        reference.setAlbumName(userInputForRename);
                        albumList.getMap().put(userInputForRename, reference);
                        try {
                            albumList.writeApp();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        finish();
                        startActivity(getIntent());
                    }

                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete album");
            builder.setMessage("Are you sure you want to delete this album?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        albumList.deleteAlbum(MainActivity.selectedAlbumName);
                        albumList.writeApp();
                        System.out.println("album deleted");
                        Intent intent = new Intent(AlbumView.this, MainActivity.class);
                        startActivity(intent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null){
            Uri selectedImage = data.getData();
            for (Photo photo : photoList) {
                if (selectedImage.toString().equals(photo.getPathName())) {
                    Toast.makeText(this, "You already have this photo", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            Photo newPhoto = new Photo(selectedImage.toString());
            albumList.getMap().get(MainActivity.selectedAlbumName).getPhotos().add(newPhoto);
            try {
                albumList.writeApp();
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageAdapter.updateList(photoList,albumList.getMap().get(MainActivity.selectedAlbumName).getPhotos());
            imageAdapter.notifyDataSetChanged();
            gridView.setAdapter(imageAdapter);
            System.out.println("photo added");
            System.out.println(newPhoto.getPathName());
            System.out.println("photos arraylist: " + photoList);
            System.out.println("album arraylist: " + albumList.getMap().get(MainActivity.selectedAlbumName).getPhotos());
        }
    }
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        return true;
    }
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public void onRestart() {
        super.onRestart();
        finish();
        try {
            albumList = new AlbumList();
        } catch (IOException e) {
            e.printStackTrace();
        }
        startActivity(getIntent());
    }
}
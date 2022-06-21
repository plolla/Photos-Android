package com.example.photosandroid16;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.photosandroid16.models.Album;
import com.example.photosandroid16.models.AlbumList;
import com.example.photosandroid16.models.Tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PhotoView extends AppCompatActivity {
    ImageView imageView;
    Button deletePhoto;
    Button addTag;
    Button deleteTag;
    Button movePhoto;
    ListView tagList;


    AlbumList albumList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        try {
            albumList = new AlbumList();
        } catch (IOException e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }

        tagList = findViewById(R.id.listView);
        ArrayList<String> loadedTagStrings = new ArrayList<>(albumList.getMap().get(MainActivity.selectedAlbumName).getPhoto(AlbumView.photoClicked).getTagStrings());
        sort(loadedTagStrings);
        ArrayAdapter<String> tagAdapter = new ArrayAdapter<>(this, R.layout.tag_text, loadedTagStrings);
        tagList.setAdapter(tagAdapter);

        deletePhoto = findViewById(R.id.deletePhoto);
        deletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PhotoView.this);
                builder.setTitle("Delete album");
                builder.setMessage("Are you sure you want to delete this photo?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            albumList.getMap().get(MainActivity.selectedAlbumName).removePhoto(AlbumView.photoClicked.getPathName());
                            albumList.writeApp();
                            Intent intent = new Intent(PhotoView.this, AlbumView.class);
                            startActivity(intent);
                            Toast.makeText(PhotoView.this, "Photo deleted", Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            System.out.println(e.toString());
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
        });

        Album album = albumList.getMap().get(MainActivity.selectedAlbumName);
        imageView = findViewById(R.id.imageView);
        Uri image = Uri.parse(AlbumView.photoClicked.getPathName());
        System.out.println("in PhotoView " + image.toString());
        imageView.setImageURI(image);


        addTag = findViewById(R.id.addTag);
        addTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(AlbumView.photoClicked.getTags());
                AlertDialog.Builder alert = new AlertDialog.Builder(PhotoView.this);
                alert.setTitle("Add Tag");LinearLayout layout = new LinearLayout(PhotoView.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                //Set up radio buttons
                RadioButton[] rb = new RadioButton[2];
                RadioGroup rg = new RadioGroup(PhotoView.this);
                rg.setOrientation(RadioGroup.HORIZONTAL);
                rb[0] = new RadioButton(PhotoView.this);
                rb[1] = new RadioButton(PhotoView.this);
                rb[0].setText("Person");
                rb[1].setText("Location");
                rg.addView(rb[0]);
                rg.addView(rb[1]);

                //Set up values
                EditText value = new EditText(PhotoView.this);
                value.setHint("Value");

                //Build Linear Layout
                layout.addView(rg);
                layout.addView(value);
                alert.setView(layout);
                alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(rb[0].isChecked() == rb[1].isChecked() || value.getText().toString().trim().length() == 0){
                            Toast.makeText(PhotoView.this, "Please enter all fields for tag", Toast.LENGTH_LONG).show();
                            return;
                        }
                        else{
                            String key = rb[0].isChecked() == true ? "person" : "location";
                            String val = value.getText().toString().toLowerCase().trim();
                            if(!albumList.getMap().get(MainActivity.selectedAlbumName).getPhoto(AlbumView.photoClicked).checkIfTagIsInList(key, val)){
                                albumList.getMap().get(MainActivity.selectedAlbumName).getPhoto(AlbumView.photoClicked).addTag(key, val);
                                loadedTagStrings.add(key + " : " + val);
                                sort(loadedTagStrings);
                                Toast.makeText(PhotoView.this, "Tag added", Toast.LENGTH_LONG).show();
                                tagAdapter.notifyDataSetChanged();
                                try {
                                    albumList.writeApp();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            else{
                                Toast.makeText(PhotoView.this, "This tag already exists", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                });
                alert.show();
            }
        });
        deleteTag = findViewById(R.id.deleteTag);
        deleteTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PhotoView.this);
                if (albumList.getMap().get(MainActivity.selectedAlbumName).getPhoto(AlbumView.photoClicked).getTags().size() > 0) {
                    ArrayList<Tag> allTags = albumList.getMap().get(MainActivity.selectedAlbumName).getPhoto(AlbumView.photoClicked).getTags();
                    builder.setTitle("Delete tag");
                    builder.setMessage("Which tag would you like to delete");
                    ListView lv = new ListView(PhotoView.this);
                    LinearLayout layout = new LinearLayout(PhotoView.this);
                    ArrayAdapter<Tag> dialogAdapter = new ArrayAdapter<>(PhotoView.this, R.layout.album_text_home, allTags);
                    lv.setAdapter(dialogAdapter);
                    layout.addView(lv);
                    builder.setView(layout);
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            String selected = ((TextView) view).getText().toString();
                            String[] split = selected.split(":");
                            String k = split[0].trim();
                            String v= split[1].trim();
                            AlertDialog.Builder alert = new AlertDialog.Builder(PhotoView.this);
                            alert.setTitle("Are you sure you want to delete this tag?");
                            alert.setMessage("Tag: " + k + " : " + v);
                            System.out.println("hello");
                            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    layout.setOrientation(LinearLayout.VERTICAL);
                                    albumList.getMap().get(MainActivity.selectedAlbumName).getPhoto(AlbumView.photoClicked).deleteTag(k, v);
                                    loadedTagStrings.remove(k + " : " + v);
                                    tagAdapter.notifyDataSetChanged();
                                    Toast.makeText(PhotoView.this, "Tag deleted", Toast.LENGTH_LONG).show();
                                    try {
                                        albumList.writeApp();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    dialogAdapter.notifyDataSetChanged();
                                }
                            });
                            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            });
                            alert.show();

                        }

                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            return;
                        }
                    });

                    builder.show();
                }
                else {
                    Toast.makeText(PhotoView.this, "This photo doesn't have any tags", Toast.LENGTH_LONG).show();
                }

            }
        });
        movePhoto = findViewById(R.id.movePhoto);
        movePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PhotoView.this);
                ArrayList<String> allAlbums = new ArrayList<>(albumList.getMap().keySet());
                builder.setTitle("Move Photo");
                builder.setMessage("Where would you like to move the photo to?");
                ListView lv = new ListView(PhotoView.this);
                LinearLayout layout = new LinearLayout(PhotoView.this);
                ArrayAdapter<String> dialogAdapter = new ArrayAdapter<>(PhotoView.this, R.layout.album_text_home, allAlbums);
                lv.setAdapter(dialogAdapter);
                layout.addView(lv);
                builder.setView(layout);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String selected = ((TextView) view).getText().toString();
                        if (!albumList.getMap().get(selected).checkIfPhotoIsInAlbum(AlbumView.photoClicked.getPathName())) {
                            System.out.println("it works");
                            albumList.getMap().get(selected).addPhoto(albumList.getMap().get(MainActivity.selectedAlbumName).getPhoto(AlbumView.photoClicked));
                            albumList.getMap().get(MainActivity.selectedAlbumName).removePhoto(AlbumView.photoClicked.getPathName());
                            Toast.makeText(PhotoView.this, "Photo moved to " + selected, Toast.LENGTH_LONG).show();
                            try {
                                albumList.writeApp();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            dialogAdapter.notifyDataSetChanged();
                        }
                        else {
                            System.out.println("doesn't work");
                            Toast.makeText(PhotoView.this, "This photo already exists in " + selected, Toast.LENGTH_LONG).show();
                        }
                        return;

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                });
                builder.show();

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
}
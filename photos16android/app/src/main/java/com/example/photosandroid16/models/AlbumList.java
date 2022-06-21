package com.example.photosandroid16.models;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class AlbumList implements Serializable {
    HashMap<String, Album> allAlbums = new HashMap<>();
    static final long serialVersionUID = 1L;

    public static final String fileLocation = "/data/user/0/com.example.photosandroid16/files/data.dat";

    public AlbumList() throws IOException {
        readApp(); 
    }

    public void readApp() throws IOException {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileLocation));
            allAlbums = (HashMap<String, Album>) ois.readObject();
        } catch (Exception e){
            allAlbums = new HashMap<>();
        }
    }
    public void writeApp() throws IOException {
        FileOutputStream fos = null;
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileLocation));
        oos.writeObject(allAlbums);
        readApp();
    }

    public void addAlbum(Album album) throws IOException {
        readApp();
        allAlbums.put(album.getAlbumName(), album);
        writeApp();
    }
    public void deleteAlbum(String albumName) throws IOException {
        readApp();
        allAlbums.remove(albumName);
        writeApp();
    }
    public HashMap<String, Album> getMap(){
        return allAlbums;
    }
    public ArrayList<Album> getAlbumList(){
        return new ArrayList<Album>(allAlbums.values());
    }
    public String toString(){
        String answer = "";
        for(String album : allAlbums.keySet()){
            answer+=album + " ";
        }
        return answer;
    }
}

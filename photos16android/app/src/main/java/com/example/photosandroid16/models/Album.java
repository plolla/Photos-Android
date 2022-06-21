package com.example.photosandroid16.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Album implements Serializable {
    ArrayList<Photo> photos;
    private int photoCount = 0;
    String albumName;

    public Album(String albumName){
        this.albumName = albumName;
        photos = new ArrayList<>();
    }
    public void setAlbumName(String albumName){
        this.albumName = albumName;
    }
    public String getAlbumName(){
        return albumName;
    }
    public ArrayList<Photo> getPhotos(){
        return photos;
    }
    public void addPhoto(Photo photo){
        photos.add(photo);
    }
    public Photo findPhotoByPath(String path){
        for(Photo photo: photos){
            if(photo.getPathName().equals(path)){
                return photo;
            }
        }
        return null;
    }
    public void removePhoto(String path){
        for(Photo photo: photos){
            if(photo.getPathName().equals(path)){
                photos.remove(photo);
                return;
            }
        }
    }
    public boolean checkIfPhotoIsInAlbum(String path) {
        for(Photo photo: photos){
            if(photo.getPathName().equals(path)){
                return true;
            }
        }
        return false;

    }
    public String toString(){
        return photos.toString();
    }
    public Photo getPhoto(Photo photo){
        for(Photo p:photos){
            if(p.getPathName().equals(photo.getPathName())) return p;
        }
        return null;
    }
}

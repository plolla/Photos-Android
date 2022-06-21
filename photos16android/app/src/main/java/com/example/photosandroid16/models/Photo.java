package com.example.photosandroid16.models;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;

public class Photo implements Serializable {
    private String pathName;
    private String photoName;
    private String caption = "";
    private ArrayList<Tag> tags;
    private Calendar date;
    //Tag Array List must be added

    public Photo(String pathName){
        this.pathName = pathName;
        this.photoName = pathName;
        this.caption = "";
        tags = new ArrayList<>();
        //Handle calendar stuff here as well
    }

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
    public String toString(){
        return pathName;
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }
    public void deleteTag(String keyName, String valueName) {
        for (int i = 0; i < tags.size(); i++) {
            if (tags.get(i).getKey().equals(keyName) && tags.get(i).getValue().equals(valueName)) {
                tags.remove(i);
                return;
            }
        }
    }
    public boolean checkIfTagIsInList(String keyName, String valueName) {
        if (tags.size() == 0) {
            return false;
        }
        for (Tag tag: tags) {
            if (tag.getKey().trim().toLowerCase().startsWith(keyName.trim()) && tag.getValue().trim().toLowerCase().startsWith(valueName.trim())) {
                return true;
            }
        }
        return false;
    }
    public void addTag(String key, String value){
        Tag tag = new Tag(key, value);
        tags.add(tag);
    }
    public ArrayList<String> getTagStrings() {
        ArrayList<String> stringList = new ArrayList<>();
        for (Tag tag: tags) {
            stringList.add(tag.toString());
        }
        return stringList;
    }
    public boolean doesTagExist(Tag tag){
        for(Tag t : this.getTags()){
            if(t.getKey().startsWith(tag.getKey()) && t.getValue().startsWith(tag.getValue())){
                return true;
            }
        }
        return false;
    }
}

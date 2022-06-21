package com.example.photosandroid16.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Tag implements Serializable {
    private String key;
    private String value;
    public Tag(String key, String value){
        this.key = key;
        this.value = value;
    }
    public String toString(){
         return (key+" : " + value);
    }
    public String getKey(){
        return key;
    }
    public String getValue(){
        return value;
    }
}

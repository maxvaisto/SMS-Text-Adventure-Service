package com.example.getsmsmessages;

import androidx.room.TypeConverter;

import com.google.gson.Gson;

import java.util.ArrayList;

/*
    This class is used to turn the JSson String into a list and the other way around
 */
public class Converters {
    @TypeConverter
    public static ArrayList<String> fromString(String value) {
        Gson gson = new Gson();
        ArrayList<String> list = new ArrayList<>();
        list = gson.fromJson(value, list.getClass());
        return list;
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<String> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}
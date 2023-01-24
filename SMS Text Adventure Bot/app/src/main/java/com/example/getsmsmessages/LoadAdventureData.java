package com.example.getsmsmessages;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;


public class LoadAdventureData {

    //Loads the actual text adventure text contained within the assets directory

    private AssetManager mAssetManager;
    private String TAG = "EMPTY_TAG";

    private Context mContext;
    public LoadAdventureData(Context context){
        mAssetManager = context.getAssets();
    }

    //This is used for the menu to list out each and every title
    public ArrayList<String> GetStoryTitles(Context context) {
        TAG = "loadTitles";
        mAssetManager = context.getAssets();
        mContext = context;
        ArrayList<String> gameTitles = new ArrayList<>();
        try {
            String[] fileList = mAssetManager.list("Text Adventures");
            for (String s : fileList) {
                String[] storyFileList = mAssetManager.list("Text Adventures/" + s);
                for (String value : storyFileList) {
                    String path = "Text Adventures/" + s + "/" + value;
                    if ("gameInfo.json".equals(value)) {
                        String infoJsonArray = loadJSONFromAsset(path);
                        try {
                            JSONArray jArray = new JSONArray(infoJsonArray);
                            String GInfo;
                            GInfo = jArray.get(1).toString();
                            gameTitles.add(GInfo);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG,"Error while forming storyTitles-Array");
                        }
                        Log.d("Load json", "gameTitles successfully loaded for story " + s);
                    }
                }
            }
            return gameTitles;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,"Error while parsing through story folders and gameInfo files");
        }

        return new ArrayList<>();
    }
    public ArrayList<String> GetStoryInfo(Context context, String story) {
        story = story.replaceAll("[^a-zA-Z0-9.\\-]", "_");
        TAG = "loadTitles";
        mAssetManager = context.getAssets();
        mContext = context;
        ArrayList<String>gameInfo = new ArrayList<>();
        try {
            String[] fileList = mAssetManager.list("Text Adventures");
            int indexOfStory = -1;
            for (int i = 0; i<fileList.length; i++) {
                if (fileList[i].equals(story)) {
                    indexOfStory = i;
                }
            }
            if (indexOfStory==-1){
                //Add error messages to other file and reference the variable names instead
                Log.e(TAG,"Story: " + story + " was not found");
                gameInfo.add("Error gameInfo not found");

                return gameInfo;
            } else {
                String[] storyFileList = mAssetManager.list("Text Adventures/"+fileList[indexOfStory]);
                for (String s : storyFileList) {
                    String path = "Text Adventures/" + fileList[indexOfStory] + "/" + s;

                    if ("gameInfo.json".equals(s)) {
                        String infoJsonArray = loadJSONFromAsset(path);
                        try {
                            JSONArray jArray = new JSONArray(infoJsonArray);
                            for (int d = 1; d < jArray.length(); d++) {
                                gameInfo.add(jArray.get(d).toString());
                            }
                            return gameInfo;

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "Error while parsing through gameInfo file for story" +
                                    fileList[indexOfStory]);
                        }
                        Log.e(TAG, "gameInfo successfully loaded for story " + fileList[indexOfStory]);
                    }
                    Log.e("filename " + fileList[indexOfStory], s);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        gameInfo.add("ERROR");
        return gameInfo;
    }
    public ArrayList<String> GetStoryGoTo(Context context, String story, String page) {
        story = story.replaceAll("[^a-zA-Z0-9.\\-]", "_");
        TAG = "LoadStoryGoTo";
        mAssetManager = context.getAssets();
        mContext = context;
        ArrayList<String> returnDefault = new ArrayList<>();
        try {
            String[] fileList = mAssetManager.list("Text Adventures");
            //CHANGE TO int i = 0
            int indexOfStory = -1;
            for (int i = 0; i<fileList.length; i++) {
                if (fileList[i].equals(story)) {
                    indexOfStory = i;
                }
            }
            if (indexOfStory==-1){
                //Add error messages to other file and reference the variable names instead
                Log.e(TAG,"Story: " + story + " was not found");
                returnDefault.add("Error story not found");
                return returnDefault;
            } else {
                String[] storyFileList = mAssetManager.list("Text Adventures/"+fileList[indexOfStory]);
                for (String s : storyFileList) {
                    String path = "Text Adventures/" + fileList[indexOfStory] + "/" + s;

                    Log.d(TAG, s);
                    if ("storyGoTo.json".equals(s)) {
                        String GoToJsonMap = loadJSONFromAsset(path);
                        try {
                            Gson gson = new Gson();
                            HashMap<String, ArrayList<String>> storyGoTo = new HashMap<String, ArrayList<String>>();
                            storyGoTo = gson.fromJson(GoToJsonMap, storyGoTo.getClass());
                            //Log.d("goTo",goTo.toString());
                            Log.d("goTo", storyGoTo.get("page 6").get(0));
                            Log.d("Load json", "storyGoTo successfully loaded for story " + fileList[indexOfStory]);
                            ArrayList<String> pageGoTo = storyGoTo.get(page);
                            if (!pageGoTo.isEmpty()) {
                                Log.d(TAG, "pageGoTo " + page +
                                        " found for story " + fileList[indexOfStory]);
                                return pageGoTo;

                            } else {
                                Log.e(TAG, "ERROR" + page +
                                        " not found for story" + fileList[indexOfStory]);

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    Log.e(TAG, "storyGoTo not found for  " + fileList[indexOfStory]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }
    public ArrayList<String> GetStoryChoices(Context context, String story, String page) {
        story = story.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
        TAG = "LoadStory";
        mAssetManager = context.getAssets();
        mContext = context;
        ArrayList<String> returnDefault = new ArrayList<String>();
        try {
            String[] fileList = mAssetManager.list("Text Adventures");
            //CHANGE TO int i = 0
            int indexOfStory = -1;
            for (int i = 0; i<fileList.length; i++) {
                if (fileList[i].toString().equals(story)) {
                    indexOfStory = i;
                }
            }
            if (indexOfStory==-1){
                //Add error messages to other file and reference the variable names instead
                Log.e(TAG,"Story: " + story + " was not found");
                returnDefault.add("Error story not found");
                return returnDefault;
            } else {
                String[] storyFileList = mAssetManager.list("Text Adventures/"+fileList[indexOfStory]);
                for (int k = 0; k<storyFileList.length; k++){
                    String path = "Text Adventures/" +  fileList[indexOfStory] + "/" + storyFileList[k];

                    Log.d(TAG, storyFileList[k]);
                    switch(storyFileList[k]){
                        case("storyChoices.json"):
                            String storyChoicesJSon = loadJSONFromAsset(path);
                            try {
                                Gson gson = new Gson();
                                HashMap<String,ArrayList<String>> storyChoices = new HashMap<String,ArrayList<String>>();
                                storyChoices = gson.fromJson(storyChoicesJSon,storyChoices.getClass());

                                //Log.d("storyChoices",storyChoices.toString());
                                Log.d("storyChoices",storyChoices.get("page 6").get(0));
                                Log.d("Load json", "storyChoices successfully loaded for story " + fileList[indexOfStory]);
                                ArrayList<String>  pageChoices = storyChoices.get(page);
                                if (!pageChoices.isEmpty()){
                                    Log.d(TAG,"pageChoices "+ page +
                                            " found for story " + fileList[indexOfStory]);
                                    return pageChoices;

                                } else { Log.e(TAG, "ERROR" + page +
                                        " not found for story" + fileList[indexOfStory]);

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                    }

                    Log.e(TAG, "storyChoices not found for  " + fileList[indexOfStory]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        returnDefault.add("Error story not found");
        return new ArrayList<>();
    }
    public String GetStoryText(Context context, String story, String page) {
        story = story.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
        TAG = "LoadStory";
        mAssetManager = context.getAssets();
        mContext = context;
        ArrayList<ArrayList<String>> gameInfo = new ArrayList<ArrayList<String>>();
        try {
            String[] fileList = mAssetManager.list("Text Adventures");
            //CHANGE TO int i = 0
            int indexOfStory = -1;
            for (int i = 0; i<fileList.length; i++) {
                if (fileList[i].toString().equals(story)) {
                    indexOfStory = i;
                    Log.e(TAG,"Story: " + story + " was not found");
                }
            }
            if (indexOfStory==-1){
                //Add error messages to other file and reference the variable names instead
                return "ERROR: STORY NOT FOUND!";
            } else {
                String[] storyFileList = mAssetManager.list("Text Adventures/"+fileList[indexOfStory]);
                for (int k = 0; k<storyFileList.length; k++){
                    String path = "Text Adventures/" +  fileList[indexOfStory] + "/" + storyFileList[k];

                    Log.d(TAG, storyFileList[k]);
                    switch(storyFileList[k]){
                        case("storyText.json"):
                            String storyTextJSon = loadJSONFromAsset(path);
                            try {
                                Gson gson = new Gson();
                                HashMap<String,String> storyText = new HashMap<String,String>();
                                storyText = gson.fromJson(storyTextJSon,storyText.getClass());
                                if (storyText.size()>0) {
                                    Log.d(TAG, "storyText successfully loaded for story " +
                                            fileList[indexOfStory]);
                                    String pageText = storyText.get(page);
                                    if (!pageText.isEmpty()){
                                        Log.d(TAG,"pageText "+ page +
                                                " found for story " + fileList[indexOfStory]);
                                        return pageText;

                                    } else { Log.e(TAG, "ERROR" + page +
                                            " not found for story" + fileList[indexOfStory]);

                                    }
                                } else {
                                    Log.e(TAG, "storyText not loaded");
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            break;
                    }

                    Log.e(TAG, "storyText not found for  " + fileList[indexOfStory]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "STORY TEXT NOT FOUND";
    }

    //USE THIS IF TITLE IS EVER SEPARATED FROM TEXT BODY
    public String GetStoryHeader(Context context, String story, String page) {
        story = story.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
        TAG = "LoadHeaders";
        mAssetManager = context.getAssets();
        mContext = context;
        ArrayList<ArrayList<String>> gameInfo = new ArrayList<ArrayList<String>>();
        try {
            String[] fileList = mAssetManager.list("Text Adventures");
            //CHANGE TO int i = 0
            int indexOfStory = -1;
            for (int i = 0; i<fileList.length; i++) {
                if (fileList[i].toString().equals(story)) {
                    indexOfStory = i;
                }
            }
            if (indexOfStory==-1){
                //Add error messages to other file and reference the variable names instead
                Log.e(TAG,"Story: " + story + " was not found");
                return "ERROR: STORY NOT FOUND!";

            } else {
                String[] storyFileList = mAssetManager.list("Text Adventures/"+fileList[indexOfStory]);
                for (int k = 0; k<storyFileList.length; k++){
                    String path = "Text Adventures/" +  fileList[indexOfStory] + "/" + storyFileList[k];

                    Log.d(TAG, storyFileList[k]);
                    switch(storyFileList[k]){
                        case("storyTitles.json"):
                            String storyTitleJSon = loadJSONFromAsset(path);
                            try {
                                Gson gson = new Gson();
                                HashMap<String,String> storyTitle = new HashMap<String,String>();
                                storyTitle = gson.fromJson(storyTitleJSon,storyTitle.getClass());
                                Log.d(TAG,storyTitle.toString());
                                if (storyTitle.size()>0) {
                                    Log.d(TAG, "storyTitles successfully loaded for story " +
                                            fileList[indexOfStory]);
                                    String pageTitle = storyTitle.get(page);
                                    if (!pageTitle.isEmpty()){
                                        Log.d(TAG,"pageTitle "+ page +
                                                " found for story " + fileList[indexOfStory]);
                                        return pageTitle;

                                    } else {
                                        Log.e(TAG, "ERROR" + page +
                                                " not found for story" + fileList[indexOfStory]);

                                    }
                                } else {
                                    Log.e(TAG, "storyTitles not loaded");
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            break;
                    }

                    Log.e(TAG, "storyTitle not found for  " + fileList[indexOfStory]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "TITLE NOT FOUND";
    }
    public String loadJSONFromAsset(String filepath) {
        String json = null;
        try {
            Resources res = mContext.getResources();
            InputStream is = res.getAssets().open(filepath);

            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
            Log.e("loadJSONFromAsset","Error while attempting to load a json file from " +
                    "path " + filepath);
            return "error";
        }

        return json;
    }
}

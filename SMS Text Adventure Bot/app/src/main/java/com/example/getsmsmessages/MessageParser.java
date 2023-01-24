package com.example.getsmsmessages;

import static com.example.getsmsmessages.Constants.ABOUT_TEXT;
import static com.example.getsmsmessages.Constants.INVALID_COMMAND;
import static com.example.getsmsmessages.Constants.KEY_MESSAGE_URI;
import static com.example.getsmsmessages.Constants.KEY_PURPOSE_URI;
import static com.example.getsmsmessages.Constants.KEY_RETURN_MESSAGE_URI;
import static com.example.getsmsmessages.Constants.KEY_SENDER_URI;
import static com.example.getsmsmessages.Constants.LEARN_MORE;
import static com.example.getsmsmessages.Constants.NEW_PLAYER_GREET_MESSAGE;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageParser extends Worker {

    private String userPhoneNumber;
    private String userMessage;
    private String purpose;
    public UserRepository mUserRepository;
    private final Context context;
    private String TAG = "WorkerLog";
    private ArrayList<String> mStoryTitles;
    private final int gamesPerPage = 10;
    private LoadAdventureData mLoadAdventureData;
    public MessageParser(@NonNull Context appContext,
                         @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
        context = appContext;

    }

    @NonNull
    @Override
    public Result doWork() {
        boolean has_outputData= false;
        boolean newUser = false;
        String returnMessage = "";
        userPhoneNumber = getInputData().getString(KEY_SENDER_URI);
        userMessage = getInputData().getString(KEY_MESSAGE_URI);
        purpose = getInputData().getString(KEY_PURPOSE_URI);
        Log.d(TAG, "Purpose is " + purpose);
        try{
            mUserRepository = new UserRepository(Dummy_activity.getAppApplication());
            mLoadAdventureData = new LoadAdventureData(context);
            Log.d(TAG, userPhoneNumber + " " +userMessage);
            if (mUserRepository.getUserPreviousGame(userPhoneNumber).size()==0) {
                newUser = true;
                Log.d(TAG, "NEW USER: " + userPhoneNumber);
                ArrayList<String> userPreviousGame = new ArrayList<>();
                ArrayList<String> userMenuProgress = new ArrayList<>();

                //Insert user to database

                //User
                userMenuProgress.addAll(Arrays.asList("1"));
                userPreviousGame.addAll(Arrays.asList("Menu"));
                Log.d(TAG, "Position to be set to:" + userMenuProgress);
                mUserRepository.insertUser(userPhoneNumber, userPreviousGame);
                //GameSave
                Log.d(TAG, "GameProgress to be set to: " + userMenuProgress);
                mUserRepository.insertUserGame(userPhoneNumber, "Menu", userMenuProgress);

                //Add new player greeting to the message
                returnMessage = NEW_PLAYER_GREET_MESSAGE + "\n";
                Thread.sleep(1000);
            }

            TAG = TAG + "Menu";
            List<String> gameProgress = Arrays.asList("0","0");
            ArrayList<String> commands = new ArrayList<>();

            commands.add("commands");
            commands.add("forget_me");
            commands.add("about");
            ArrayList<String> position = mUserRepository.getUserPreviousGame(userPhoneNumber);
            String lastPosition = "0";


            try {
                lastPosition = position.get(position.size()-1);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "lastPosition was not loaded from file and was set to zero");
            }
            try {
                gameProgress = mUserRepository.getUserGameProgress(userPhoneNumber,
                        lastPosition);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "gameProgress was not loaded from file and was set to \"0\",\"0\"");
            }
            String lastPositionInGame = gameProgress.get(gameProgress.size()-1);
            Log.d(TAG,"THIS: "+position.get(position.size()-1));
            mStoryTitles = mLoadAdventureData.GetStoryTitles(context);
            switch (position.get(position.size()-1)) {

                case ("Menu"):

                    Log.d(TAG, userPhoneNumber + " is in Menu");



                    ArrayList<String> menuResults =compileMenu(lastPositionInGame);
                    if (newUser){
                        returnMessage = returnMessage + menuResults.get(0);

                        returnMessage = returnMessage + "\n\n" + LEARN_MORE;

                    }
                    //ADD COMMANDS FROM MENU
                    for (int i=1; i<menuResults.size(); i++) {
                        commands.add(menuResults.get(i));
                    }
                    //
                    break;
                //GAME
                default:
                    if(lastPositionInGame.equals("-1")){
                        commands.addAll(Arrays.asList("back","quit","play"));
                        //USER IS IN INTRO SCREEN THAT PRINTS THE GAME DESCRIPTION

                    } else {
                        ArrayList<String> storyGoTo = mLoadAdventureData.GetStoryGoTo(
                                context,lastPosition,
                                "page " + lastPositionInGame);
                        for (int j=1;j<storyGoTo.size()+1;j++){
                            commands.add(String.valueOf(j));
                        }
                        commands.addAll(Arrays.asList("quit","back"));
                        Log.d(TAG, commands.toString());
                    }
                    //Implement Game Menu


                    //Load the amount of choices and add (1 to choices.length) to "new commands
                    break;
            }
            Log.d(TAG,"COMMANDS: " + commands);
            //CHECK THE MESSAGE
            boolean isCommand = false;
            String userCommand="";
            if (userMessage.length()>1 && !newUser){
                userCommand = userMessage.substring(1, userMessage.length());
                Log.d(TAG, "userCommand: " +userCommand);


                for (int i=0; i < commands.size(); i++) {
                    if (userCommand.equals(commands.get(i))) {
                        isCommand = true;
                        Log.d(TAG, "userCommand found from commands");
                        break;
                    }
                }
            }
            //If user message in command format and the user is not new
            if (isCommand){
                //Initialize the updated position and progress
                ArrayList<String> updatedGameProgress = new ArrayList<>();
                ArrayList<String> updatedPosition = new ArrayList<>();
                //Move this
                boolean number;
                try {
                    Integer.parseInt(userCommand);
                    number = true;
                    Log.d(TAG, "Command IS number");
                } catch (Exception e) {
                    number = false;
                }
                //If the command is a number
                if (number){
                    if (lastPosition.equals("Menu")){

                        //If the user has an empty save:

                        //SET POSITION TO PAGE -1
                        //If the number command was used in the menu
                        //Get game in folder

                        //returnArray.get(0) is the game description
                        //returnArray.get(1) is the name of the adventure
                        ArrayList<String> returnArray = compileGameInfo(
                                userCommand,lastPositionInGame);

                        //If the user is further than the game beginning
                        if (mUserRepository.getUserGameProgress(
                                userPhoneNumber,returnArray.get(1)).size()<2){
                            updatedGameProgress.add("-1");
                            returnMessage = returnMessage + returnArray.get(0);
                            updatedPosition.addAll(position);
                            updatedPosition.add(returnArray.get(1));
                            Log.d(TAG, "Position to be set to:" +updatedPosition);
                            mUserRepository.insertUser(userPhoneNumber,updatedPosition);

                            //IF GAME SAVE DOES NOT EXISTS CREATE A NEW ONE

                            Log.d(TAG, "GameProgress for " + returnArray.get(1) + "to be set to:"
                                    +updatedGameProgress);
                            mUserRepository.insertUserGame(
                                    userPhoneNumber,returnArray.get(1),updatedGameProgress);
                        } else{
                            updatedPosition.addAll(position);
                            updatedPosition.add(returnArray.get(1));
                            Log.d(TAG, "Position to be set to:" +updatedPosition);
                            mUserRepository.insertUser(userPhoneNumber,updatedPosition);
                            gameProgress = mUserRepository.getUserGameProgress(
                                    userPhoneNumber,returnArray.get(1));
                            lastPositionInGame = gameProgress.get(gameProgress.size()-1);
                            Log.d(TAG, "lastPosition is: " +returnArray.get(1) + "page is: "   +
                                    "page " + lastPositionInGame);
                            returnMessage = returnMessage + compileGamePage(
                                    returnArray.get(1),"page " + lastPositionInGame);
                        }

                    } else {
                        //Number command used in game
                        int commandInt = Integer.parseInt(userCommand);
                        String userCommandPage = "page 0";
                        ArrayList<String> pageGoTo = mLoadAdventureData.GetStoryGoTo(context,
                                lastPosition, "page " + lastPositionInGame);
                        try {
                            userCommandPage = pageGoTo.get(commandInt-1);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG,"userCommandPage was not loaded and was set to page 0");
                        }

                        String pageNumber =
                                userCommandPage.substring(5,userCommandPage.length());
                        Log.d(TAG,"POSSIBLE PAGES" + pageGoTo);
                        Log.d(TAG, "Integer command " + commandInt + " matches " + userCommandPage);
                        returnMessage = returnMessage + compileGamePage(lastPosition,userCommandPage);

                        updatedGameProgress.addAll(gameProgress);
                        updatedGameProgress.add(pageNumber);
                        Log.d(TAG, "GameProgress to be set to: " + updatedGameProgress);
                        mUserRepository.insertUserGame(userPhoneNumber,lastPosition,
                                updatedGameProgress);

                    }
                }

                else{
                    //Command is not a number and therefore is one of the following commands
                    int lastPositionInGameInt = Integer.parseInt(lastPositionInGame);


                    switch(userCommand){
                        case("quit"):


                            for (int i = 0; i<position.size()-1;i++){
                                updatedPosition.add(position.get(i));
                            }
                            if (updatedPosition.size()==0) {
                                updatedPosition.add("Menu");
                            }
                            Log.d(TAG, "Position to be set to:" +updatedPosition);
                            mUserRepository.insertUser(userPhoneNumber,updatedPosition);


                            ////
                            //GET LAST POSITION IN MENU AND PRINT IT
                            gameProgress = mUserRepository.getUserGameProgress(userPhoneNumber,"Menu");
                            lastPositionInGame = gameProgress.get(gameProgress.size()-1);
                            returnMessage = returnMessage + compileMenu(lastPositionInGame).get(0);

                            break;
                        case("back"):
                            /*
                            Back should send to user back to some previous page

                            If the user is at the beginning of and adventure or in the description
                            page, the user should be sent back to the menu page
                                (with the corresponding index number?)

                             */
                            Log.d(TAG, "back started");
                            Log.d(TAG, "gameProgress "+ gameProgress +
                                    " and its size is: " + gameProgress.size());

                            //What should this do if the player has just entered an adventure?
                            if (gameProgress.size()<3){
                                //Set current game progress to -1,0
                                updatedGameProgress.addAll(Arrays.asList("-1","0"));
                                Log.d(TAG, "GameProgress to be set to:" +updatedGameProgress);

                                mUserRepository.insertUserGame(userPhoneNumber,lastPosition,
                                        updatedGameProgress);


                                //Set the users current position to be inside the menu

                                //Copy position
                                for (int i = 0; i<position.size()-1;i++){
                                    updatedPosition.add(position.get(i));
                                }
                                //Set user
                                Log.d(TAG, "Position to be set to:" +updatedPosition);
                                mUserRepository.insertUser(userPhoneNumber,updatedPosition);
                                gameProgress = mUserRepository.getUserGameProgress(userPhoneNumber,"Menu");
                                lastPositionInGame = gameProgress.get(gameProgress.size()-1);
                                returnMessage = returnMessage + compileMenu(lastPositionInGame).get(0);
                            }
                            else {
                                for (int i = 0; i<gameProgress.size()-1;i++){
                                    updatedGameProgress.add(gameProgress.get(i));
                                }
                                lastPositionInGame = updatedGameProgress.get(updatedGameProgress.size()-1);
                                returnMessage = returnMessage + compileGamePage(lastPosition,"page " +lastPositionInGame);
                                Log.d(TAG, "GameProgress to be set to:" +updatedGameProgress);
                                mUserRepository.insertUserGame(userPhoneNumber,
                                        lastPosition, updatedGameProgress);

                            }

                            break;
                        case("commands"):
                            //List possible commands
                            returnMessage = returnMessage + "Possible commands are: ";
                            for (int i = 0; i<commands.size();i++){
                                returnMessage = returnMessage + ", \"." + commands.get(i) + "\"";
                            }


                            break;
                        case("reset"):
                            //Clears the game progress.
                            //Only possible if user is inside a game
                            updatedGameProgress.addAll(Arrays.asList("-1","0"));
                            Log.d(TAG, "GameProgress to be set to:" +updatedGameProgress);
                            mUserRepository.insertUserGame(userPhoneNumber,
                                    position.get(position.size()-1), updatedGameProgress);
                            lastPositionInGame = updatedGameProgress.get(updatedGameProgress.size()-1);
                            returnMessage = returnMessage + compileGamePage(
                                    lastPosition,lastPositionInGame);
                            break;
                        case("forget_me"):
                            //Removes the user from user_table (they will still remain in
                            //the saveGame_table
                            Log.d(TAG, "User" + userPhoneNumber + " is to be forgotten");
                            mUserRepository.deleteUser(userPhoneNumber);
                            returnMessage = returnMessage + "You've been forgotten.";
                            break;
                        case("next"):
                            updatedGameProgress.add(String.valueOf(
                                    lastPositionInGameInt +1));
                            Log.d(TAG, "GameProgress to be set to:" +updatedGameProgress);
                            mUserRepository.insertUserGame(userPhoneNumber,"Menu",
                                    updatedGameProgress);
                            lastPositionInGame = updatedGameProgress.get(updatedGameProgress.size()-1);
                            returnMessage = returnMessage + compileMenu(lastPositionInGame).get(0);

                            break;
                        case("previous"):

                            updatedGameProgress.add(String.valueOf(
                                    lastPositionInGameInt -1));
                            Log.d(TAG, "GameProgress to be set to:" +updatedGameProgress);
                            mUserRepository.insertUserGame(userPhoneNumber,"Menu",
                                    updatedGameProgress);
                            lastPositionInGame = updatedGameProgress.get(updatedGameProgress.size()-1);
                            returnMessage = returnMessage + compileMenu(lastPositionInGame).get(0);
                            break;
                        case("play"):
                            /*
                            Game progress should be {"-1","0"} since the last position was
                            in game description screen.

                             */
                            //THE GAME PROGRESS IS SET TO "-1","0"
                            //INSTEAD OF ADDING 0 TO THE END OF IT
                            updatedGameProgress.add("-1");
                            updatedGameProgress.add("0");
                            Log.d(TAG, "GameProgress to be set to:" +updatedGameProgress);
                            mUserRepository.insertUserGame(userPhoneNumber,
                                    lastPosition,updatedGameProgress);
                            returnMessage = returnMessage +
                                    compileGamePage(lastPosition,"page 0");
                            break;
                        case("about"):

                            /*
                            Insert here a reason why this app exists
                             */
                            returnMessage = returnMessage + ABOUT_TEXT;

                    }
                }
            }  else {
                if (!newUser){
                    returnMessage = INVALID_COMMAND;
                    //Add here invalid command message
                }

            }
            Data outputData = null;
            if (purpose != null){
                switch(purpose){
                    case("RETURN"):
                        outputData = new Data.Builder()
                                .putString(KEY_RETURN_MESSAGE_URI,returnMessage)
                                .build();
                        has_outputData=true;

                }
            }
            SendSMS messenger = new SendSMS();
            messenger.sendLongSMS(userPhoneNumber, returnMessage);
            //…
            Log.d(TAG, "MESSAGE TO BE SENT: " + returnMessage);
            if(has_outputData){
                return Result.success(outputData);
            } else{
                return Result.success();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error while parsing the message");
        }

        return Result.failure();
    }

    public ArrayList<String> compileMenu (String position){
        String TAG_CompileMenu = "Worker" + "CompileMenu";
        Log.d(TAG_CompileMenu, "position is " + position + " and max per page is " + gamesPerPage);
        ArrayList<String> returnValue = new ArrayList<>();
        if (mStoryTitles.size()>0) {
            int pos = Integer.parseInt(position);
            int range =(int) Math.ceil((double)mStoryTitles.size()/(double)gamesPerPage);
            if (0<pos && pos<range+1){
                returnValue.add("Select a text adventure: ");
                int lastGame = Math.min((pos)*gamesPerPage-1,mStoryTitles.size()-1);
                int firstGame = (pos-1)*gamesPerPage;
                if (pos>1){
                    returnValue.set(0,returnValue.get(0)+"\n" +
                            "Go to the previous page with .previous");
                    returnValue.add("previous");
                    Log.d(TAG_CompileMenu,"Previous option added for page " + pos);

                }
                Log.d(TAG_CompileMenu, "pos is: "+ pos + " and range is: " + range);
                Log.d(TAG_CompileMenu,"fistGame and lastGame are" + firstGame + " " + lastGame);
                Log.d(TAG_CompileMenu, "");
                for (int i=0;i<=lastGame-firstGame;i++){
                    returnValue.set(0,returnValue.get(0)+"\n" +
                            "." + (i+1) + " - "+ mStoryTitles.get(i+firstGame));
                    returnValue.add(String.valueOf(i+1));
                    Log.d(TAG_CompileMenu,"Added adv:" +mStoryTitles.get(i+firstGame) + "with i=" +
                            i+ " firstGame=" + firstGame + " lastGame=" + lastGame );
                }
                // if page is not the last page
                //last page is the page

                if (pos<range) {
                    returnValue.set(0,returnValue.get(0)+"\n" +
                            "Go to the next page with .next");
                    returnValue.add("next");
                    Log.d(TAG_CompileMenu,"Next option added for page" + pos);
                }
            } else {

                returnValue.add("\n" +
                        "Error your position is invalid. You've been moved to the start " +
                                "of the menu.\n");
            }

        } else {
            returnValue.add("\n Error mStoryTitles is empty \n");
        }

        return returnValue;
    }
    public String help (String command){

        return "";
    }
    public ArrayList<String> compileGameInfo(String command, String gamePosition ){
        ArrayList<String> returnArray = new ArrayList<>();
        String TAG_gameInfo = "WorkerGameInfo";
        int pos = Integer.parseInt(gamePosition);
        int ind = Integer.parseInt(command);
        int targetIndex = (pos-1)*gamesPerPage+ind-1;
        String returnString ="";
        String gameName = mLoadAdventureData.GetStoryTitles(context).get(targetIndex);
        ArrayList<String> gameInfo = new ArrayList<>();
        try{
            //gameInfo contents: title, author, length, difficulty, rating and url
            gameInfo = mLoadAdventureData.GetStoryInfo(context,gameName);
            returnString = returnString + "Adventure name: " + gameInfo.get(0) ;
            returnString = returnString + "\n Author: " + gameInfo.get(1) ;
            returnString = returnString + "\n Length : " + gameInfo.get(2) ;
            returnString = returnString + "\n Difficulty : " + gameInfo.get(3) ;
            returnString = returnString + "\n Rating : " + gameInfo.get(4) ;
            returnString = returnString + "\n URL : " + gameInfo.get(5) ;
            returnString = returnString + "\n Description : \n" + gameInfo.get(6) ;
            returnString = returnString + "\n\n Type .play to start the adventure and .back to " +
                    "return to the menu.";
            returnArray.add(returnString);
            returnArray.add(gameName);
            Log.d(TAG_gameInfo, "Information for game " + gameName + " loaded successfully");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG_gameInfo, "Error occurred while loading and parsing " +
                    "game info from GetStoryInfo." + "\nCurrent returnArray is: " + returnArray);
        }
        return returnArray;

    }

    public String compileGamePage(String lastPosition,String userCommandPage) {
        String returnString = "";
        String pageTitle = mLoadAdventureData.GetStoryHeader(context,lastPosition,userCommandPage);
        String pageText = mLoadAdventureData.GetStoryText(context,lastPosition,
                userCommandPage);
        ArrayList<String> pageChoices = mLoadAdventureData.GetStoryChoices(context,
                lastPosition,userCommandPage);
        returnString = returnString + pageTitle;
        returnString = returnString + "\n" + pageText;
        for (int i = 0; i<pageChoices.size();i++) {
            returnString = returnString + "\n."+ (i+1) + " - " + pageChoices.get(i);
        }
        return returnString;
    }

}


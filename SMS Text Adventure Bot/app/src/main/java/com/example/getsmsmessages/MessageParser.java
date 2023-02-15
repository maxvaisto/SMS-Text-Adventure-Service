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
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
/*
    *****************************************
    THIS IS THE MAIN CLASS OF THE APPLICATION
    *****************************************


    MessageParser class
    * (1) Reads the user message.
    * (2) Fetches the user data.
    * (3) Given the users current position and message the users position is updated
    * if the message if valid (otherwise error message).
    * (4) Compiles a text response to the user command.
    * (5) Calls a SendSMS instance to send the response.
    *
    * The user phone number is not checked whether it is valid or not in this function
    * it must be done elsewhere. Also, the validity of the repository contents is also not checked.

 */
public class MessageParser extends Worker {


    //Initialize class variables

    //Repo for user and game data
    public UserRepository mUserRepository;
    //App context used for receiving game data
    private final Context context;

    //Debug tag (Start of debug TAG for this class)
    private String TAG = "WorkerLog";

    //List of story titles private member variable to reduce passing of data to the compileMenu
    //function simpler
    private ArrayList<String> mStoryTitles;

    //Constant that limits the amount of games listed per page
    private final int gamesPerPage = 10;

    //Instance of LoadAdventureData used for reading the game page text from JSON files
    private final LoadAdventureData mLoadAdventureData;



    //Simple class initialization that does not do anything by itself
    public MessageParser(@NonNull Context appContext,
                         @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
        context = appContext;
        //Create a new user repository and adventure data loader (fetches Strings)
        mUserRepository = new UserRepository(Dummy_activity.getAppApplication());
        mLoadAdventureData = new LoadAdventureData(context);
    }

    //Only after the message parser is called to action the script to read and send a message
    //is activated
    @NonNull
    @Override
    public Result doWork() {

        //Initialize support variables
        boolean has_outputData= false;
        boolean newUser = false;
        StringBuilder returnMessage = new StringBuilder();

        //Game commands
        ArrayList<String> commands = new ArrayList<>(Arrays.asList("commands","forget_me","about"));

        //Get text message information from the input data

        //User phone number
        String userPhoneNumber = getInputData().getString(KEY_SENDER_URI);
        //Sent text message
        String userMessage = getInputData().getString(KEY_MESSAGE_URI);
        //Used to detect invalid URIs
        String purpose = getInputData().getString(KEY_PURPOSE_URI);
        //For debugging
        Log.d(TAG, "Purpose is " + purpose);

        //Start the main part of the function
        try{

            //NEW USER
            Log.d(TAG, userPhoneNumber + " " + userMessage);
            if (mUserRepository.getUserPreviousGame(userPhoneNumber).size()==0) {
                newUser = true;
                Log.d(TAG, "NEW USER: " + userPhoneNumber);

                //User
                ArrayList<String> userPreviousGame = new ArrayList<>(Collections.singletonList("Menu"));
                ArrayList<String> userMenuProgress = new ArrayList<>(Collections.singletonList("1"));

                //Insert user to database
                Log.d(TAG, "Position to be set to:" + userMenuProgress);
                mUserRepository.insertUser(userPhoneNumber, userPreviousGame);
                //GameSave
                Log.d(TAG, "GameProgress to be set to: " + userMenuProgress);
                mUserRepository.insertUserGame(userPhoneNumber, "Menu", userMenuProgress);

                //Add new player greeting to the message
                returnMessage = new StringBuilder(NEW_PLAYER_GREET_MESSAGE + "\n");
                Thread.sleep(1000);
            }

            TAG = TAG + "Menu";
            List<String> gameProgress;

            ArrayList<String> position = mUserRepository.getUserPreviousGame(userPhoneNumber);
            String lastPosition;

            String lastPositionInGame = "";
            try {
                lastPosition = position.get(position.size()-1);
                Log.d(TAG,"Last position in game is: " + lastPositionInGame);
            } catch (Exception e) {
                e.printStackTrace();
                lastPosition = "0";
                Log.e(TAG, "lastPosition was not loaded from file and was set to zero");
            }


            //Try to load the user's current game position
            try {
                gameProgress = mUserRepository.getUserGameProgress(userPhoneNumber,
                        lastPosition);

            } catch (Exception e) {
                gameProgress = Arrays.asList("0","0");
                e.printStackTrace();
                Log.e(TAG, "gameProgress was not loaded from file and was set to \"0\",\"0\"");
            }

            //Get last position in game
            lastPositionInGame = gameProgress.get(gameProgress.size()-1);

            //Get a list of all of the current stories
            mStoryTitles = mLoadAdventureData.GetStoryTitles(context);

            //Log.d(TAG,"There");
            //Check the current position of the user to give differing messages if the user is in
            //game or in the Menu
            //The user is in menu
            if ("Menu".equals(position.get(position.size() - 1))) {
                Log.d(TAG, userPhoneNumber + " is in Menu");

                ArrayList<String> menuResults = compileMenu(lastPositionInGame);
                if (newUser) {
                    returnMessage.append(menuResults.get(0));

                    returnMessage.append("\n\n").append(LEARN_MORE);

                }
                //ADD COMMANDS FROM MENU
                for (int i = 1; i < menuResults.size(); i++) {
                    commands.add(menuResults.get(i));
                }
                //
                //The user is in game
            } else {//Check if the user is in the game description / into page
                if (lastPositionInGame.equals("-1")) {

                    //Only in the game description page can the user select play
                    //to start playing the adventure
                    commands.add("play");

                } else {
                    //Otherwise add the current page Go To pages to the list of
                    // available commands

                    //Get go to pages from file
                    ArrayList<String> storyGoTo = mLoadAdventureData.GetStoryGoTo(
                            context, lastPosition,
                            "page " + lastPositionInGame);
                    //Add their corresponding indexes to the list of available commands
                    for (int j = 1; j < storyGoTo.size() + 1; j++) {
                        commands.add(String.valueOf(j));
                    }
                }

                //Also add quit and back
                commands.addAll(Arrays.asList("quit", "back"));
            }
            Log.d(TAG,"COMMANDS: " + commands);
            //CHECK THE MESSAGE
            boolean isCommand = checkCommand(userMessage,commands,newUser);
            String userCommand= userMessage.substring(1);

            //If the user is in a position to send a command worth reading (not a new user)
            // and the command is valid we continue
            if (isCommand && !newUser){
                //Initialize the updated position and progress
                ArrayList<String> updatedGameProgress = new ArrayList<>();
                ArrayList<String> updatedPosition = new ArrayList<>();


                //If the command is a number
                if (isNumber(userCommand)){

                    //Number command used in the main menu
                    //This creates a
                    if (lastPosition.equals("Menu")){

                        //If the user has an empty save:

                        //SET POSITION TO PAGE -1
                        //If the number command was used in the menu
                        //Get game in folder

                        //returnArray.get(0) is the game description
                        //returnArray.get(1) is the name of the adventure
                        ArrayList<String> returnArray = compileGameInfo(
                                userCommand,lastPositionInGame);

                        //If the user is at the beginning of the game
                        if (mUserRepository.getUserGameProgress(
                                userPhoneNumber,returnArray.get(1)).size()<2){
                            updatedGameProgress.add("-1");
                            returnMessage.append(returnArray.get(0));
                            updatedPosition.addAll(position);
                            updatedPosition.add(returnArray.get(1));
                            Log.d(TAG, "Position to be set to:" +updatedPosition);
                            mUserRepository.insertUser(userPhoneNumber,updatedPosition);
                            Log.d(TAG, "GameProgress for " + returnArray.get(1) + "to be set to:"
                                    +updatedGameProgress);
                            mUserRepository.insertUserGame(
                                    userPhoneNumber,returnArray.get(1),updatedGameProgress);
                        } else{
                            //the user is further than the game beginning
                            updatedPosition.addAll(position);
                            updatedPosition.add(returnArray.get(1));
                            Log.d(TAG, "Position to be set to:" +updatedPosition);
                            mUserRepository.insertUser(userPhoneNumber,updatedPosition);
                            gameProgress = mUserRepository.getUserGameProgress(
                                    userPhoneNumber,returnArray.get(1));
                            lastPositionInGame = gameProgress.get(gameProgress.size()-1);
                            Log.d(TAG, "lastPosition is: " +returnArray.get(1) + "page is: "   +
                                    "page " + lastPositionInGame);
                            returnMessage.append(compileGamePage(
                                    returnArray.get(1), "page " + lastPositionInGame));
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
                                userCommandPage.substring(5);
                        Log.d(TAG,"POSSIBLE PAGES" + pageGoTo);
                        Log.d(TAG, "Integer command " + commandInt + " matches " + userCommandPage);
                        returnMessage.append(compileGamePage(lastPosition, userCommandPage));

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
                            returnMessage.append(compileMenu(lastPositionInGame).get(0));

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
                                returnMessage.append(compileMenu(lastPositionInGame).get(0));
                            }
                            else {
                                for (int i = 0; i<gameProgress.size()-1;i++){
                                    updatedGameProgress.add(gameProgress.get(i));
                                }
                                lastPositionInGame = updatedGameProgress.get(updatedGameProgress.size()-1);
                                returnMessage.append(compileGamePage(lastPosition, "page " + lastPositionInGame));
                                Log.d(TAG, "GameProgress to be set to:" +updatedGameProgress);
                                mUserRepository.insertUserGame(userPhoneNumber,
                                        lastPosition, updatedGameProgress);

                            }

                            break;
                        case("commands"):
                            //List possible commands
                            returnMessage.append("Possible commands are: ");
                            for (int i = 0; i<commands.size();i++){
                                returnMessage.append(", \".").append(commands.get(i)).append("\"");
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
                            returnMessage.append(compileGamePage(
                                    lastPosition, lastPositionInGame));
                            break;
                        case("forget_me"):
                            //Removes the user from user_table (they will still remain in
                            //the saveGame_table
                            Log.d(TAG, "User" + userPhoneNumber + " is to be forgotten");
                            mUserRepository.deleteUser(userPhoneNumber);
                            returnMessage.append("You've been forgotten.");
                            break;
                        case("next"):
                            updatedGameProgress.add(String.valueOf(
                                    lastPositionInGameInt +1));
                            Log.d(TAG, "GameProgress to be set to:" +updatedGameProgress);
                            mUserRepository.insertUserGame(userPhoneNumber,"Menu",
                                    updatedGameProgress);
                            lastPositionInGame = updatedGameProgress.get(updatedGameProgress.size()-1);
                            returnMessage.append(compileMenu(lastPositionInGame).get(0));

                            break;
                        case("previous"):

                            updatedGameProgress.add(String.valueOf(
                                    lastPositionInGameInt -1));
                            Log.d(TAG, "GameProgress to be set to:" +updatedGameProgress);
                            mUserRepository.insertUserGame(userPhoneNumber,"Menu",
                                    updatedGameProgress);
                            lastPositionInGame = updatedGameProgress.get(updatedGameProgress.size()-1);
                            returnMessage.append(compileMenu(lastPositionInGame).get(0));
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
                            returnMessage.append(compileGamePage(lastPosition, "page 0"));
                            break;
                        case("about"):
                            returnMessage.append(ABOUT_TEXT);
                            break;
                        //This should never be reached
                        default:
                            returnMessage.append("Something went wrong");
                            break;
                    }
                }
            }  else {
                /*
                    If the user has sent an invalid command that
                 */
                if (!newUser){
                    returnMessage = new StringBuilder(INVALID_COMMAND);
                }

            }

            //Finally we compile the results into a URI to be sent to the SMS message sender

            Data outputData = null;
            if (purpose != null){
                if ("RETURN".equals(purpose)) {
                    outputData = new Data.Builder()
                            .putString(KEY_RETURN_MESSAGE_URI, returnMessage.toString())
                            .build();
                    has_outputData = true;
                }
            }
            SendSMS messenger = new SendSMS();
            messenger.sendLongSMS(userPhoneNumber, returnMessage.toString());
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

    //Checks if the user command is valid
    private boolean checkCommand(String command, ArrayList<String> commands,boolean newUser){

        if (command.length()>1 && !newUser){
            //Refine the command to remove the starting "." charcter
            command = command.substring(1);
            Log.d(TAG, "userCommand: " +command);


            for (int i=0; i < commands.size(); i++) {
                if (command.equals(commands.get(i))) {

                    Log.d(TAG, "userCommand found from commands");
                    return true;
                }
            }
        }

        return false;
    }

    //Check if command is a number
    private boolean isNumber(String command) {

        try {
            Integer.parseInt(command);
            Log.d(TAG, "Command IS number");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //Used to compile Menu page text
    private ArrayList<String> compileMenu (String position){
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

    //Game information is compiled and returned
    private ArrayList<String> compileGameInfo(String command, String gamePosition ){

        ArrayList<String> returnArray = new ArrayList<>();
        String TAG_gameInfo = "WorkerGameInfo";
        int pos = Integer.parseInt(gamePosition);
        int ind = Integer.parseInt(command);
        int targetIndex = (pos-1)*gamesPerPage+ind-1;
        String returnString ="";
        String gameName = mLoadAdventureData.GetStoryTitles(context).get(targetIndex);
        ArrayList<String> gameInfo;
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


    //This is used to compile all of the different parts of the game page into a
    //lastPosition is a the index of the last user activity
    //storyPage is the page that the text is retrieved from
    private String compileGamePage(String lastPosition,String storyPage) {

        StringBuilder returnString = new StringBuilder();
        String pageTitle = mLoadAdventureData.GetStoryHeader(context,lastPosition,storyPage);
        String pageText = mLoadAdventureData.GetStoryText(context,lastPosition,
                storyPage);
        ArrayList<String> pageChoices = mLoadAdventureData.GetStoryChoices(context,
                lastPosition,storyPage);
        returnString.append(pageTitle);
        returnString.append("\n").append(pageText);
        for (int i = 0; i<pageChoices.size();i++) {
            returnString.append("\n.").append(i + 1).append(" - ").append(pageChoices.get(i));
        }
        return returnString.toString();
    }

}


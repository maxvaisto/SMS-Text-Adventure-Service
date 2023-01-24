package com.example.getsmsmessages;

public class Constants {

    //Most of these have not been implemented. Most of these were created at the beginning of the
    //android programming part of the project and have been forgotten ever since.
    //I was supposed to add here descriptions of each and every command which can be printed
    //via ".commands [commmand]" e.g. ".commands quit".
    public static final String INVALID_COMMAND = "The Command you've sent is invalid. \n " +
            "Start your command with a period and try again. \n " +
            "To see all possible commands type .commands";
    public static final String NEW_PLAYER_GREET_MESSAGE = " SMS TEXT ADVENTURE MESSENGER \n \n" +
            "TO OPERATE THIS PROGRAM START YOUR EVERY COMMAND WITH A PERIOD / '.'-SIGN." +
            "MESSAGES WITHOUT A PERIOD AT THE BEGINNING WILL BE IGNORED. \n\n";
    public static final String LEARN_MORE = "If you wist to learn more about this project, " +
            "type .about";

    public static final String KEY_PURPOSE_URI = "KEY_PURPOSE_URI";

    //The messages were compressed into a package that used said codes to differentiate the
    //parts of the sms message
    public static final String KEY_SENDER_URI = "KEY_SENDER_URI";
    public static final String KEY_MESSAGE_URI = "KEY_MESSAGE_URI";
    public static final String KEY_RETURN_MESSAGE_URI = "KEY_RETURN_MESSAGE_URI";
    public static final String KEY_TEXT_MESSAGE = "KEY_TEXT_MESSAGE";

    //Information about the project
    public static final String ABOUT_TEXT = "SMS Text-adventure messenger is an text adventure app " +
            "made by Max Väistö. The application/service was initially released in August of 2022." +
            "\nThe app was created in Android Studio and runs on Java and SQLite " +
            "and is designed to run on Android devices (versions 6.1.1+). This program was created as a personal project to offer " +
            "mobile entertainment via SMS even when the user has no internet connection." +
            "\n\n The text adventures are originally from https://chooseyourstory.com " +
            "and were collected by a script also made by me that uses Selenium " +
            "chromedriver, Java and JS. Said script is not included " +
            "within the app files." +
            "\n\n To learn more about this project contact max.vaisto@gmail.com.";

    //Add this instead?
    public static final String a = "This app was made with love, sweat, Android Studio and tears."
            + " and runs on Java and SQLite";


    private Constants() {}

}

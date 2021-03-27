package com.example.trivia.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
    private SharedPreferences preferences;

    public Prefs(Activity activity) {
        preferences = activity.getPreferences(Context.MODE_PRIVATE);
    }

    public void saveHighScore(int score){
        int highScore = preferences.getInt("highScore", 0);
        if(highScore < score){
            preferences.edit().putInt("highScore",score).apply();
        }
    }

    public int getHighScore(){
        return preferences.getInt("highScore", 0);
    }

    public void saveState(int index){
        preferences.edit().putInt("index",index).apply();
    }

    public int getState(){
        return  preferences.getInt("index",0);
    }

    public void saveCurrentScore(int score){
        preferences.edit().putInt("currentScore" , score).apply();
    }

    public int getCurrentScore(){
        return preferences.getInt("currentScore", 0);
    }
}

package com.example.trivia.controller;

import android.app.Application;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class AppController extends Application {
    private static AppController mInstance;
    private RequestQueue requestQueue;

    public static synchronized AppController getInstance(){
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public RequestQueue getRequestQueue(){
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(this);
        }
        return requestQueue;
    }

    public <T> void addRequestQueue(Request <T> req){
        getRequestQueue().add(req);
    }


}

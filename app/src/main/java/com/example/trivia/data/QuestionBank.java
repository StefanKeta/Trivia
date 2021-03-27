package com.example.trivia.data;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.trivia.controller.AppController;
import com.example.trivia.model.Question;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class QuestionBank {

    private ArrayList<Question> questions = new ArrayList<>();
    private String url = "https://raw.githubusercontent.com/curiousily/simple-quiz/master/script/statements-data.json";

    public ArrayList<Question> getQuestions(final AnswerListAsyncResponse callback){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for(int i = 0 ; i < response.length() ; i++){
                    try {
                        Question question = new Question();
                        question.setQuestion(response.getJSONArray(i).get(0).toString());
                        question.setAnswer(response.getJSONArray(i).getBoolean(1));
                        questions.add(question);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if(callback != null) callback.processFinished(questions);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR", "onErrorResponse: " + error);
            }
        });
        AppController.getInstance().addRequestQueue(jsonArrayRequest);

        Log.d("questions", "getQuestions: " + questions);
        return questions;
    }

}

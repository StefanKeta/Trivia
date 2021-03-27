package com.example.trivia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trivia.data.AnswerListAsyncResponse;
import com.example.trivia.data.QuestionBank;
import com.example.trivia.model.Question;
import com.example.trivia.model.Score;
import com.example.trivia.util.Prefs;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button trueButton;
    private Button falseButton;
    private Button shareButton;
    private ImageButton prevButton;
    private ImageButton nextButton;
    private TextView cardViewQuestion;
    private TextView questionCounter;
    private TextView scoreCounter;
    private TextView highScoreTV;

    private ArrayList<Question> questions;

    private static final String SCORE_ID = "score";
    private int currentIndex = 0;
    private final int worth = 100;
    private Score score ;
    private int highScore ;
    private String highScoreString;
    private Prefs prefs;

    private SoundPool soundPool;
    private AudioAttributes attributes;

    private int correctSound,wrongSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        questions = new QuestionBank().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questions) {
                cardViewQuestion.setText(questions.get(currentIndex).getQuestion());
                questionCounter.setText((currentIndex+1) + "/" + questions.size());
            }
        });

    }

    private void init() {
        score = new Score();
        prefs = new Prefs(this);
        currentIndex = prefs.getState();

        trueButton = findViewById(R.id.true_button);
        falseButton = findViewById(R.id.false_button);
        shareButton = findViewById(R.id.share_button);
        prevButton = findViewById(R.id.prev_button);
        nextButton = findViewById(R.id.next_button);
        cardViewQuestion = findViewById(R.id.card_view_question);
        questionCounter = findViewById(R.id.counter_text);
        scoreCounter = findViewById(R.id.score_tv);
        highScoreTV = findViewById(R.id.highscore_tv);
        highScoreString = getResources().getString(R.string.high_score);


        score.setScore(prefs.getCurrentScore());
        scoreCounter.setText(String.valueOf(score.getScore()));

        highScore = prefs.getHighScore();
        highScoreTV.setText(highScoreString + highScore);

        trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);
        shareButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);

        attributes = new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION).build();
        soundPool = new SoundPool.Builder().setMaxStreams(2).setAudioAttributes(attributes).build();

        correctSound = soundPool.load(this,R.raw.correct,1);
        wrongSound = soundPool.load(this,R.raw.defeat_one,1);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.prev_button:
                prevButton();
                break;
            case R.id.true_button:
                checkAnswer(true);
                updateQuestion();
                break;
            case R.id.false_button:
                checkAnswer(false);
                updateQuestion();
                break;
            case R.id.next_button:
                nextButton();
                break;
            case R.id.share_button:
                sendHighScoreMail();
                break;
        }
    }

    private void nextButton() {
        currentIndex = (currentIndex + 1) % questions.size();
        updateQuestion();
    }

    private void prevButton() {
        if (currentIndex == 0) {
            currentIndex = questions.size() - 1;
        } else {
            currentIndex--;
        }
        updateQuestion();
    }

    private void checkAnswer(boolean answer) {
        boolean correctAnswer = questions.get(currentIndex).isAnswer();
        int toastID ;

        if (correctAnswer == answer) {
            fadeAnim();
            addPoints();
            toastID = R.string.correct;
            checkHighScore();
            soundPool.play(correctSound,10,10,0,0,0);
            prefs.saveHighScore(score.getScore());
        } else
        {
            toastID = R.string.incorrect;
            shakeAnim();
            subtractPoints();
            soundPool.play(wrongSound,10,10,0,0,0);
        }
        Toast.makeText(this, toastID, Toast.LENGTH_SHORT).show();
        scoreCounter.setText(String.valueOf(score.getScore()));
    }

    private void addPoints(){
        score.setScore(score.getScore() + worth);
    }

    private void subtractPoints(){
        if(score.getScore() >= worth){
            score.setScore(score.getScore()-worth);
        } else{
            score.setScore(0);
        }
    }

    private void updateQuestion() {
        String question = questions.get(currentIndex).getQuestion();
        this.cardViewQuestion.setText(question);
        questionCounter.setText((currentIndex+1) + " / " + questions.size());
    }

    private void checkHighScore(){
        if(score.getScore() > highScore){
            highScoreTV.setText( highScoreString + score.getScore());
        }
    }

    private void sendHighScoreMail(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "I am playing trivia!");
        intent.putExtra(Intent.EXTRA_TEXT, "My current score is " + score.getScore() + ". My highest is " + highScore);
        startActivity(intent);
    }

    private void shakeAnim(){
        Animation shake = AnimationUtils.loadAnimation(this,R.anim.shake_anim);
        final CardView cardView = findViewById(R.id.card_view);
        cardView.setAnimation(shake);
        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setBackgroundColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setBackgroundColor(Color.WHITE);
                nextButton();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void fadeAnim(){
        final CardView cardView = findViewById(R.id.card_view);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(500);
        alphaAnimation.setRepeatCount(2);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setBackgroundColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setBackgroundColor(Color.WHITE);
                nextButton();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        prefs.saveHighScore(score.getScore());
        prefs.saveState(this.currentIndex);
        prefs.saveCurrentScore(score.getScore());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        prefs.saveState(0);
        prefs.saveCurrentScore(0);
    }
}
package com.example.dinomath;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class CompareGameActivity extends AppCompatActivity {
    private TextView question, scoreText, comboText, feedback;
    private Button greaterBtn, lessBtn, exitBtn;
    private int score = 0, combo = 0, questionCount = 0;
    private int number1, number2;
    private SharedPreferences sharedPreferences;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compare_game_activity);

        question = findViewById(R.id.question);
        scoreText = findViewById(R.id.score);
        comboText = findViewById(R.id.combo);
        feedback = findViewById(R.id.feedback);
        greaterBtn = findViewById(R.id.greaterBtn);
        lessBtn = findViewById(R.id.lessBtn);
        exitBtn = findViewById(R.id.exitBtn);

        sharedPreferences = getSharedPreferences("DinoMathScores", MODE_PRIVATE);
        generateQuestion();

        greaterBtn.setOnClickListener(v -> checkAnswer(true));
        lessBtn.setOnClickListener(v -> checkAnswer(false));
        exitBtn.setOnClickListener(v -> finish());
    }

    private void generateQuestion() {
        if (questionCount >= 10) {
            endRound();
            return;
        }

        Random rand = new Random();
        number1 = rand.nextInt(100);
        number2 = rand.nextInt(100);
        question.setText(number1 + " ? " + number2);
        feedback.setText(""); // Clear feedback when generating new question
    }

    private void checkAnswer(boolean isGreater) {
        boolean correct = (isGreater && number1 > number2) || (!isGreater && number1 < number2);

        if (correct) {
            score += 10 + (combo * 2);
            combo++;
            feedback.setText("Correct! 🎉");
            feedback.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            combo = 0;
            feedback.setText("Wrong! ❌");
            feedback.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }

        questionCount++;
        updateScore();

        // Delay next question to show feedback
        handler.postDelayed(() -> {
            generateQuestion();
        }, 1500); // 1.5 second delay
    }

    private void updateScore() {
        scoreText.setText("Score: " + score);
        comboText.setText("Combo: " + combo);
    }

    private void endRound() {
        int highScore = sharedPreferences.getInt("compareHighScore", 0);
        if (score > highScore) {
            sharedPreferences.edit().putInt("compareHighScore", score).apply();
        }

        new android.app.AlertDialog.Builder(this)
                .setTitle("Round Over!")
                .setMessage("Your Score: " + score +
                        "\nHigh Score: " + sharedPreferences.getInt("compareHighScore", 0) +
                        "\nPlay Again?")
                .setPositiveButton("Yes", (dialog, which) -> restartGame())
                .setNegativeButton("No", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void restartGame() {
        score = 0;
        combo = 0;
        questionCount = 0;
        updateScore();
        generateQuestion();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove any pending callbacks when activity is destroyed
        handler.removeCallbacksAndMessages(null);
    }
}
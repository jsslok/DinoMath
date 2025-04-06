package com.example.dinomath;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class OrderGameActivity extends AppCompatActivity {
    private TextView question, scoreText, comboText, feedback;
    private Button[] numberButtons = new Button[5];
    private TextView[] answerBoxes = new TextView[5];
    private Button undoBtn, submitBtn, exitBtn;
    private int score = 0, combo = 0, questionCount = 0;
    private List<Integer> currentNumbers = new ArrayList<>();
    private List<Integer> userOrder = new ArrayList<>();
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_game_activity);

        // Initialize views
        question = findViewById(R.id.question);
        scoreText = findViewById(R.id.score);
        comboText = findViewById(R.id.combo);
        feedback = findViewById(R.id.feedback);
        exitBtn = findViewById(R.id.exitBtn);
        undoBtn = findViewById(R.id.undoBtn);
        submitBtn = findViewById(R.id.submitBtn);

        // Initialize number buttons
        numberButtons[0] = findViewById(R.id.num1);
        numberButtons[1] = findViewById(R.id.num2);
        numberButtons[2] = findViewById(R.id.num3);
        numberButtons[3] = findViewById(R.id.num4);
        numberButtons[4] = findViewById(R.id.num5);

        // Initialize answer boxes
        answerBoxes[0] = findViewById(R.id.answerBox1);
        answerBoxes[1] = findViewById(R.id.answerBox2);
        answerBoxes[2] = findViewById(R.id.answerBox3);
        answerBoxes[3] = findViewById(R.id.answerBox4);
        answerBoxes[4] = findViewById(R.id.answerBox5);

        sharedPreferences = getSharedPreferences("DinoMathScores", MODE_PRIVATE);
        generateQuestion();

        // Set click listeners for number buttons
        for (int i = 0; i < numberButtons.length; i++) {
            final int index = i;
            numberButtons[i].setOnClickListener(v -> addToAnswer(index));
        }

        undoBtn.setOnClickListener(v -> undoLast());
        submitBtn.setOnClickListener(v -> checkAnswer());
        exitBtn.setOnClickListener(v -> finish());
    }

    private void generateQuestion() {
        if (questionCount >= 10) {
            endRound();
            return;
        }

        // Generate 5 unique random numbers
        currentNumbers.clear();
        userOrder.clear();
        Random rand = new Random();
        while (currentNumbers.size() < 5) {
            int num = rand.nextInt(100);
            if (!currentNumbers.contains(num)) {
                currentNumbers.add(num);
            }
        }

        // Set numbers to buttons
        for (int i = 0; i < numberButtons.length; i++) {
            numberButtons[i].setText(String.valueOf(currentNumbers.get(i)));
            numberButtons[i].setVisibility(View.VISIBLE);
        }

        // Clear answer boxes
        for (TextView box : answerBoxes) {
            box.setText("");
        }

        question.setText("Arrange in ascending order:");
        feedback.setText("");
        questionCount++;
    }

    private void addToAnswer(int numberIndex) {
        for (TextView box : answerBoxes) {
            if (box.getText().toString().isEmpty()) {
                int num = Integer.parseInt(numberButtons[numberIndex].getText().toString());
                box.setText(String.valueOf(num));
                userOrder.add(num);
                numberButtons[numberIndex].setVisibility(View.INVISIBLE);
                break;
            }
        }
    }

    private void undoLast() {
        for (int i = answerBoxes.length - 1; i >= 0; i--) {
            if (!answerBoxes[i].getText().toString().isEmpty()) {
                int num = Integer.parseInt(answerBoxes[i].getText().toString());
                answerBoxes[i].setText("");
                userOrder.remove(userOrder.size() - 1);

                // Find and show the corresponding number button
                for (Button btn : numberButtons) {
                    if (btn.getText().toString().equals(String.valueOf(num))) {
                        btn.setVisibility(View.VISIBLE);
                        break;
                    }
                }
                break;
            }
        }
    }

    private void checkAnswer() {
        if (userOrder.size() < 5) {
            feedback.setText("Please select all numbers!");
            feedback.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            return;
        }

        // Check if numbers are in ascending order
        boolean isCorrect = true;
        for (int i = 0; i < userOrder.size() - 1; i++) {
            if (userOrder.get(i) > userOrder.get(i + 1)) {
                isCorrect = false;
                break;
            }
        }

        if (isCorrect) {
            score += 10 + (combo * 2); // Combo bonus increases score
            combo++;
            feedback.setText("Correct! 🎉");
            feedback.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            combo = 0;
            feedback.setText("Wrong order! ❌");
            feedback.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }

        updateScore();

        // Delay before next question
        new android.os.Handler().postDelayed(() -> generateQuestion(), 1500);
    }

    private void updateScore() {
        scoreText.setText("Score: " + score);
        comboText.setText("Combo: " + combo);
    }

    private void endRound() {
        // Save high score to shared preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int currentHighScore = sharedPreferences.getInt("orderHighScore", 0);

        if (score > currentHighScore) {
            editor.putInt("orderHighScore", score);
            // Also save to a general high score if you want to show it on homepage
            editor.putInt("overallHighScore", score);
        }
        editor.apply();

        // Ask to play again
        new android.app.AlertDialog.Builder(this)
                .setTitle("Round Over!")
                .setMessage("Your Score: " + score +
                        "\nHigh Score: " + sharedPreferences.getInt("orderHighScore", 0) +
                        "\nPlay Again?")
                .setPositiveButton("Yes", (dialog, which) -> restartGame())
                .setNegativeButton("No", (dialog, which) -> {
                    // Return the high score to homepage
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("highScore", sharedPreferences.getInt("orderHighScore", 0));
                    setResult(RESULT_OK, returnIntent);
                    finish();
                })
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
}
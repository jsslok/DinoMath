package com.example.dinomath;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ComposeGameActivity extends AppCompatActivity {
    private TextView scoreText, comboText, targetNumber, instructionText, feedback;
    private Button[] numberButtons = new Button[5];
    private TextView[] answerBoxes = new TextView[2];
    private Button undoBtn, submitBtn, exitBtn;
    private int score = 0, combo = 0, questionCount = 0;
    private int targetValue;
    private List<Integer> currentNumbers = new ArrayList<>();
    private List<Integer> selectedNumbers = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compose_game_activity);


        scoreText = findViewById(R.id.scoreText);
        comboText = findViewById(R.id.comboText);
        targetNumber = findViewById(R.id.targetNumber);
        instructionText = findViewById(R.id.instructionText);
        feedback = findViewById(R.id.feedback);
        exitBtn = findViewById(R.id.exitBtn);
        undoBtn = findViewById(R.id.undoBtn);
        submitBtn = findViewById(R.id.submitBtn);


        numberButtons[0] = findViewById(R.id.btnOption1);
        numberButtons[1] = findViewById(R.id.btnOption2);
        numberButtons[2] = findViewById(R.id.btnOption3);
        numberButtons[3] = findViewById(R.id.btnOption4);
        numberButtons[4] = findViewById(R.id.btnOption5);


        answerBoxes[0] = findViewById(R.id.answerBox1);
        answerBoxes[1] = findViewById(R.id.answerBox2);

        sharedPreferences = getSharedPreferences("DinoMathScores", MODE_PRIVATE);
        generateNewQuestion();


        for (int i = 0; i < numberButtons.length; i++) {
            final int index = i;
            numberButtons[i].setOnClickListener(v -> selectNumber(index));
        }

        undoBtn.setOnClickListener(v -> undoSelection());
        submitBtn.setOnClickListener(v -> checkAnswer());
        exitBtn.setOnClickListener(v -> finish());
    }

    private void generateNewQuestion() {
        if (questionCount >= 10) {
            endRound();
            return;
        }


        selectedNumbers.clear();
        answerBoxes[0].setText("");
        answerBoxes[1].setText("");
        feedback.setText("");


        Random rand = new Random();
        targetValue = 30 + rand.nextInt(71);
        targetNumber.setText("Target: " + targetValue);


        currentNumbers.clear();
        int firstNum = 10 + rand.nextInt(targetValue - 20);
        int secondNum = targetValue - firstNum;

        currentNumbers.add(firstNum);
        currentNumbers.add(secondNum);


        while (currentNumbers.size() < 5) {
            int num = 10 + rand.nextInt(91);
            boolean valid = true;
            for (int existing : currentNumbers) {
                if (existing + num == targetValue) {
                    valid = false;
                    break;
                }
            }
            if (valid) {
                currentNumbers.add(num);
            }
        }


        Collections.shuffle(currentNumbers);

        // Assign numbers to buttons
        for (int i = 0; i < numberButtons.length; i++) {
            numberButtons[i].setText(String.valueOf(currentNumbers.get(i)));
            numberButtons[i].setEnabled(true);
            numberButtons[i].setAlpha(1f);
        }

        questionCount++;
    }

    private void selectNumber(int index) {
        if (selectedNumbers.size() >= 2) return;

        int num = Integer.parseInt(numberButtons[index].getText().toString());
        selectedNumbers.add(num);
        answerBoxes[selectedNumbers.size() - 1].setText(String.valueOf(num));
        numberButtons[index].setEnabled(false);
        numberButtons[index].setAlpha(0.5f);
    }

    private void undoSelection() {
        if (selectedNumbers.isEmpty()) return;

        int lastNum = selectedNumbers.remove(selectedNumbers.size() - 1);
        answerBoxes[selectedNumbers.size()].setText("");


        for (Button btn : numberButtons) {
            if (btn.getText().toString().equals(String.valueOf(lastNum)) ){
                btn.setEnabled(true);
                btn.setAlpha(1f);
                break;
            }
        }
    }

    private void checkAnswer() {
        if (selectedNumbers.size() < 2) {
            feedback.setText("Please select two numbers!");
            feedback.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            return;
        }

        int sum = selectedNumbers.get(0) + selectedNumbers.get(1);
        if (sum == targetValue) {
            score += 10 + (combo * 2);
            combo++;
            feedback.setText("Correct! 🎉");
            feedback.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            combo = 0;
            feedback.setText("Wrong! ❌");
            feedback.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }

        updateScore();

        // Delay before next question
        handler.postDelayed(this::generateNewQuestion, 1500);
    }

    private void updateScore() {
        scoreText.setText("Score: " + score);
        comboText.setText("Combo: " + combo);
    }

    private void endRound() {
        int highScore = sharedPreferences.getInt("composeHighScore", 0);
        if (score > highScore) {
            sharedPreferences.edit().putInt("composeHighScore", score).apply();
        }

        new android.app.AlertDialog.Builder(this)
                .setTitle("Round Over!")
                .setMessage("Your Score: " + score +
                        "\nHigh Score: " + sharedPreferences.getInt("composeHighScore", 0) +
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
        generateNewQuestion();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
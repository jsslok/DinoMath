package com.example.dinomath;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private TextView compareHighScore, orderHighScore, composeHighScore;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("DinoMathScores", MODE_PRIVATE);

        compareHighScore = findViewById(R.id.compareHighScore);
        orderHighScore = findViewById(R.id.orderHighScore);
        composeHighScore = findViewById(R.id.composeHighScore);

        updateHighScores();

        Button compareButton = findViewById(R.id.compareButton);
        Button orderButton = findViewById(R.id.orderButton);
        Button composeButton = findViewById(R.id.composeButton);

        compareButton.setOnClickListener(v ->
                startActivityForResult(new Intent(this, CompareGameActivity.class), 1));

        orderButton.setOnClickListener(v ->
                startActivityForResult(new Intent(this, OrderGameActivity.class), 2));

        composeButton.setOnClickListener(v ->
                startActivityForResult(new Intent(this, ComposeGameActivity.class), 3));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            updateHighScores();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateHighScores();
    }

    private void updateHighScores() {
        int compareScore = sharedPreferences.getInt("compareHighScore", 0);
        int orderScore = sharedPreferences.getInt("orderHighScore", 0);
        int composeScore = sharedPreferences.getInt("composeHighScore", 0);

        compareHighScore.setText("High Score: " + compareScore);
        orderHighScore.setText("High Score: " + orderScore);
        composeHighScore.setText("High Score: " + composeScore);
    }
}
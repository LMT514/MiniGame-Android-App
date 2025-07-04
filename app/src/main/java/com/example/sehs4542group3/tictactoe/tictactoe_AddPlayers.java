package com.example.sehs4542group3.tictactoe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sehs4542group3.DatabaseHelper;
import com.example.sehs4542group3.R;

public class tictactoe_AddPlayers extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tictactoe_add_players);
        dbHelper = new DatabaseHelper(this);
        username = getIntent().getStringExtra("username");
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        final EditText playerOne = findViewById(R.id.PlayerOneName);
        final EditText PlayerTwo = findViewById(R.id.PlayerTwoName);
        final Button StartGameButton = findViewById(R.id.StartGameButton);



        StartGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String getPlayerOneName = playerOne.getText().toString();
                final String getPlayerTwoName = PlayerTwo.getText().toString();

                if(getPlayerOneName.isEmpty() || getPlayerTwoName.isEmpty()){
                    Toast.makeText(tictactoe_AddPlayers.this, "Please enter your name", Toast.LENGTH_SHORT).show();
                }
                else{

                    Intent intent = new Intent(tictactoe_AddPlayers.this, tictactoeActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("PlayerOne", getPlayerOneName);
                    intent.putExtra("PlayerTwo", getPlayerTwoName);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }
}
















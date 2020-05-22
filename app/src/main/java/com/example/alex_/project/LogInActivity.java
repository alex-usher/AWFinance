package com.example.alex_.project;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LogInActivity extends AppCompatActivity {

    EditText pinField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
    }

    public void LogIn(View view){
        pinField = findViewById(R.id.PinEntry);

        String pinEntered = Crypt.Hash512(pinField.getText().toString());
        String pinStored = FileHandler.readFile(this, "AppEntry");

        if(pinEntered.equals(pinStored)){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "PIN incorrect.", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}

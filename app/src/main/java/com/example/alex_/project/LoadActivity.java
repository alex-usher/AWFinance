package com.example.alex_.project;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class LoadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        DBHelper dbHelper = new DBHelper(this);

        if(dbHelper.testConnection()) {
            checkPINFile();
        }
    }

    public void checkPINFile(){
        String string = FileHandler.readFile(this,"AppEntry");

        if(string != null){
            Intent intent = new Intent(this, LogInActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, SetPinActivity.class);
            startActivity(intent);
        }
    }
}

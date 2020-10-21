package com.example.alex_.project;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class LoadActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_load);

    DBHelper dbHelper = new DBHelper(this);

    if (dbHelper.testConnection()) {
      checkPINFile();
    }
  }

  // helper method to direct the application to the correct activity based on if a PIN exists or
  // not
  private void checkPINFile() {
    String string = FileHandler.readFile(this, FileHandler.DEFAULT_FILENAME);

    if (string != null) {
      Intent intent = new Intent(this, LogInActivity.class);
      startActivity(intent);
    } else {
      Intent intent = new Intent(this, SetPinActivity.class);
      startActivity(intent);
    }
  }
}

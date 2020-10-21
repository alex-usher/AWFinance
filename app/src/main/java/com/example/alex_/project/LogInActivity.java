package com.example.alex_.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LogInActivity extends AppCompatActivity {

  EditText pinField;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_log_in);
  }

  // an on click function for the log in button, switches to the main application if
  // the PIN is entered correctly.
  public void LogIn(View view) {
    pinField = findViewById(R.id.PinEntry);

    String pinEntered = Crypt.hash512(pinField.getText().toString());

    if (FileHandler.checkContents(this, pinEntered, FileHandler.DEFAULT_FILENAME)) {
      startActivity(new Intent(this, MainActivity.class));
    } else {
      Toast.makeText(getApplicationContext(), "PIN incorrect.", Toast.LENGTH_SHORT).show();
    }
  }
}

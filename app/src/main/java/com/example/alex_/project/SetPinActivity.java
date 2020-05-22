package com.example.alex_.project;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SetPinActivity extends AppCompatActivity {

    EditText pinEntry;
    EditText pinConfirm;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pin);
    }

    public void createPin(View view){
        pinEntry = findViewById(R.id.PinEntry);
        pinConfirm = findViewById(R.id.PinConfirm);

        String pin = Crypt.Hash512(pinEntry.getText().toString());
        String checkPin = Crypt.Hash512(pinConfirm.getText().toString());

        if(pin.equals(checkPin) && pin != null && checkPin != null){
            FileHandler.writeToFile(this, "AppEntry", pin);
            mainActivity();
        } else if(!(pin.equals(checkPin))){
            Toast toast = Toast.makeText(getApplicationContext(), "PINs do not match", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "There was an error. Try Again", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void mainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}

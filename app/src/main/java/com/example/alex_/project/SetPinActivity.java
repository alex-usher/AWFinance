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

    // on click function that sets the PIN if the fields in the form are entered correctly.
    public void createPin(View view){
        pinEntry = findViewById(R.id.PinEntry);
        pinConfirm = findViewById(R.id.PinConfirm);

        String pin = Crypt.hash512(pinEntry.getText().toString());
        String checkPin = Crypt.hash512(pinConfirm.getText().toString());

        if(!pin.isEmpty() && !checkPin.isEmpty() && pin.equals(checkPin)){
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

    private void mainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}

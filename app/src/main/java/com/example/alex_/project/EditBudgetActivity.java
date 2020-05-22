package com.example.alex_.project;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

public class EditBudgetActivity extends AppCompatActivity {

    private Budget budget;
    private Spinner typeSpinner;
    private EditText colorBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_budget);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        typeSpinner = findViewById(R.id.type);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.budgetTypes,
                android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(arrayAdapter);

        final Context context = this;

        colorBox = findViewById(R.id.colour);
        colorBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorPickerDialog.Builder(context, AlertDialog.BUTTON_NEUTRAL)
                        .setTitle("ColorPicker Dialog")
                        .setPreferenceName("ColorPickerDialog")
                        .setPositiveButton(getString(R.string.confirm),
                                new ColorEnvelopeListener() {
                                    @Override
                                    public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                        String hex = "#" + envelope.getHexCode();
                                        colorBox.setText(hex);
                                        colorBox.clearFocus();
                                    }
                                })
                        .setNegativeButton(getString(R.string.cancel),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        dialogInterface.dismiss();
                                        colorBox.clearFocus();
                                    }
                                })
                        .attachAlphaSlideBar(true)
                        .attachBrightnessSlideBar(true)
                        .show();
            }
        });

        Button submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit(view);
            }
        });

        Button delete = findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete(view);
            }
        });

        budget = BudgetsFragment.getBudget();
        updateFields();
    }

    private void updateFields() {
        EditText name = findViewById(R.id.name);
        EditText amount = findViewById(R.id.amount);

        name.setText(budget.getName());
        amount.setText(String.format("%s", budget.getAmount()));
        colorBox.setText(budget.getColour());
        // subtract 1 to convert from 1-4 to 0-3 to fit within bounds of spinner
        typeSpinner.setSelection(BudgetType.typeToInt(budget.getType()) - 1);
    }

    public void submit(View view) {
        try {
            EditText name = findViewById(R.id.name);
            EditText amount = findViewById(R.id.amount);

            String nameStr = name.getText().toString();
            float amountFloat = Float.parseFloat(amount.getText().toString());
            String colour = colorBox.getText().toString();

            if (nameStr.isEmpty() || amountFloat == 0.0f || colour.isEmpty()) {
                Toast.makeText(this, "Please complete the form.", Toast.LENGTH_SHORT).show();
            } else {
                DBHelper dbHelper = new DBHelper(this);

                budget = new Budget(budget.getID(), nameStr, amountFloat,
                        BudgetType.stringToBudgetType(typeSpinner.getSelectedItem().toString()),
                        budget.getDateCreated(), colour);


                dbHelper.updateBudget(budget);

                Toast.makeText(this, "Budget updated.", Toast.LENGTH_SHORT).show();

                changeActivity();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void delete(View view) {
        try {
            DBHelper dbHelper = new DBHelper(this);
            dbHelper.deleteBudget(budget);

            Toast.makeText(this, "Budget deleted.", Toast.LENGTH_SHORT).show();

            changeActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

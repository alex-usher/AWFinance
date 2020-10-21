package com.example.alex_.project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

    // create the dropdown for the budget type
    typeSpinner = findViewById(R.id.type);
    ArrayAdapter<CharSequence> arrayAdapter =
        ArrayAdapter.createFromResource(
            this, R.array.budgetTypes, android.R.layout.simple_spinner_item);
    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    typeSpinner.setAdapter(arrayAdapter);

    final Context context = this;

    // create the colour selector box
    colorBox = findViewById(R.id.colour);
    colorBox.setOnClickListener(
        v ->
            new ColorPickerDialog.Builder(context, AlertDialog.BUTTON_NEUTRAL)
                .setTitle("ColorPicker Dialog")
                .setPreferenceName("ColorPickerDialog")
                .setPositiveButton(
                    getString(R.string.confirm),
                    new ColorEnvelopeListener() {
                      @Override
                      public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                        String hex = "#" + envelope.getHexCode();
                        colorBox.setText(hex);
                        colorBox.clearFocus();
                      }
                    })
                .setNegativeButton(
                    getString(R.string.cancel),
                    (dialogInterface, which) -> {
                      dialogInterface.dismiss();
                      colorBox.clearFocus();
                    })
                .attachAlphaSlideBar(true)
                .attachBrightnessSlideBar(true)
                .show());

    // set the OnClickListener for the submit button
    Button submit = findViewById(R.id.submit);
    submit.setOnClickListener(this::submit);

    // set the OnClickListener for the delete button
    Button delete = findViewById(R.id.delete);
    delete.setOnClickListener(this::delete);

    budget = BudgetsFragment.getBudget();
    updateFields();
  }

  // a helper method to set the fields of the form to those of the current budget
  private void updateFields() {
    EditText name = findViewById(R.id.name);
    EditText amount = findViewById(R.id.amount);

    name.setText(budget.getName());
    amount.setText(String.format("%s", budget.getAmount()));
    colorBox.setText(budget.getColour());
    // subtract 1 to convert from 1-4 to 0-3 to fit within bounds of spinner
    typeSpinner.setSelection(BudgetType.typeToInt(budget.getType()) - 1);
  }

  // the on click function for the submit button
  private void submit(View view) {
    try {
      // obtain the fields from the form
      EditText name = findViewById(R.id.name);
      EditText amount = findViewById(R.id.amount);
      String nameStr = name.getText().toString();
      float amountFloat = Float.parseFloat(amount.getText().toString());
      String colour = colorBox.getText().toString();

      // if the form is incomplete, inform the user of such via a Toast
      if (nameStr.isEmpty() || amountFloat == 0.0f || colour.isEmpty()) {
        Toast.makeText(this, "Please complete the form.", Toast.LENGTH_SHORT).show();
      } else {
        DBHelper dbHelper = new DBHelper(this);

        budget =
            new Budget(
                budget.getID(),
                nameStr,
                amountFloat,
                BudgetType.stringToBudgetType(typeSpinner.getSelectedItem().toString()),
                budget.getDateCreated(),
                colour);

        dbHelper.updateBudget(budget);

        Toast.makeText(this, "Budget updated.", Toast.LENGTH_SHORT).show();

        changeActivity();
      }
    } catch (NullPointerException e) {
      e.printStackTrace();
    }
  }

  // on click function for the delete button
  private void delete(View view) {
    DBHelper dbHelper = new DBHelper(this);
    dbHelper.deleteBudget(budget);

    Toast.makeText(this, "Budget deleted.", Toast.LENGTH_SHORT).show();

    changeActivity();
  }

  // helper method to reduce duplication
  // changes the activity back to main on call
  private void changeActivity() {
    Intent intent = new Intent(this, MainActivity.class);
    startActivity(intent);
  }
}

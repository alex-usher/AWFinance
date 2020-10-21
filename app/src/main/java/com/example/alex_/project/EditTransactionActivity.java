package com.example.alex_.project;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Set;

public class EditTransactionActivity extends AppCompatActivity {

  private Transaction transaction;
  private EditText dateBox;

  private final Calendar calendar = Calendar.getInstance();
  private Set<Budget> budgets;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_transaction);

    Toolbar toolbar = findViewById(R.id.toolbar);
    toolbar.setTitle("");
    setSupportActionBar(toolbar);

    // setup the drop down for if the transaction is a deposit / withdrawal
    final Spinner spinner = findViewById(R.id.type);
    final ArrayAdapter<CharSequence> arrayAdapter =
        ArrayAdapter.createFromResource(
            this, R.array.typeArray, android.R.layout.simple_spinner_item);
    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    spinner.setAdapter(arrayAdapter);
    int position = arrayAdapter.getPosition("Deposit"); // setting initial value to 1
    spinner.setSelection(position);

    // set up of the date selection box for the date the transaction took place
    dateBox = findViewById(R.id.date);
    final DatePickerDialog.OnDateSetListener date =
        (view, year, month, dayOfMonth) -> {
          calendar.set(year, month, dayOfMonth);

          final SimpleDateFormat sdf =
              new SimpleDateFormat(DBHelper.DATE_FORMAT_DISPLAYED, Locale.getDefault());

          dateBox.setText(sdf.format(calendar.getTime()));
          dateBox.clearFocus();
        };

    dateBox.setOnClickListener(
        v ->
            new DatePickerDialog(
                    getParent(),
                    date,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH))
                .show());

    // set up the drop down for the different budgets the transaction can be classed under
    final Spinner budgetSpinner = findViewById(R.id.budget);

    DBHelper dbHelper = new DBHelper(this);
    budgets = dbHelper.getAllBudgets();
    ArrayList<String> budgetList = new ArrayList<>();

    for (Budget budget : budgets) {
      budgetList.add(budget.getName());
    }

    final ArrayAdapter<String> arrayAdapter1 =
        new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, budgetList);
    arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    budgetSpinner.setAdapter(arrayAdapter1);

    // set the OnClickListener for the submit button
    Button submit = findViewById(R.id.submit);
    submit.setOnClickListener(this::submit);

    // set the OnClickListener for the delete button
    Button delete = findViewById(R.id.delete);
    delete.setOnClickListener(this::delete);

    transaction = RecentTransactionFragment.getTransaction();
    updateFields();
  }

  // private helper method to set the form fields to the values of the transaction
  private void updateFields() {
    EditText paidTo = findViewById(R.id.paidTo);
    EditText value = findViewById(R.id.value);
    EditText description = findViewById(R.id.description);
    Spinner type = findViewById(R.id.type);
    Spinner budget = findViewById(R.id.budget);

    paidTo.setText(transaction.getPaidTo());
    value.setText(
        String.format(
            "%s",
            transaction.getValue() < 0 ? -1 * transaction.getValue() : transaction.getValue()));
    description.setText(transaction.getDesc());

    if (transaction.isDeposit()) {
      type.setSelection(0);
    } else {
      type.setSelection(1);
    }

    dateBox.setText(transaction.dateToString());

    int i = findInBudgetList(transaction.getBudget());
    if (i == -1) {
      budget.setSelection(0);
    } else {
      budget.setSelection(i);
    }
  }

  // helper method to find a particular budget in the list based on its id
  private int findInBudgetList(int budgetId) {
    int i = 0;
    for (Budget budget : budgets) {
      if (budget.getID() == budgetId) {
        return i;
      }
      i++;
    }

    return -1;
  }

  // on click function for the submit button
  private void submit(View view) {
    try {
      // obtain fields from the form
      EditText paidTo = findViewById(R.id.paidTo);
      EditText value = findViewById(R.id.value);
      EditText description = findViewById(R.id.description);
      Spinner type = findViewById(R.id.type);
      dateBox = findViewById(R.id.date);
      Spinner budget = findViewById(R.id.budget);

      String paidToStr = paidTo.getText().toString();
      float valueDec = Float.parseFloat(value.getText().toString());
      String descStr = description.getText().toString();
      String typeStr = type.getSelectedItem().toString();
      String dateStr = dateBox.getText().toString();
      String budgetStr =
          (budget.getSelectedItem() != null ? budget.getSelectedItem().toString() : "");

      // if the form is incomplete we inform the user of such via a toast
      if (paidToStr.isEmpty()
          || valueDec == 0.0f
          || descStr.isEmpty()
          || typeStr.isEmpty()
          || dateStr.isEmpty()) {
        Toast.makeText(this, "Please complete the form.", Toast.LENGTH_SHORT).show();
      } else {
        DBHelper dbHelper = new DBHelper(this);

        int budgetId = dbHelper.getBudgetID(budgetStr);
        transaction =
            new Transaction(
                transaction.getID(),
                paidToStr,
                Transaction.valueParser(valueDec, typeStr),
                descStr,
                dateStr,
                transaction.getDateCreated(),
                budgetId);

        dbHelper.updateTransaction(transaction);

        Toast.makeText(this, "Transaction updated.", Toast.LENGTH_SHORT).show();

        changeActivity();
      }
    } catch (NullPointerException e) {
      e.printStackTrace();
    }
  }

  // on click function of the delete button
  private void delete(View view) {
    DBHelper dbHelper = new DBHelper(this);
    dbHelper.deleteTransaction(transaction);

    Toast.makeText(this, "Transaction deleted.", Toast.LENGTH_SHORT).show();

    changeActivity();
  }

  // helper method to change the activity back to main on call
  private void changeActivity() {
    Intent intent = new Intent(this, MainActivity.class);
    startActivity(intent);
  }
}

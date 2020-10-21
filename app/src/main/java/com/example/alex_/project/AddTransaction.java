package com.example.alex_.project;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class AddTransaction extends Fragment {

  private EditText dateBox;

  private final Calendar calendar = Calendar.getInstance();

  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View v = inflater.inflate(R.layout.fragment_add_transaction, container, false);

    // handling spinner
    final Spinner spinner = v.findViewById(R.id.type);
    final ArrayAdapter<CharSequence> arrayAdapter =
        ArrayAdapter.createFromResource(
            getContext(), R.array.typeArray, android.R.layout.simple_spinner_item);
    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    spinner.setAdapter(arrayAdapter);
    int position = arrayAdapter.getPosition("Deposit"); // setting initial value to 1
    spinner.setSelection(position);

    // set up the date box and define the onDateSetListener
    dateBox = v.findViewById(R.id.date);
    final DatePickerDialog.OnDateSetListener date =
        (view, year, month, dayOfMonth) -> {
          calendar.set(year, month, dayOfMonth);

          final SimpleDateFormat sdf =
              new SimpleDateFormat(DBHelper.DATE_FORMAT_DISPLAYED, Locale.getDefault());

          dateBox.setText(sdf.format(calendar.getTime()));
          dateBox.clearFocus();
        };

    // set the onClickListener for the date box
    dateBox.setOnClickListener(
        v1 ->
            new DatePickerDialog(
                    getContext(),
                    date,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH))
                .show());

    final Spinner budgetSpinner = v.findViewById(R.id.budget);
    DBHelper dbHelper = new DBHelper(getActivity());

    Set<Budget> budgets = dbHelper.getAllBudgets();
    List<String> budgetList = new ArrayList<>();

    for (Budget budget : budgets) {
      budgetList.add(budget.getName());
    }

    // set up drop down for budgets
    final ArrayAdapter<String> arrayAdapter1 =
        new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, budgetList);
    arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    budgetSpinner.setAdapter(arrayAdapter1);
    Button submit = v.findViewById(R.id.submit);
    submit.setOnClickListener(this::submit);

    return v;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  // on click function for the submit button
  private void submit(View view) {
    try {
      // read information from the transaction form
      final EditText paidTo = getActivity().findViewById(R.id.paidTo);
      final EditText value = getActivity().findViewById(R.id.value);
      final EditText description = getActivity().findViewById(R.id.description);
      final Spinner type = getActivity().findViewById(R.id.type);
      dateBox = getActivity().findViewById(R.id.date);
      final Spinner budget = getActivity().findViewById(R.id.budget);

      String paidToStr = paidTo.getText().toString();
      float valueDec = Float.parseFloat(value.getText().toString());
      String descStr = description.getText().toString();
      String typeStr = type.getSelectedItem().toString();
      String dateStr = dateBox.getText().toString();
      String budgetStr =
          (budget.getSelectedItem() != null ? budget.getSelectedItem().toString() : "");

      // if the form is not complete make a toast informing the user of such
      if ((paidToStr.isEmpty())
          || (valueDec == 0.0f)
          || (descStr.isEmpty())
          || (typeStr.isEmpty())
          || (dateStr.isEmpty())) {
        Toast toast = Toast.makeText(getActivity(), "Please Complete the Form", Toast.LENGTH_SHORT);
        toast.show();
      } else {
        DBHelper dbHelper = new DBHelper(getContext());

        int budgetId = dbHelper.getBudgetID(budgetStr);

        Transaction transaction =
            new Transaction(
                Transaction.valueParser(valueDec, typeStr),
                paidToStr,
                descStr,
                dateStr,
                new Timestamp(System.currentTimeMillis()),
                budgetId);

        dbHelper.addTransaction(transaction);

        Toast toast = Toast.makeText(getActivity(), "Transaction Created", Toast.LENGTH_SHORT);
        toast.show();

        getActivity()
            .getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, new RecentTransactionFragment())
            .commit();
      }
    } catch (NullPointerException e) {
      e.printStackTrace();
    }
  }
}

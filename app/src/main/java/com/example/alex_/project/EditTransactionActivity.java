package com.example.alex_.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EditTransactionActivity extends AppCompatActivity {

	private Transaction transaction;
	private EditText dateBox;

	private final Calendar calendar = Calendar.getInstance();
	private List<Budget> budgets;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_transaction);

		Toolbar toolbar = findViewById(R.id.toolbar);
		toolbar.setTitle("");
		setSupportActionBar(toolbar);

		final Spinner spinner = findViewById(R.id.type);
		final ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.typeArray, android.R.layout.simple_spinner_item);
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner.setAdapter(arrayAdapter);
		int position = arrayAdapter.getPosition("Deposit");//setting initial value to 1
		spinner.setSelection(position);

		dateBox = findViewById(R.id.date);
		final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
				calendar.set(year, month, dayOfMonth);

				final SimpleDateFormat sdf = new SimpleDateFormat(DBHelper.DATE_FORMAT_DISPLAYED);

				dateBox.setText(sdf.format(calendar.getTime()));
				dateBox.clearFocus();
			}
		};

		dateBox.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new DatePickerDialog(getParent(), date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH)).show();
			}
		});

		final Spinner budgetSpinner = findViewById(R.id.budget);

		budgets = getBudgets();
		ArrayList<String> budgetList = new ArrayList<>();

		for (int i = 0; i < budgets.size(); i++) {
			Budget budget = budgets.get(i);
			budgetList.add(budget.getName());
		}

		final ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, budgetList);
		arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		budgetSpinner.setAdapter(arrayAdapter1);

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

		transaction = RecentTransactionFragment.getTransaction();
		updateFields();
	}

	private List<Budget> getBudgets() {
		DBHelper dbHelper = new DBHelper(this);
		return dbHelper.getAllBudgets();
	}

	private void updateFields() {
		EditText paidTo = findViewById(R.id.paidTo);
		EditText value = findViewById(R.id.value);
		EditText description = findViewById(R.id.description);
		Spinner type = findViewById(R.id.type);
		Spinner budget = findViewById(R.id.budget);

		paidTo.setText(transaction.getPaidTo());
		value.setText(String.format("%s", transaction.getValue() < 0 ? -1 * transaction.getValue() : transaction.getValue()));
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

	private int findInBudgetList(int budgetId) {
		for (int i = 0; i < budgets.size(); i++) {
			if (budgets.get(i).getID() == budgetId) {
				return i;
			}
		}

		return -1;
	}

	public void submit(View view) {
		try {
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
			String budgetStr = (budget.getSelectedItem() != null ? budget.getSelectedItem().toString() : "");


			if (paidToStr.isEmpty() || valueDec == 0.0f || descStr.isEmpty() || typeStr.isEmpty() || dateStr.isEmpty()) {
				Toast.makeText(this, "Please complete the form.", Toast.LENGTH_SHORT).show();
			} else {
				DBHelper dbHelper = new DBHelper(this);

				int budgetId = dbHelper.getBudgetID(budgetStr);
				transaction = new Transaction(transaction.getID(), paidToStr, Transaction.valueParser(valueDec, typeStr), descStr,
					dateStr, transaction.getDateCreated(), budgetId);

				dbHelper.updateTransaction(transaction);

				Toast.makeText(this, "Transaction updated.", Toast.LENGTH_SHORT).show();

				changeActivity();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void delete(View view) {
		try {
			DBHelper dbHelper = new DBHelper(this);
			dbHelper.deleteTransaction(transaction);

			Toast.makeText(this, "Transaction deleted.", Toast.LENGTH_SHORT).show();

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

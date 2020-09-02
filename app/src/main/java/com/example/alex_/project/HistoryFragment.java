package com.example.alex_.project;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.sql.ClientInfoStatus;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HistoryFragment extends Fragment {

	private EditText startDate;
	private EditText endDate;

	private final Calendar startCalendar = Calendar.getInstance();
	private final Calendar endCalendar = Calendar.getInstance();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
													 Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_history, container, false);

		startDate = v.findViewById(R.id.startDate);
		endDate = v.findViewById(R.id.endDate);

		final DatePickerDialog.OnDateSetListener startDateDialog = (view, year, month, dayOfMonth) -> {
			startCalendar.set(year, month, dayOfMonth);

			final SimpleDateFormat sdf = new SimpleDateFormat(DBHelper.DATE_FORMAT_DISPLAYED, Locale.getDefault());

			startDate.setText(sdf.format(startCalendar.getTime()));
			startDate.clearFocus();
		};

		startDate.setOnClickListener(v1 ->
			new DatePickerDialog(getContext(), startDateDialog, startCalendar.get(Calendar.YEAR),
				startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DAY_OF_MONTH)).show());

		final DatePickerDialog.OnDateSetListener endDateDialog = (view, year, month, dayOfMonth) -> {
			endCalendar.set(year, month, dayOfMonth);

			final SimpleDateFormat sdf = new SimpleDateFormat(DBHelper.DATE_FORMAT_DISPLAYED, Locale.getDefault());

			endDate.setText(sdf.format(endCalendar.getTime()));
			endDate.clearFocus();
		};

		endDate.setOnClickListener(v1 ->
			new DatePickerDialog(getContext(), endDateDialog, endCalendar.get(Calendar.YEAR),
				endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH)).show());

		Button submit = v.findViewById(R.id.submit);

		submit.setOnClickListener(v12 -> {
			displayTransactions(v, v.findViewById(R.id.linearLayout));
		});

		return v;
	}

	private void displayTransactions(View v, LinearLayout linearLayout) {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DBHelper.DATE_FORMAT_DISPLAYED);
			LocalDate start = LocalDate.parse(startDate.getText(), formatter);
			LocalDate end;

			try {
				end = LocalDate.parse(endDate.getText(), formatter);

				if(end.compareTo(start) < 0) {
					Toast.makeText(getContext(), "The end date must be after the start date", Toast.LENGTH_SHORT).show();
					return;
				}

				DBHelper helper = new DBHelper(getContext());
				linearLayout.removeAllViews();

				List<Transaction> transactions = helper.getAllTransactions();

				if(transactions.isEmpty()) {
					Toast.makeText(getContext(), "There are no transactions for this period", Toast.LENGTH_SHORT).show();
					return;
				}

				for (Transaction t : transactions) {
					if (DBHelper.dateIsBetween(t.getDate(), start, end)) {
						linearLayout.addView(DynamicLayoutHandler.generateGrid(getActivity(), t, helper));
					}
				}

			} catch (DateTimeParseException e) {
				Toast.makeText(getContext(), "Please select an end date", Toast.LENGTH_SHORT).show();
			}
		} catch (DateTimeParseException e) {
			Toast.makeText(getContext(), "Please select a start date", Toast.LENGTH_SHORT).show();
		}
	}
}
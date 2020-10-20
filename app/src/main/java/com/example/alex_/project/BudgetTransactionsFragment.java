package com.example.alex_.project;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;


public class BudgetTransactionsFragment extends Fragment {

	private final Budget b;

	public BudgetTransactionsFragment() {
		b = BudgetsFragment.getBudget();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
													 Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_budget_transactions, container, false);

		setTitle(v);
		drawProgressBox(v);
		drawTransactionsBox(v);

		return v;
	}

	private void setTitle(View v) {
		TextView title = v.findViewById(R.id.budgetName);
		title.setText("Transactions for " + b.getName());
	}

	private void drawProgressBox(View v) {
		//get linear layout
		LinearLayout layout = v.findViewById(R.id.progressBarContainer);

		//create grid layout
		GridLayout grid = DynamicLayoutHandler.generateGrid(getContext(), b, new DBHelper(getContext()));
		grid.setBackgroundResource(R.color.appBackgroundDark);

		//create progress bar
		ProgressBar progressBar = DynamicLayoutHandler.generateProgressBar(getActivity(), b, new DBHelper(getContext()));

		GridLayout.LayoutParams progressParams = new GridLayout.LayoutParams();
		progressParams.columnSpec = GridLayout.spec(0, 7, 7f);
		progressParams.rowSpec = GridLayout.spec(0, 1);

		grid.addView(progressBar, progressParams);

		//create text view for %
		TextView percentage = DynamicLayoutHandler.generateTextView(getActivity(), b, new DBHelper(getContext()));

		GridLayout.LayoutParams percentageParams = new GridLayout.LayoutParams();
		percentageParams.columnSpec = GridLayout.spec(7, 1, 1f);
		percentageParams.rowSpec = GridLayout.spec(0, 1);

		grid.addView(percentage, percentageParams);

		// add grid to linear layout
		layout.addView(grid);
	}

	private void drawTransactionsBox(View v) {
		DBHelper dbHelper = new DBHelper(getContext());
		List<Transaction> transactions = dbHelper.getAllTransactionsInBudget(b);

		for(Transaction transaction : transactions) {
			LinearLayout layout = v.findViewById(R.id.transactionBox);

			//create grid layout
			GridLayout grid = DynamicLayoutHandler.generateGrid(getActivity(), transaction, dbHelper);

			//add grid layout to view
			layout.addView(grid);
		}
	}
}

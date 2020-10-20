package com.example.alex_.project;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.List;

public class MainFragment extends Fragment {

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_main, container, false);

		drawProgressBox(v);
		drawTransactionBox(v);

		return v;
	}

	private void drawProgressBox(View v) {
		// draw box containing summary of budgets
		final DBHelper dbHelper = new DBHelper(getContext());

		List<Budget> budgetList = dbHelper.getAllBudgets();

		//linear layout to add items to
		LinearLayout linearLayout = v.findViewById(R.id.progressBarContainer);

		for (int i = 0; i < budgetList.size(); i++) {
			final Budget budget = budgetList.get(i);

			//add grid to linear layout
			linearLayout.addView(DynamicLayoutHandler.createBudgetWithProgress(getContext(), budget, dbHelper));
		}
	}

	private void drawTransactionBox(View v) {
		//draw box containing transactions less than 1 week old
		DBHelper dbHelper = new DBHelper(this.getContext());
		List<Transaction> transactionList = dbHelper.getAllTransactions();

		for (int i = 0; i < transactionList.size(); i++) {
			if (transactionList.get(i).isThisWeek()) {
				final Transaction transaction = transactionList.get(i);

				//find linear layout to add views to
				LinearLayout linearLayout = v.findViewById(R.id.transactionBox);

				//create grid layout
				GridLayout gl = DynamicLayoutHandler.generateGrid(getActivity(), transaction, dbHelper);

				//add grid layout to view
				linearLayout.addView(gl);
			}
		}
	}
}

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

			//create gridlayout
			GridLayout gl = DynamicLayoutHandler.generateGrid(getContext(), getActivity(), budget, dbHelper);

			//create textview for budget name
			TextView tvName = new TextView(this.getActivity());
			tvName.setMinWidth(0);
			tvName.setMinHeight(GridLayoutManager.LayoutParams.MATCH_PARENT);
			tvName.setGravity(Gravity.FILL);
			tvName.setText(budget.getName());
			tvName.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
			tvName.setTextAppearance(R.style.progressBox);

			GridLayout.LayoutParams nameParams = new GridLayout.LayoutParams();
			nameParams.columnSpec = GridLayout.spec(0, 2, 2f);
			nameParams.rowSpec = GridLayout.spec(0, 1);

			gl.addView(tvName, nameParams);

			//create progress bar
			ProgressBar progressBar = DynamicLayoutHandler.generateProgressBar(getActivity(), budget, dbHelper);

			GridLayout.LayoutParams progressParams = new GridLayout.LayoutParams();
			progressParams.columnSpec = GridLayout.spec(2, 5, 5f);
			progressParams.rowSpec = GridLayout.spec(0, 1);

			gl.addView(progressBar, progressParams);

			//create text view for %
			TextView tvPerc = DynamicLayoutHandler.generateTextView(getActivity(), budget, dbHelper);

			GridLayout.LayoutParams percParams = new GridLayout.LayoutParams();
			percParams.columnSpec = GridLayout.spec(7, 1, 1f);
			percParams.rowSpec = GridLayout.spec(0, 1);

			gl.addView(tvPerc, percParams);

			//add grid to linear layout
			linearLayout.addView(gl);
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

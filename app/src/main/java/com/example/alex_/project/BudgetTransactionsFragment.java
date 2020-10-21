package com.example.alex_.project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.Set;

public class BudgetTransactionsFragment extends Fragment {

  private final Budget b;

  public BudgetTransactionsFragment() {
    b = BudgetsFragment.getBudget();
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_budget_transactions, container, false);

    setTitle(v);
    drawProgressBox(v);
    drawTransactionsBox(v);

    return v;
  }

  // Sets the title text of the fragment
  private void setTitle(View v) {
    TextView title = v.findViewById(R.id.budgetName);
    title.setText("Transactions for " + b.getName());
  }

  private void drawProgressBox(View v) {
    // get linear layout
    LinearLayout layout = v.findViewById(R.id.progressBarContainer);

    // create grid layout
    GridLayout grid =
        DynamicLayoutHandler.generateBudgetGrid(getContext(), b, new DBHelper(getContext()));
    grid.setBackgroundResource(R.color.appBackgroundDark);

    // create progress bar
    ProgressBar progressBar =
        DynamicLayoutHandler.generateBudgetProgressBar(
            getActivity(), b, new DBHelper(getContext()));

    GridLayout.LayoutParams progressParams = new GridLayout.LayoutParams();
    progressParams.columnSpec = GridLayout.spec(0, 7, 7f);
    progressParams.rowSpec = GridLayout.spec(0, 1);

    grid.addView(progressBar, progressParams);

    // create text view for %
    TextView percentage =
        DynamicLayoutHandler.generatePercentageTextView(
            getActivity(), b, new DBHelper(getContext()));

    GridLayout.LayoutParams percentageParams = new GridLayout.LayoutParams();
    percentageParams.columnSpec = GridLayout.spec(7, 1, 1f);
    percentageParams.rowSpec = GridLayout.spec(0, 1);

    grid.addView(percentage, percentageParams);

    // add grid to linear layout
    layout.addView(grid);
  }

  private void drawTransactionsBox(View v) {
    DBHelper dbHelper = new DBHelper(getContext());
    Set<Transaction> transactions = dbHelper.getAllTransactionsInBudget(b);

    for (Transaction transaction : transactions) {
      LinearLayout layout = v.findViewById(R.id.transactionBox);

      // create grid layout
      GridLayout grid =
          DynamicLayoutHandler.generateTransactionGrid(getActivity(), transaction, dbHelper);

      // add grid layout to view
      layout.addView(grid);
    }
  }
}

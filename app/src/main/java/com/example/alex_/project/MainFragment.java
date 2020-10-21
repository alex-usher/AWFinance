package com.example.alex_.project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Set;

public class MainFragment extends Fragment {

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_main, container, false);

    drawProgressBox(v);
    drawTransactionBox(v);

    return v;
  }

  // private helper method to show all the budgets and their progress boxes
  private void drawProgressBox(View v) {
    // draw box containing summary of budgets
    final DBHelper dbHelper = new DBHelper(getContext());

    Set<Budget> budgetList = dbHelper.getAllBudgets();

    // linear layout to add items to
    LinearLayout linearLayout = v.findViewById(R.id.progressBarContainer);

    for (Budget budget : budgetList) {
      // add grid to linear layout
      linearLayout.addView(
          DynamicLayoutHandler.createBudgetWithProgress(getContext(), budget, dbHelper));
    }
  }

  // a helper method to show the most recent transactions (all those which occurred in the
  // previous 7 days
  private void drawTransactionBox(View v) {
    // draw box containing transactions less than 1 week old
    DBHelper dbHelper = new DBHelper(this.getContext());
    Set<Transaction> transactionList = dbHelper.getAllTransactions();

    for (Transaction transaction : transactionList) {
      if (transaction.isThisWeek()) {
        // find linear layout to add views to
        LinearLayout linearLayout = v.findViewById(R.id.transactionBox);

        // create grid layout
        GridLayout gl =
            DynamicLayoutHandler.generateTransactionGrid(getActivity(), transaction, dbHelper);

        // add grid layout to view
        linearLayout.addView(gl);
      }
    }
  }
}

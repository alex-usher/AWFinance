package com.example.alex_.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Set;

public class RecentTransactionFragment extends Fragment {

  private static Transaction t;

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_recent_transaction, container, false);
    drawList(v);
    return v;
  }

  private void drawList(View v) {
    DBHelper dbHelper = new DBHelper(this.getActivity());
    Set<Transaction> transactionList = dbHelper.getAllTransactions();

    // get linear layout
    LinearLayout linearLayout = v.findViewById(R.id.linearLayout);

    for (final Transaction transaction : transactionList) {
      // create grid layout
      GridLayout gl = DynamicLayoutHandler.generateTransactionGrid(getContext(), transaction, dbHelper);
      gl.setOnClickListener(
			  v1 -> editTransaction(transaction));
      gl.setOnLongClickListener(
			  v12 -> {
				Toast.makeText(getContext(), "Click to Edit.", Toast.LENGTH_SHORT).show();
				return true;
			  });
      gl.setId(transaction.getID());

      // create textview displaying transaction payee
      TextView tvPayee = DynamicLayoutHandler.generatePayee(getContext(), transaction);
      tvPayee.setTextAppearance(R.style.nameTextInGrid);
      tvPayee.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);

      GridLayout.LayoutParams payeeParams = new GridLayout.LayoutParams();
      payeeParams.columnSpec = GridLayout.spec(0, 3, 3f);
      payeeParams.rowSpec = GridLayout.spec(0, 1);

      gl.addView(tvPayee, payeeParams);

      // create textview displaying transaction date
      TextView tvDate = DynamicLayoutHandler.generateDate(getContext(), transaction);

      GridLayout.LayoutParams dateParams = new GridLayout.LayoutParams();
      dateParams.columnSpec = GridLayout.spec(4, 1, 1f);
      dateParams.rowSpec = GridLayout.spec(0, 1);

      gl.addView(tvDate, dateParams);

      // create textview with transaction budget
      TextView tvBudget = DynamicLayoutHandler.generateBudget(getContext(), transaction, dbHelper);

      GridLayout.LayoutParams budgetParams = new GridLayout.LayoutParams();
      budgetParams.columnSpec = GridLayout.spec(0, 3, 3f);
      budgetParams.rowSpec = GridLayout.spec(1, 1);

      gl.addView(tvBudget, budgetParams);

      // create textview with transaction amount
      TextView tvAmount = DynamicLayoutHandler.generateAmount(getContext(), transaction);

      GridLayout.LayoutParams amountParams = new GridLayout.LayoutParams();
      amountParams.columnSpec = GridLayout.spec(4, 1, 1f);
      amountParams.rowSpec = GridLayout.spec(1, 1);

      gl.addView(tvAmount, amountParams);

      // create textview to display transaction description
      TextView tvDesc = DynamicLayoutHandler.generateDescription(getContext(), transaction);

      GridLayout.LayoutParams descParams = new GridLayout.LayoutParams();
      descParams.columnSpec = GridLayout.spec(0, 5, 5f);
      descParams.rowSpec = GridLayout.spec(2, 1);

      gl.addView(tvDesc, descParams);

      // add grid layout to view
      linearLayout.addView(gl);
    }
  }

  private void editTransaction(Transaction transaction) {
    try {
      setTransaction(transaction);
      Intent intent = new Intent(this.getContext(), EditTransactionActivity.class);
      startActivity(intent);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static Transaction getTransaction() {
    return t;
  }

  private static void setTransaction(Transaction transaction) {
    t = transaction;
  }
}

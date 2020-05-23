package com.example.alex_.project;

import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;

public class RecentTransactionFragment extends Fragment {

	private static Transaction t;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_recent_transaction, container, false);
		drawList(v);
		return v;
	}

	private void drawList(View v) {
		DBHelper dbHelper = new DBHelper(this.getActivity());
		List<Transaction> transactionList = dbHelper.getAllTransactions();

		//get linear layout
		LinearLayout linearLayout = v.findViewById(R.id.linearLayout);

		for (int i = 0; i < transactionList.size(); i++) {
			final Transaction transaction = transactionList.get(i);

			//create grid layout
			GridLayout gl = new GridLayout(this.getActivity());
			gl.setMinimumWidth(GridLayout.LayoutParams.MATCH_PARENT);
			gl.setMinimumHeight(GridLayout.LayoutParams.WRAP_CONTENT);
			gl.setColumnCount(5);
			gl.setRowCount(3);
			gl.setClickable(true);
			gl.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					editTransaction(transaction);
				}
			});
			gl.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					Toast.makeText(getContext(), "Click to Edit.", Toast.LENGTH_SHORT).show();
					return true;
				}
			});
			gl.setId(transaction.getID());
			gl.setBackgroundResource(R.drawable.on_pressed_state);
			gl.setPadding(30, 30, 30, 30);

			//create textview displaying transaction payee
			TextView tvPayee = new TextView(this.getActivity());
			tvPayee.setMinHeight(GridLayoutManager.LayoutParams.WRAP_CONTENT);
			tvPayee.setMinWidth(0);
			tvPayee.setGravity(Gravity.FILL);
			tvPayee.setTextAppearance(R.style.nameTextInGrid);
			tvPayee.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
			tvPayee.setText(transaction.getPaidTo());

			GridLayout.LayoutParams payeeParams = new GridLayout.LayoutParams();
			payeeParams.columnSpec = GridLayout.spec(0, 3, 3f);
			payeeParams.rowSpec = GridLayout.spec(0, 1);

			gl.addView(tvPayee, payeeParams);

			//create textview displaying transaction date
			TextView tvDate = new TextView(this.getActivity());
			tvDate.setMinHeight(GridLayoutManager.LayoutParams.WRAP_CONTENT);
			tvDate.setMinWidth(0);
			tvDate.setGravity(Gravity.FILL);
			tvDate.setTextAppearance(R.style.descTextInGrid);
			tvDate.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
			tvDate.setText(transaction.dateToString());

			GridLayout.LayoutParams dateParams = new GridLayout.LayoutParams();
			dateParams.columnSpec = GridLayout.spec(4, 1, 1f);
			dateParams.rowSpec = GridLayout.spec(0, 1);

			gl.addView(tvDate, dateParams);

			//create textview with transaction budget
			TextView tvBudget = new TextView(this.getActivity());
			tvBudget.setMinHeight(GridLayoutManager.LayoutParams.WRAP_CONTENT);
			tvBudget.setMinWidth(0);
			tvBudget.setGravity(Gravity.FILL);
			tvBudget.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
			tvBudget.setTextAppearance(R.style.transactionDetails);

			String budgetName = dbHelper.getBudget(transaction.getBudget()) == null ? "" : dbHelper.getBudget(transaction.getBudget()).getName();
			if (budgetName == null) {
				budgetName = "";
			}

			tvBudget.setText(budgetName);

			GridLayout.LayoutParams budgetParams = new GridLayout.LayoutParams();
			budgetParams.columnSpec = GridLayout.spec(0, 3, 3f);
			budgetParams.rowSpec = GridLayout.spec(1, 1);

			gl.addView(tvBudget, budgetParams);

			//create textview with transaction amount
			TextView tvAmount = new TextView(this.getActivity());
			tvAmount.setMinHeight(GridLayoutManager.LayoutParams.WRAP_CONTENT);
			tvAmount.setMinWidth(0);
			tvAmount.setGravity(Gravity.FILL);
			tvAmount.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
			tvAmount.setTextAppearance(R.style.transactionDetails);

			NumberFormat format = NumberFormat.getCurrencyInstance();
			format.setCurrency(Currency.getInstance("GBP"));
			tvAmount.setText(format.format(transaction.getValue()));

			if (transaction.isDeposit()) {
				tvAmount.setTextColor(getResources().getColor(R.color.transactionDeposit, null));
			} else {
				tvAmount.setTextColor(getResources().getColor(R.color.transactionWithdrawal, null));
			}

			GridLayout.LayoutParams amountParams = new GridLayout.LayoutParams();
			amountParams.columnSpec = GridLayout.spec(4, 1, 1f);
			amountParams.rowSpec = GridLayout.spec(1, 1);

			gl.addView(tvAmount, amountParams);

			//create textview to display transaction description
			TextView tvDesc = new TextView(this.getActivity());
			tvDesc.setMinHeight(GridLayoutManager.LayoutParams.WRAP_CONTENT);
			tvDesc.setMinWidth(0);
			tvDesc.setGravity(Gravity.FILL);
			tvDesc.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
			tvDesc.setTextAppearance(R.style.transactionDetails);
			tvDesc.setText(transaction.getDesc());

			GridLayout.LayoutParams descParams = new GridLayout.LayoutParams();
			descParams.columnSpec = GridLayout.spec(0, 5, 5f);
			descParams.rowSpec = GridLayout.spec(2, 1);

			gl.addView(tvDesc, descParams);

			//add grid layout to view
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

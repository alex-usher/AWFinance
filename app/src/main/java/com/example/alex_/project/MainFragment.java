package com.example.alex_.project;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
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
import android.widget.Toast;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.Date;
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
			GridLayout gl = new GridLayout(this.getActivity());
			gl.setRowCount(1);
			gl.setColumnCount(8);
			gl.setClickable(true);
			gl.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					NumberFormat format = NumberFormat.getCurrencyInstance();
					format.setCurrency(Currency.getInstance("GBP"));
					String text = format.format(-1 * budget.getSpent(dbHelper)) + " spent out of " + format.format(budget.getAmount());
					Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
				}
			});
			gl.setPadding(10, 30, 10, 30);
			gl.setMinimumWidth(GridLayout.LayoutParams.MATCH_PARENT);
			gl.setMinimumHeight(GridLayout.LayoutParams.WRAP_CONTENT);
			gl.setBackgroundResource(R.drawable.on_pressed_state_dark);

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
			ProgressBar progressBar = new ProgressBar(this.getActivity(), null, android.R.attr.progressBarStyleHorizontal);
			progressBar.setIndeterminate(false);
			progressBar.setProgress(-1 * Math.round((budget.getSpent(dbHelper) / budget.getAmount()) * 100));
			progressBar.setVisibility(View.VISIBLE);

			Drawable progressDrawable = progressBar.getProgressDrawable().mutate();
			progressDrawable.setColorFilter(Color.parseColor(budget.getColour()), PorterDuff.Mode.SRC_IN);
			progressBar.setProgressDrawable(progressDrawable);

			GridLayout.LayoutParams progressParams = new GridLayout.LayoutParams();
			progressParams.columnSpec = GridLayout.spec(2, 5, 5f);
			progressParams.rowSpec = GridLayout.spec(0, 1);

			gl.addView(progressBar, progressParams);

			//create text view for %
			TextView tvPerc = new TextView(this.getActivity());
			tvPerc.setMinWidth(0);
			tvPerc.setMinHeight(GridLayoutManager.LayoutParams.MATCH_PARENT);
			tvPerc.setGravity(Gravity.FILL);
			String percent = Math.round(-1 * (budget.getSpent(dbHelper) / budget.getAmount()) * 100) + "%";
			tvPerc.setText(percent);
			tvPerc.setTextAppearance(R.style.progressBox);
			tvPerc.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

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
				GridLayout gl = new GridLayout(this.getActivity());
				gl.setMinimumWidth(GridLayout.LayoutParams.MATCH_PARENT);
				gl.setMinimumHeight(GridLayout.LayoutParams.WRAP_CONTENT);
				gl.setColumnCount(5);
				gl.setRowCount(3);
				gl.setClickable(true);
				gl.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						NumberFormat format = NumberFormat.getCurrencyInstance();
						format.setCurrency(Currency.getInstance("GBP"));
						String text = "";
						if (transaction.getValue() < 0) {
							text = String.format("%s: Paid %s to %s.",
								transaction.getDate().format(DateTimeFormatter.ofPattern(DBHelper.DATE_FORMAT_DISPLAYED)),
								format.format(transaction.getValue()), transaction.getPaidTo());
						} else {
							text = String.format("%s: Received %s from %s.",
								transaction.getDate().format(DateTimeFormatter.ofPattern(DBHelper.DATE_FORMAT_DISPLAYED)),
								format.format(-1 * transaction.getValue()), transaction.getPaidTo());
						}
						Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
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

				String budgetName = dbHelper.getBudget(transaction.getBudget()).getName();
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
	}
}

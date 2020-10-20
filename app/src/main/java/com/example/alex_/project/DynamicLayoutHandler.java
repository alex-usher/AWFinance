package com.example.alex_.project;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Currency;

public abstract class DynamicLayoutHandler {

	public static GridLayout createBudgetWithProgress(final Context context, final Budget budget, final DBHelper dbHelper) {
		//create gridlayout
		GridLayout gl = DynamicLayoutHandler.generateGrid(context, budget, dbHelper);

		//create textview for budget name
		TextView tvName = new TextView(context);
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
		ProgressBar progressBar = DynamicLayoutHandler.generateProgressBar(context, budget, dbHelper);

		GridLayout.LayoutParams progressParams = new GridLayout.LayoutParams();
		progressParams.columnSpec = GridLayout.spec(2, 5, 5f);
		progressParams.rowSpec = GridLayout.spec(0, 1);

		gl.addView(progressBar, progressParams);

		//create text view for %
		TextView tvPerc = DynamicLayoutHandler.generateTextView(context, budget, dbHelper);

		GridLayout.LayoutParams percParams = new GridLayout.LayoutParams();
		percParams.columnSpec = GridLayout.spec(7, 1,  1f);
		percParams.rowSpec = GridLayout.spec(0, 1);

		gl.addView(tvPerc, percParams);

		return gl;
	}

	public static GridLayout generateGrid(final Context context, final Budget budget, final DBHelper dbHelper) {
		GridLayout gl = new GridLayout(context);
		gl.setRowCount(1);
		gl.setColumnCount(8);
		gl.setClickable(true);
		gl.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				NumberFormat format = NumberFormat.getCurrencyInstance();
				format.setCurrency(Currency.getInstance("GBP"));
				String text = format.format(-1 * budget.getSpent(dbHelper)) + " spent out of " + format.format(budget.getAmount());
				Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
			}
		});
		gl.setPadding(10, 30, 10, 30);
		gl.setMinimumWidth(GridLayout.LayoutParams.MATCH_PARENT);
		gl.setMinimumHeight(GridLayout.LayoutParams.WRAP_CONTENT);
		gl.setBackgroundResource(R.drawable.on_pressed_state_dark);
		return gl;
	}

	public static ProgressBar generateProgressBar(Context context, Budget budget, DBHelper dbHelper) {
		ProgressBar progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
		progressBar.setIndeterminate(false);
		progressBar.setProgress(-1 * Math.round((budget.getSpent(dbHelper) / budget.getAmount()) * 100));
		progressBar.setVisibility(View.VISIBLE);

		Drawable progressDrawable = progressBar.getProgressDrawable().mutate();
		progressDrawable.setColorFilter(Color.parseColor(budget.getColour()), PorterDuff.Mode.SRC_IN);
		progressBar.setProgressDrawable(progressDrawable);

		return progressBar;
	}

	public static TextView generateTextView(Context context, Budget budget, DBHelper dbHelper) {
		TextView tvPerc = new TextView(context);
		tvPerc.setMinWidth(0);
		tvPerc.setMinHeight(GridLayoutManager.LayoutParams.MATCH_PARENT);
		tvPerc.setGravity(Gravity.FILL);
		String percent = Math.round(-1 * (budget.getSpent(dbHelper) / budget.getAmount()) * 100) + "%";
		tvPerc.setText(percent);
		tvPerc.setTextAppearance(R.style.progressBox);
		tvPerc.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

		return tvPerc;
	}

	public static GridLayout generateGrid(final Context context, final Transaction transaction, DBHelper dbHelper) {
		GridLayout gl = new GridLayout(context);
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
				Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
			}
		});
		gl.setId(transaction.getID());
		gl.setBackgroundResource(R.drawable.on_pressed_state);
		gl.setPadding(30, 30, 30, 30);

		TextView tvPayee = generatePayee(context, transaction);

		GridLayout.LayoutParams payeeParams = new GridLayout.LayoutParams();
		payeeParams.columnSpec = GridLayout.spec(0, 3, 3f);
		payeeParams.rowSpec = GridLayout.spec(0, 1);

		gl.addView(tvPayee, payeeParams);

		//create textview displaying transaction date
		TextView tvDate = generateDate(context, transaction);

		GridLayout.LayoutParams dateParams = new GridLayout.LayoutParams();
		dateParams.columnSpec = GridLayout.spec(4, 1, 1f);
		dateParams.rowSpec = GridLayout.spec(0, 1);

		gl.addView(tvDate, dateParams);

		//create textview with transaction budget
		TextView tvBudget = generateBudget(context, transaction, dbHelper);

		GridLayout.LayoutParams budgetParams = new GridLayout.LayoutParams();
		budgetParams.columnSpec = GridLayout.spec(0, 3, 3f);
		budgetParams.rowSpec = GridLayout.spec(1, 1);

		gl.addView(tvBudget, budgetParams);

		//create textview with transaction amount
		TextView tvAmount = generateAmount(context, transaction);

		GridLayout.LayoutParams amountParams = new GridLayout.LayoutParams();
		amountParams.columnSpec = GridLayout.spec(4, 1, 1f);
		amountParams.rowSpec = GridLayout.spec(1, 1);

		gl.addView(tvAmount, amountParams);

		//create textview to display transaction description
		TextView tvDesc = generateDescription(context, transaction);

		GridLayout.LayoutParams descParams = new GridLayout.LayoutParams();
		descParams.columnSpec = GridLayout.spec(0, 5, 5f);
		descParams.rowSpec = GridLayout.spec(2, 1);

		gl.addView(tvDesc, descParams);

		return gl;
	}

	public static TextView generatePayee(Context context, Transaction transaction) {
		TextView tvPayee = new TextView(context);
		tvPayee.setMinHeight(GridLayoutManager.LayoutParams.WRAP_CONTENT);
		tvPayee.setMinWidth(0);
		tvPayee.setGravity(Gravity.FILL);
		tvPayee.setTextAppearance(R.style.nameTextInGrid);
		tvPayee.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
		tvPayee.setText(transaction.getPaidTo());

		return tvPayee;
	}

	public static TextView generateDate(Context context, Transaction transaction) {
		TextView tvDate = new TextView(context);
		tvDate.setMinHeight(GridLayoutManager.LayoutParams.WRAP_CONTENT);
		tvDate.setMinWidth(0);
		tvDate.setGravity(Gravity.FILL);
		tvDate.setTextAppearance(R.style.descTextInGrid);
		tvDate.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
		tvDate.setText(transaction.dateToString());

		return tvDate;
	}

	public static TextView generateBudget(Context context, Transaction transaction, DBHelper dbHelper) {
		TextView tvBudget = new TextView(context);
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

		return tvBudget;
	}

	public static TextView generateAmount(Context context, Transaction transaction) {
		TextView tvAmount = new TextView(context);
		tvAmount.setMinHeight(GridLayoutManager.LayoutParams.WRAP_CONTENT);
		tvAmount.setMinWidth(0);
		tvAmount.setGravity(Gravity.FILL);
		tvAmount.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
		tvAmount.setTextAppearance(R.style.transactionDetails);

		NumberFormat format = NumberFormat.getCurrencyInstance();
		format.setCurrency(Currency.getInstance("GBP"));
		tvAmount.setText(format.format(transaction.getValue()));

		if (transaction.isDeposit()) {
			tvAmount.setTextColor(context.getResources().getColor(R.color.transactionDeposit, null));
		} else {
			tvAmount.setTextColor(context.getResources().getColor(R.color.transactionWithdrawal, null));
		}

		return tvAmount;
	}

	public static TextView generateDescription(Context context, Transaction transaction) {
		TextView tvDesc = new TextView(context);
		tvDesc.setMinHeight(GridLayoutManager.LayoutParams.WRAP_CONTENT);
		tvDesc.setMinWidth(0);
		tvDesc.setGravity(Gravity.FILL);
		tvDesc.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
		tvDesc.setTextAppearance(R.style.transactionDetails);
		tvDesc.setText(transaction.getDesc());

		return tvDesc;
	}

}

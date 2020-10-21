package com.example.alex_.project;

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

  /**
   * Creates a gridlayout that appears something like:
   * -------------------------------------------------
   * Budget Name /------ PROGRESS BOX -------/     xx%
   * -------------------------------------------------
   *
   * @param context - the context to create the GridLayout in
   * @param budget - the budget to create the GridLayout for
   * @param dbHelper - a DBHelper instance to use
   * @return - the GridLayout as detailed above, ready to be added to the view
   */
  public static GridLayout createBudgetWithProgress(
      final Context context, final Budget budget, final DBHelper dbHelper) {
    // create gridlayout
    GridLayout gl = DynamicLayoutHandler.generateBudgetGrid(context, budget, dbHelper);

    // create textview for budget name and define appearance / layout
    TextView tvName = new TextView(context);
    tvName.setMinWidth(0);
    tvName.setMinHeight(GridLayoutManager.LayoutParams.MATCH_PARENT);
    tvName.setGravity(Gravity.FILL);
    tvName.setText(budget.getName());
    tvName.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
    tvName.setTextAppearance(R.style.progressBox);

    // add the TextView to the view
    GridLayout.LayoutParams nameParams = new GridLayout.LayoutParams();
    nameParams.columnSpec = GridLayout.spec(0, 2, 2f);
    nameParams.rowSpec = GridLayout.spec(0, 1);

    gl.addView(tvName, nameParams);

    // create progress bar
    ProgressBar progressBar =
        DynamicLayoutHandler.generateBudgetProgressBar(context, budget, dbHelper);

    // add the progress bar to the view
    GridLayout.LayoutParams progressParams = new GridLayout.LayoutParams();
    progressParams.columnSpec = GridLayout.spec(2, 5, 5f);
    progressParams.rowSpec = GridLayout.spec(0, 1);

    gl.addView(progressBar, progressParams);

    // create text view for %
    TextView tvPerc = DynamicLayoutHandler.generatePercentageTextView(context, budget, dbHelper);

    // add the TextView for percentage to the view
    GridLayout.LayoutParams percParams = new GridLayout.LayoutParams();
    percParams.columnSpec = GridLayout.spec(7, 1, 1f);
    percParams.rowSpec = GridLayout.spec(0, 1);

    gl.addView(tvPerc, percParams);

    return gl;
  }

  /**
   * Returns a GridLayout created for the given budget, with its OnClickListener set to display a
   * Toast containing information relevant to the budget.
   *
   * @param context - the context to create the GridLayout in
   * @param budget - the Budget for the Toast to contain information about
   * @param dbHelper - a DBHelper instance used to obtain information about the budget
   * @return - the GridLayout object, ready to add to a view.
   */
  public static GridLayout generateBudgetGrid(
      final Context context, final Budget budget, final DBHelper dbHelper) {
    GridLayout gl = new GridLayout(context);
    gl.setRowCount(1);
    gl.setColumnCount(8);
    gl.setClickable(true);
    gl.setOnClickListener(
        v -> {
          NumberFormat format = NumberFormat.getCurrencyInstance();
          format.setCurrency(Currency.getInstance("GBP"));
          String text =
              String.format(
                  "%s spent out of %s",
                  format.format(-1 * budget.getSpent(dbHelper)), format.format(budget.getAmount()));
          Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        });
    gl.setPadding(10, 30, 10, 30);
    gl.setMinimumWidth(GridLayout.LayoutParams.MATCH_PARENT);
    gl.setMinimumHeight(GridLayout.LayoutParams.WRAP_CONTENT);
    gl.setBackgroundResource(R.drawable.on_pressed_state_dark);
    return gl;
  }

  /**
   * Creates a progress bar displaying the percentage of a budget's amount that has been spent
   *
   * @param context - the context to create the ProgressBar in
   * @param budget - the budget to the display the percentage for
   * @param dbHelper - a DBHelper instance used to obtain information about the budget's amount
   *     spent
   * @return - the ProgressBar object, ready to add to a view.
   */
  public static ProgressBar generateBudgetProgressBar(
      Context context, Budget budget, DBHelper dbHelper) {
    ProgressBar progressBar =
        new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
    progressBar.setIndeterminate(false);

    // set the percentage
    progressBar.setProgress(
        -1 * Math.round((budget.getSpent(dbHelper) / budget.getAmount()) * 100));

    progressBar.setVisibility(View.VISIBLE);

    // set the colour and appearance of the progress bar
    Drawable progressDrawable = progressBar.getProgressDrawable().mutate();
    progressDrawable.setColorFilter(Color.parseColor(budget.getColour()), PorterDuff.Mode.SRC_IN);
    progressBar.setProgressDrawable(progressDrawable);

    return progressBar;
  }

  /**
   * Creates a TextView displaying the percentage of the amount of the budget that has been spent,
   * as a text, e.g. xx%
   *
   * @param context - the context to create the TextView in
   * @param budget - the budget to use to obtain the percentage
   * @param dbHelper - a DBHelper instance used to obtain the percentage
   * @return - the TextView object, ready to be added to a view.
   */
  public static TextView generatePercentageTextView(
      Context context, Budget budget, DBHelper dbHelper) {
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

  /**
   * Generates the GridLayout for the transaction information, seen in MainFragment,
   * BudgetTransactionsFragment etc. Displays all information about a Transaction - amount,
   * description, date etc. and on click displays this information as a sentence in a Toast.
   *
   * @param context - the context to create the GridLayout in
   * @param transaction - the transaction to use to obtain the information
   * @param dbHelper - a DBHelper instance used to obtain some of the information required
   * @return - the GridLayout object, ready to be added to a view.
   */
  public static GridLayout generateTransactionGrid(
      final Context context, final Transaction transaction, DBHelper dbHelper) {
    GridLayout gl = new GridLayout(context);
    gl.setMinimumWidth(GridLayout.LayoutParams.MATCH_PARENT);
    gl.setMinimumHeight(GridLayout.LayoutParams.WRAP_CONTENT);
    gl.setColumnCount(5);
    gl.setRowCount(3);
    gl.setClickable(true);

    // set the on click listener to display a Toast
    gl.setOnClickListener(
        v -> {
          NumberFormat format = NumberFormat.getCurrencyInstance();
          format.setCurrency(Currency.getInstance("GBP"));
          String text = "";
          if (transaction.getValue() < 0) {
            text =
                String.format(
                    "%s: Paid %s to %s.",
                    transaction
                        .getDate()
                        .format(DateTimeFormatter.ofPattern(DBHelper.DATE_FORMAT_DISPLAYED)),
                    format.format(transaction.getValue()),
                    transaction.getPaidTo());
          } else {
            text =
                String.format(
                    "%s: Received %s from %s.",
                    transaction
                        .getDate()
                        .format(DateTimeFormatter.ofPattern(DBHelper.DATE_FORMAT_DISPLAYED)),
                    format.format(-1 * transaction.getValue()),
                    transaction.getPaidTo());
          }
          Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        });
    gl.setId(transaction.getID());
    gl.setBackgroundResource(R.drawable.on_pressed_state);
    gl.setPadding(30, 30, 30, 30);

    // create TextView displaying who the transaction was paid to / received from
    TextView tvPayee = generatePayee(context, transaction);

    GridLayout.LayoutParams payeeParams = new GridLayout.LayoutParams();
    payeeParams.columnSpec = GridLayout.spec(0, 3, 3f);
    payeeParams.rowSpec = GridLayout.spec(0, 1);

    gl.addView(tvPayee, payeeParams);

    // create textview displaying transaction date
    TextView tvDate = generateDate(context, transaction);

    GridLayout.LayoutParams dateParams = new GridLayout.LayoutParams();
    dateParams.columnSpec = GridLayout.spec(4, 1, 1f);
    dateParams.rowSpec = GridLayout.spec(0, 1);

    gl.addView(tvDate, dateParams);

    // create textview with transaction budget
    TextView tvBudget = generateBudget(context, transaction, dbHelper);

    GridLayout.LayoutParams budgetParams = new GridLayout.LayoutParams();
    budgetParams.columnSpec = GridLayout.spec(0, 3, 3f);
    budgetParams.rowSpec = GridLayout.spec(1, 1);

    gl.addView(tvBudget, budgetParams);

    // create textview with transaction amount
    TextView tvAmount = generateAmount(context, transaction);

    GridLayout.LayoutParams amountParams = new GridLayout.LayoutParams();
    amountParams.columnSpec = GridLayout.spec(4, 1, 1f);
    amountParams.rowSpec = GridLayout.spec(1, 1);

    gl.addView(tvAmount, amountParams);

    // create TextView to display transaction description
    TextView tvDesc = generateDescription(context, transaction);

    GridLayout.LayoutParams descParams = new GridLayout.LayoutParams();
    descParams.columnSpec = GridLayout.spec(0, 5, 5f);
    descParams.rowSpec = GridLayout.spec(2, 1);

    gl.addView(tvDesc, descParams);

    return gl;
  }

  /**
   * Creates a TextView containing the payee / receiver of a particular transaction
   *
   * @param context - the context to create the TextView in.
   * @param transaction - the transaction to obtain the payee / receiver from
   * @return - the TextView object, ready to be added to a view.
   */
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

  /**
   * Creates a TextView containing the date that a transaction occurred.
   *
   * @param context - the context to create the TextView in
   * @param transaction - the transaction to obtain the date from
   * @return - the TextView object, ready to be added to a view.
   */
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

  /**
   * Creates a TextView containing the name of the Budget the transaction belongs to
   *
   * @param context - the context to create the TextView in
   * @param transaction - the transaction from which to obtain the budget name
   * @param dbHelper - a DBHelper instance used to help obtain the budget name
   * @return - the TextView object, ready to be added to a view.
   */
  public static TextView generateBudget(
      Context context, Transaction transaction, DBHelper dbHelper) {
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

  /**
   * Creates a TextView containing the amount that the transaction was.
   *
   * @param context - the context to create the TextView in
   * @param transaction - the transaction from which to obtain the amount
   * @return - the TextView object, ready to be added to a view.
   */
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

  /**
   * Creates a TextView object containing the description of the given transaction
   *
   * @param context - the context to create the TextView in
   * @param transaction - the transaction from which to obtain the description
   * @return - the TextView object, ready to be added to a view.
   */
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

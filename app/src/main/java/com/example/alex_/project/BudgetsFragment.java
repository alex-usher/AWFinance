package com.example.alex_.project;

import android.content.Intent;
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

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public class BudgetsFragment extends Fragment {

	private static Budget budget;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_budgets, container, false);

		PieChart pieChart = v.findViewById(R.id.pieChart);
		drawChart(pieChart);
		setTotalSpending(v);
		drawList(v);

		return v;
	}

	private void setTotalSpending(View v) {
		DBHelper dbHelper = new DBHelper(getContext());
		float totalSpent = -1 * dbHelper.getTotalBudgetSpending();
		NumberFormat format = NumberFormat.getCurrencyInstance();
		format.setCurrency(Currency.getInstance("GBP"));

		String text = "Total Spent: " + format.format(totalSpent);
		TextView tv = v.findViewById(R.id.totalSpending);
		tv.setText(text);
	}

	private void drawChart(PieChart pieChart) {
		pieChart.setUsePercentValues(true);

		ArrayList<PieEntry> yvalues = new ArrayList<>();
		ArrayList<Integer> colours = new ArrayList<>();

		DBHelper dbHelper = new DBHelper(getContext());
		List<Budget> budgetList = dbHelper.getAllBudgets();

		if (budgetList.size() > 0) {
			for (int i = 0; i < budgetList.size(); i++) {
				Budget budget = budgetList.get(i);

				yvalues.add(new PieEntry(-1 * budget.getSpent(dbHelper), budget.getName(), i));
				colours.add(Color.parseColor(budget.getColour()));
			}

			PieDataSet dataSet = new PieDataSet(yvalues, "");
			PieData data = new PieData(dataSet);

			data.setValueFormatter(new PercentFormatter());

			pieChart.setData(data);
			dataSet.setColors(colours);
			dataSet.setSliceSpace(3f);
			pieChart.setDrawHoleEnabled(true);
			pieChart.setHoleRadius(15f);
			pieChart.setTransparentCircleRadius(17f);
			data.setValueTextColor(Color.WHITE);
			data.setValueTextSize(13f);
			pieChart.setDrawEntryLabels(false);

			Description description = new Description();
			description.setText("");
			description.setTextColor(Color.WHITE);

			//pieChart.getLegend().setTextColor(Color.WHITE);
			pieChart.setBackgroundColor(Color.BLACK);
			pieChart.setCenterTextColor(Color.BLACK);

			Legend legend = pieChart.getLegend();
			legend.setEnabled(true);
			legend.setTextColor(Color.WHITE);
			legend.setTextSize(13f);

		} else {
			pieChart.setCenterText("No Budgets Found");
			pieChart.setCenterTextColor(R.color.defaultTextLight);
		}
	}

	private void drawList(View v) {
		DBHelper dbHelper = new DBHelper(getContext());
		List<Budget> budgetList = dbHelper.getAllBudgets();

		for (int i = 0; i < budgetList.size(); i++) {
			final Budget b = budgetList.get(i);

			LinearLayout linearLayout = v.findViewById(R.id.linearLayout);

			//create grid layout
			final GridLayout gl = new GridLayout(getActivity());
			gl.setMinimumWidth(GridLayout.LayoutParams.MATCH_PARENT);
			gl.setMinimumHeight(GridLayout.LayoutParams.WRAP_CONTENT);
			gl.setColumnCount(3);
			gl.setRowCount(3);
			gl.setPadding(30, 30, 30, 30);
			gl.setId(b.getID());
			gl.setBackgroundResource(R.drawable.on_pressed_state);
			gl.setClickable(true);
			gl.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					budget = b;
					getFragmentManager()
						.beginTransaction()
						.replace(R.id.fragment_container, new BudgetTransactionsFragment())
						.addToBackStack(null)
						.commit();
				}
			});
			gl.setLongClickable(true);
			gl.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					budget = b;
					startActivity(new Intent(getContext(), EditBudgetActivity.class));
					return true;
				}
			});

			//create textview budget name
			TextView tvName = new TextView(getActivity());
			String name = b.getName();
			tvName.setMinHeight(GridLayoutManager.LayoutParams.MATCH_PARENT);
			tvName.setMinWidth(0);
			tvName.setGravity(Gravity.FILL_HORIZONTAL);
			tvName.setText(name);
			tvName.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
			tvName.setTextAppearance(R.style.nameTextInGrid);

			GridLayout.LayoutParams nameParams = new GridLayout.LayoutParams();
			nameParams.columnSpec = GridLayout.spec(0, 2, 2f);
			nameParams.rowSpec = GridLayout.spec(0, 1);

			gl.addView(tvName, nameParams);

			//create textview amount, frequency
			TextView tvAmount = new TextView(getActivity());
			tvAmount.setMinHeight(GridLayoutManager.LayoutParams.MATCH_PARENT);
			tvAmount.setMinWidth(0);
			tvAmount.setGravity(Gravity.FILL_HORIZONTAL);

			NumberFormat format = NumberFormat.getCurrencyInstance();
			format.setCurrency(Currency.getInstance("GBP"));
			String text = format.format(b.getAmount()) + ", " + b.getType().toString();
			tvAmount.setText(text);

			tvAmount.setTextAppearance(R.style.descTextInGrid);
			tvAmount.setGravity(Gravity.END);
			tvAmount.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);

			GridLayout.LayoutParams amountParams = new GridLayout.LayoutParams();
			amountParams.columnSpec = GridLayout.spec(2, 1, 1f);
			amountParams.rowSpec = GridLayout.spec(0, 1);

			gl.addView(tvAmount, amountParams);

			//create progress bar
			ProgressBar pb = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleHorizontal);
			pb.setIndeterminate(false);
			pb.setProgress(-1 * Math.round((b.getSpent(dbHelper) / b.getAmount()) * 100));
			pb.setVisibility(View.VISIBLE);

			Drawable progressDrawable = pb.getProgressDrawable().mutate();
			progressDrawable.setColorFilter(Color.parseColor(b.getColour()), PorterDuff.Mode.SRC_IN);
			pb.setProgressDrawable(progressDrawable);

			GridLayout.LayoutParams progressParams = new GridLayout.LayoutParams();
			progressParams.columnSpec = GridLayout.spec(0, 3, 3f);
			progressParams.rowSpec = GridLayout.spec(1, 1);

			gl.addView(pb, progressParams);
			linearLayout.addView(gl);

			//create textview % spent
			TextView percentSpent = new TextView(getActivity());
			percentSpent.setMinHeight(GridLayoutManager.LayoutParams.MATCH_PARENT);
			percentSpent.setMinWidth(0);
			percentSpent.setGravity(Gravity.FILL_HORIZONTAL);
			String spent = -1 * Math.round(b.getSpent(dbHelper) / b.getAmount() * 100) + "% Used";
			percentSpent.setText(spent);
			percentSpent.setTextAppearance(R.style.descTextInGrid);
			percentSpent.setGravity(Gravity.START);
			percentSpent.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);

			GridLayout.LayoutParams psParams = new GridLayout.LayoutParams();
			psParams.columnSpec = GridLayout.spec(0, 2, 2f);
			psParams.rowSpec = GridLayout.spec(2, 1);

			gl.addView(percentSpent, psParams);

			//create textview amount left
			TextView amountLeft = new TextView(getActivity());
			amountLeft.setMinHeight(GridLayoutManager.LayoutParams.MATCH_PARENT);
			amountLeft.setMinWidth(0);
			amountLeft.setGravity(Gravity.FILL_HORIZONTAL);

			String amount = format.format(b.getAmount() + b.getSpent(dbHelper)) + " Left";
			amountLeft.setText(amount);

			amountLeft.setTextAppearance(R.style.descTextInGrid);
			amountLeft.setGravity(Gravity.END);
			amountLeft.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);

			GridLayout.LayoutParams alParams = new GridLayout.LayoutParams();
			alParams.columnSpec = GridLayout.spec(2, 1, 1f);
			alParams.rowSpec = GridLayout.spec(2, 1);

			gl.addView(amountLeft, alParams);
		}
	}

	public static Budget getBudget() {
		return budget;
	}
}

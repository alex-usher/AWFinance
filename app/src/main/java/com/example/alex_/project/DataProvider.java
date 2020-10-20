package com.example.alex_.project;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

public class DataProvider implements RemoteViewsService.RemoteViewsFactory {
	List<GridLayout> list = new ArrayList<>();
	Context context;
	private int appWidgetId;

	public DataProvider(Context context, Intent intent) {
		this.context = context;
		appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
	}

	@Override
	public void onCreate() {
		init();
	}

	@Override
	public void onDataSetChanged() {
		init();
	}

	@Override
	public void onDestroy() {
		list.clear();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public RemoteViews getViewAt(int position) {
		RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_budget_list);
		list.get(position).setVisibility(View.VISIBLE);
		rv.apply(context, list.get(position));

		return rv;
	}

	@Override
	public RemoteViews getLoadingView() {
		return null;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	private void init() {
		list.clear();

		final DBHelper dbHelper = new DBHelper(context);
		List<Budget> budgetList = dbHelper.getAllBudgets();

		System.out.println(budgetList);

		for (Budget b : budgetList) {
			list.add(DynamicLayoutHandler.createBudgetWithProgress(context, b, dbHelper));
		}

		System.out.println(list);
	}
}
package com.example.alex_.project;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

public class BudgetListWidget extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		for(int id : appWidgetIds) {
			Intent intent = new Intent(context, WidgetService.class);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
			intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

			RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_budget_list);

			rv.setRemoteAdapter(R.id.budget_list, intent);

			rv.setEmptyView(R.id.budget_list, R.id.empty_view);
			appWidgetManager.notifyAppWidgetViewDataChanged(id, R.id.budget_list);
			appWidgetManager.updateAppWidget(id, rv);
		}

		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
}


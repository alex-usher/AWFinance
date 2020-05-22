package com.example.alex_.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView nv = findViewById(R.id.nav_view);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.navHome:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MainFragment()).commit();
                        break;
                    case R.id.navRecentTransactions:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RecentTransactionFragment()).commit();
                        break;
                    case R.id.navBudgets:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BudgetsFragment()).commit();
                        break;
                    default:
                        break;
                }

                drawer.closeDrawer(GravityCompat.START);//changing menu state
                return true;
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.open_nav, R.string.close_nav);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new MainFragment()).commit();

            nv.setCheckedItem(R.id.navHome);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.addButton:
                View menuItemView = findViewById(R.id.addButton);

                Context context = new ContextThemeWrapper(this, R.style.optionsMenu);
                PopupMenu pm = new PopupMenu(context, menuItemView);
                pm.inflate(R.menu.menu_toolbar_drawer);

                pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()){
                            case R.id.addTransaction:
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddTransaction()).commit();
                                break;
                            case R.id.addBudget:
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddBudget()).commit();
                                break;
                            default:
                                break;
                        }

                        return true;
                    }
                });

                pm.show();
                break;
            case R.id.settingsButton:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}

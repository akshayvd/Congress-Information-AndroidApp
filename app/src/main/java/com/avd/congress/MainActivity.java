package com.avd.congress;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    LegislatorFragment legislatorFragment;
    BillFragment billFragment;
    CommitteeFragment committeeFragment;
    FavoriteFragment favoriteFragment;
    AboutMe aboutMe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        loadLegislatorFragment();
        return true;
    }

    private void loadLegislatorFragment() {
        if(legislatorFragment==null){
           legislatorFragment = new LegislatorFragment();
        }
        getSupportActionBar().setTitle("Legislators");
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, legislatorFragment).commit();
    }

    private void loadBillFragment() {
        if(billFragment==null){
            billFragment = new BillFragment();
        }
        getSupportActionBar().setTitle("Bills");
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, billFragment).commit();
    }

    private void loadCommitteeFragment() {
        if(committeeFragment==null){
            committeeFragment = new CommitteeFragment();
        }
        getSupportActionBar().setTitle("Committees");
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, committeeFragment).commit();
    }

    private void loadFavoriteFragment() {
        if(favoriteFragment==null){
            favoriteFragment = new FavoriteFragment();
        }
        getSupportActionBar().setTitle("Favorites");
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, favoriteFragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_legislators) {
            loadLegislatorFragment();
        } else if (id == R.id.nav_bills) {
            loadBillFragment();
        } else if (id == R.id.nav_committees) {
            loadCommitteeFragment();
        } else if (id == R.id.nav_favorites) {
            loadFavoriteFragment();
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(this, AboutMe.class);
            startActivityForResult(intent, 0);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

package edu.iit.cs442.tmolenho.ehome;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        AccountSettingsFragment.OnFragmentInteractionListener,
        DashboardFragment.OnFragmentInteractionListener,
        SearchOfflineFragment.OnFragmentInteractionListener,
        SearchOnlineFragment.OnFragmentInteractionListener,
        SearchOptionsFragment.OnFragmentInteractionListener {

    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // display DashboardFragment initially
        fm = getSupportFragmentManager();
        Fragment dashboard = DashboardFragment.newInstance("", "");
        fm.beginTransaction().replace(R.id.contentFragment, dashboard).commit();

        navigationView.setCheckedItem(R.id.nav_dashboard);
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if (!item.isChecked()) {
            switch (item.getItemId()) {
                case R.id.nav_dashboard:
                    Fragment dashboard = DashboardFragment.newInstance("", "");
                    fm.beginTransaction()
                            .replace(R.id.contentFragment, dashboard)
                            .addToBackStack(null)
                            .commit();
                    break;
                case R.id.nav_admin_add:
                    // TODO
                    break;
                case R.id.nav_search_offline:
                    Fragment searchOffline = SearchOfflineFragment.newInstance("", "");
                    fm.beginTransaction()
                            .replace(R.id.contentFragment, searchOffline)
                            .addToBackStack(null)
                            .commit();
                    break;
                case R.id.nav_search_online:
                    Fragment searchOnline = SearchOnlineFragment.newInstance("", "");
                    fm.beginTransaction()
                            .replace(R.id.contentFragment, searchOnline)
                            .addToBackStack(null)
                            .commit();
                    break;
                case R.id.nav_search_options:
                    Fragment searchOptions = SearchOptionsFragment.newInstance("", "");
                    fm.beginTransaction()
                            .replace(R.id.contentFragment, searchOptions)
                            .addToBackStack(null)
                            .commit();
                    break;
                case R.id.nav_account_setttings:
                    Fragment accountSettings = AccountSettingsFragment.newInstance("", "");
                    fm.beginTransaction()
                            .replace(R.id.contentFragment, accountSettings)
                            .addToBackStack(null)
                            .commit();
                    break;
                case R.id.nav_logout:
                    // TODO handle other logout tasks, clear backstack etc
                    Intent logout = new Intent(this, LoginActivity.class);
                    startActivity(logout);
                    break;
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}

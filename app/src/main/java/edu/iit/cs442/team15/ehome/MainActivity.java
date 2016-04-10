package edu.iit.cs442.team15.ehome;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import edu.iit.cs442.team15.ehome.util.SavedLogin;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        AccountSettingsFragment.OnAccountUpdatedListener,
        DashboardFragment.OnFragmentInteractionListener,
        SearchOfflineFragment.OnFragmentInteractionListener {

    // key for storing nav item id that corresponds to a Fragment
    private static final String NAV_ID = "nav_id";

    private DrawerLayout drawer;
    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        this.onAccountUpdated(); // display user Name and Email in nav header

        fm = getSupportFragmentManager();
        fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                // select correct menu entry
                Fragment displayed = fm.findFragmentById(R.id.contentFragment);

                if (displayed instanceof DashboardFragment)
                    navigationView.setCheckedItem(R.id.nav_dashboard);
                else if (displayed instanceof SearchOfflineFragment)
                    navigationView.setCheckedItem(R.id.nav_search_offline);
                else if (displayed instanceof SearchOnlineFragment)
                    navigationView.setCheckedItem(R.id.nav_search_online);
                else if (displayed instanceof SearchOptionsFragment)
                    navigationView.setCheckedItem(R.id.nav_search_options);
                else if (displayed instanceof AccountSettingsFragment)
                    navigationView.setCheckedItem(R.id.nav_account_settings);
            }
        });

        // display DashboardFragment initially
        fm.beginTransaction()
                .replace(R.id.contentFragment, DashboardFragment.newInstance())
                .commit();
        navigationView.setCheckedItem(R.id.nav_dashboard);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment newFragment = null; // new Fragment to be displayed

        if (!item.isChecked()) {
            switch (item.getItemId()) {
                case R.id.nav_dashboard:
                    newFragment = DashboardFragment.newInstance();
                case R.id.nav_admin_add:
                    // TODO if this ever gets implemented
                    break;
                case R.id.nav_search_offline:
                    newFragment = SearchOfflineFragment.newInstance("", "");
                    break;
                case R.id.nav_search_online:
                    newFragment = SearchOnlineFragment.newInstance();
                    break;
                case R.id.nav_search_options:
                    newFragment = SearchOptionsFragment.newInstance();
                    break;
                case R.id.nav_account_settings:
                    newFragment = AccountSettingsFragment.newInstance();
                    break;
                case R.id.nav_logout:
                    Intent logout = new Intent(this, LoginActivity.class);
                    logout.putExtra(LoginActivity.EXTRA_LOGOUT, true);
                    startActivity(logout);
                    finish();
                    break;
            }
        }

        if (newFragment != null) {
            fm.beginTransaction()
                    .replace(R.id.contentFragment, newFragment)
                    .addToBackStack(null)
                    .commit();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onAccountUpdated() {
        // display user Name and Email in nav header
        View headerView = ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0);
        ((TextView) headerView.findViewById(R.id.navHeaderName)).setText(SavedLogin.getInstance().getName());
        ((TextView) headerView.findViewById(R.id.navHeaderEmail)).setText(SavedLogin.getInstance().getEmail());
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}

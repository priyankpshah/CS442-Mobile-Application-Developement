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
        SearchOfflineFragment.OnFragmentInteractionListener,
        SearchOnlineFragment.OnFragmentInteractionListener{

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
                // select correct menu entry on back pressed
                if (fm.getBackStackEntryCount() > 0) {
                    FragmentManager.BackStackEntry top = fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1);

                    switch (top.getName()) {
                        case "dashboard":
                            navigationView.setCheckedItem(R.id.nav_dashboard);
                            break;
                        case "search_offline":
                            navigationView.setCheckedItem(R.id.nav_search_offline);
                            break;
                        case "search_online":
                            navigationView.setCheckedItem(R.id.nav_search_online);
                            break;
                        case "search_options":
                            navigationView.setCheckedItem(R.id.nav_search_options);
                            break;
                        case "account_settings":
                            navigationView.setCheckedItem(R.id.nav_account_settings);
                            break;
                    }
                }
            }
        });
        // display DashboardFragment initially
        Fragment dashboard = DashboardFragment.newInstance();
        fm.beginTransaction().replace(R.id.contentFragment, dashboard).commit();

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
        if (!item.isChecked()) {
            switch (item.getItemId()) {
                // TODO convert to only one beginTransaction() if possible in future
                case R.id.nav_dashboard:
                    Fragment dashboard = DashboardFragment.newInstance();
                    fm.beginTransaction()
                            .replace(R.id.contentFragment, dashboard)
                            .addToBackStack("dashboard")
                            .commit();
                    break;
                case R.id.nav_admin_add:
                    // TODO
                    break;
                case R.id.nav_search_offline:
                    Fragment searchOffline = SearchOfflineFragment.newInstance("", "");
                    fm.beginTransaction()
                            .replace(R.id.contentFragment, searchOffline)
                            .addToBackStack("search_offline")
                            .commit();
                    break;
                case R.id.nav_search_online:
                    Fragment searchOnline = SearchOnlineFragment.newInstance("", "");
                    fm.beginTransaction()
                            .replace(R.id.contentFragment, searchOnline)
                            .addToBackStack("search_online")
                            .commit();
                    break;
                case R.id.nav_search_options:
                    Fragment searchOptions = SearchOptionsFragment.newInstance();
                    fm.beginTransaction()
                            .replace(R.id.contentFragment, searchOptions)
                            .addToBackStack("search_options")
                            .commit();
                    break;
                case R.id.nav_account_settings:
                    Fragment accountSettings = AccountSettingsFragment.newInstance();
                    fm.beginTransaction()
                            .replace(R.id.contentFragment, accountSettings)
                            .addToBackStack("account_settings")
                            .commit();
                    break;
                case R.id.nav_logout:
                    Intent logout = new Intent(this, LoginActivity.class);
                    logout.putExtra(LoginActivity.EXTRA_LOGOUT, true);
                    startActivity(logout);
                    finish();
                    break;
            }
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

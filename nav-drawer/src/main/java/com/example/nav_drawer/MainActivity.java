package com.example.nav_drawer;

import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // UI elements
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    // Fragments
    // Declare fragment handled by Navigation Drawer
    private Fragment mNewsFragment;
    private Fragment mProfileFragment;
    private Fragment mSettingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configure all views
        configureToolBar();
        configureDrawerLayout();
        configureNavigationView();

        showFirstFragment();
    }

    @Override
    public void onBackPressed() {
        // Handle back click to close menu
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle Navigation Item Click
        int id = item.getItemId();

        // Show fragment after user clicked on a menu item
        switch (id){
            case R.id.activity_main_drawer_news :
                showNewsFragment();
                break;
            case R.id.activity_main_drawer_profile:
                showProfileFragment();
                break;
            case R.id.activity_main_drawer_settings:
                showSettingsFragment();
                break;
            default:
                break;
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }


    private void configureToolBar(){
        mToolbar = findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(mToolbar);
    }

    // Configure the Drawer Layout
    private void configureDrawerLayout(){
        mDrawerLayout = findViewById(R.id.activity_main_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    // Configure the NavigationView
    private void configureNavigationView(){
        mNavigationView = findViewById(R.id.activity_main_nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
    }


    // Create each fragment page and show it

    private void showNewsFragment(){
        if (mNewsFragment == null) mNewsFragment = new NewsFragment();
        startTransactionFragment(mNewsFragment);
    }

    private void showSettingsFragment(){
        if (mSettingsFragment == null) mSettingsFragment = new SettingsFragment();
        startTransactionFragment(mSettingsFragment);
    }

    private void showProfileFragment(){
        if (mProfileFragment == null) mProfileFragment = new ProfileFragment();
        startTransactionFragment(mProfileFragment);
    }

    // Generic method that will replace and show a fragment inside the MainActivity Frame Layout
    private void startTransactionFragment(Fragment fragment){
        if (!fragment.isVisible()){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.activity_main_frame_layout, fragment)
                    .commit();
        }
    }

    // Show the first fragment when the activity is created
    private void showFirstFragment(){
        Fragment visibleFragment = getSupportFragmentManager().findFragmentById(R.id.activity_main_frame_layout);
        if (visibleFragment == null){
            // Show News Fragment
            showNewsFragment();
            // Mark as selected the menu item corresponding to NewsFragment
            mNavigationView.getMenu().getItem(0).setChecked(true);
        }
    }
}

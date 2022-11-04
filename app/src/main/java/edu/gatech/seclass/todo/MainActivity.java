package edu.gatech.seclass.todo;

import static cn.bmob.v3.Bmob.getApplicationContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import edu.gatech.seclass.todo.Tool.SP;
import edu.gatech.seclass.todo.widget.RippleWrapper;
import edu.gatech.seclass.todo.widget.TickProgressBar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private MenuItem mMenuItemIDLE;
    private Button mBtnStart;
    private Button mBtnPause;
    private Button mBtnResume;
    private Button mBtnStop;
    private Button mBtnSkip;
    private TextView mTextCountDown;
    private TextView mTextTimeTile;
    private TickProgressBar mProgressBar;
    private RippleWrapper mRippleWrapper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);

                if (mMenuItemIDLE != null && newState == DrawerLayout.STATE_IDLE) {
                    runNavigationItemSelected(mMenuItemIDLE);
                    mMenuItemIDLE = null;
                }
            }
        };

        mDrawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mBtnStart = (Button)findViewById(R.id.btn_start);
        mBtnPause = (Button)findViewById(R.id.btn_pause);
        mBtnResume = (Button)findViewById(R.id.btn_resume);
        mBtnStop = (Button)findViewById(R.id.btn_stop);
        mBtnSkip = (Button)findViewById(R.id.btn_skip);
        mTextCountDown = (TextView)findViewById(R.id.text_count_down);
        mTextTimeTile = (TextView)findViewById(R.id.text_time_title);
        mProgressBar = (TickProgressBar)findViewById(R.id.tick_progress_bar);
        mRippleWrapper = (RippleWrapper)findViewById(R.id.ripple_wrapper);

        initActions();
    }

    private void initActions() {
       mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mBtnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

      mBtnResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mBtnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

      mBtnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mRippleWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mMenuItemIDLE = item;
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }

        return true;
    }

    private void runNavigationItemSelected(@NonNull MenuItem item) {//侧面抽屉的选中状态
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_setting:

                break;
            case R.id.nav_schedule:

                break;
            case R.id.nav_info:

                break;
            case R.id.nav_exit:
                new SP(getApplicationContext()).SaveUser("","",false,false);
                //new SP(getApplicationContext()).SetRem(false);
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,Login.class);
                MainActivity.this.startActivity(intent);
                MainActivity.this.finish();
                break;
        }
    }

}
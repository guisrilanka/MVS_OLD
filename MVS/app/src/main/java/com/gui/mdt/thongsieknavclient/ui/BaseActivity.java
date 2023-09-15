
package com.gui.mdt.thongsieknavclient.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.appcompat.*;
import android.view.View;

import com.gui.mdt.thongsieknavclient.*;
import com.gui.mdt.thongsieknavclient.R;

/**
 * Created by aungm on 27/2/2017.
 */

public abstract  class BaseActivity extends AppCompatActivity {

    protected abstract int getLayoutResource();
    protected abstract  void handleHomeAsUpEvent();
    protected Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        configureToolbar();
    }

    private void configureToolbar()
    {
        mToolBar = (Toolbar) findViewById(R.id.toolbar);

        //custom toolbar found; set it as support action bar;
        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
        }

        if(getSupportActionBar() != null)
        {
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home: {
                handleHomeAsUpEvent();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
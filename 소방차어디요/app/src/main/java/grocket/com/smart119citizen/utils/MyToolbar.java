package grocket.com.smart119citizen.utils;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import grocket.com.smart119citizen.R;


/**
 * Created by chokyounglae on 15. 8. 5..
 */
public class MyToolbar {
    AppCompatActivity myActivity;
    Toolbar mToolbar;


    public MyToolbar(AppCompatActivity myActivity, Toolbar mToolbar) {
        this.myActivity = myActivity;
        this.mToolbar = mToolbar;
    }

    public void setToolbar(String title) {
        myActivity.setSupportActionBar(mToolbar);
        myActivity.setTitle(title);
        android.support.v7.app.ActionBar actionBar = myActivity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
    }

    public void setToolbar(String title, int indicatorResId) {
        myActivity.setSupportActionBar(mToolbar);
        myActivity.setTitle(title);

        android.support.v7.app.ActionBar actionBar = myActivity.getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeAsUpIndicator(indicatorResId);
    }

    public void setToolbarWithBackKey(String title) {
        setToolbarWithBackKey(title, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myActivity.finish();
            }
        });
    }

    public void setToolbarWithBackKey(String title, View.OnClickListener onClickListener) {
        this.mToolbar.setTitle(title);
        this.mToolbar.setTitleTextColor(Color.WHITE);
        this.mToolbar.setNavigationIcon(R.drawable.ico_back);
        this.mToolbar.setNavigationOnClickListener(onClickListener);
    }
}

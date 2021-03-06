package com.csm.fblogin.facebooklogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.csm.fblogin.facebookapi.FacebookCustomApi;
import com.facebook.FacebookSdk;
import com.facebook.login.widget.LoginButton;

/*
author - sagar nayak
date- 18 july 2016
desc - this is a class which implements the facebook lib and can be used as an example to implement this in another class.
 */
public class MainActivity extends AppCompatActivity {

    FacebookCustomApi facebookCustomApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(MainActivity.this);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        facebookCustomApi = new FacebookCustomApi(MainActivity.this, loginButton);

        /*
        button to get the data fetched from facebook api in a arraylist.
         */
        ((Button) findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("log", "" + facebookCustomApi.getValues());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 64206) { //requst code for facebook api
            Log.i("log", "facebook api result : " + requestCode + " " + resultCode + " " + data);
            facebookCustomApi.ActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

package com.csm.fblogin.facebookapi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by sagar on 7/29/2016.
 * library project to do the facebook api work in android project.
 * this lib does the internal work in getting the data from facebook api.
 * How To Use -
 *    create facebook app id for your app and set it in your app manifest.
 *    use the login button fragment in your layout to place the facebook login button-
 *                 <com.facebook.login.widget.LoginButton
 *                 android:id="@+id/login_button"
 *                 android:layout_width="wrap_content"
 *                 android:layout_height="wrap_content"
 *                 android:layout_centerHorizontal="true"
 *                 android:layout_centerVertical="true" />
 *   write this line before setContentView - FacebookSdk.sdkInitialize(MainActivity.this);
 *   create a instance of FacebookCustomApi and pass activity context and Facebook Login Button Instance
 *                                 - FacebookCustomApi facebppkapi = new FacebookCutomApi(MainActivity.this,loginbutton);
 *   in onActivityResult() -
 *                                  if (requestCode == 64206) { //requst code for facebook api
 *                                       facebppkapi.ActivityResult(requestCode, resultCode, data);
 *                                   }
 *  now you can use the reference of the facebookcustomapi to get the values through function, after you login.
 */
public class FacebookCustomApi {

    Context c;
    LoginButton loginButton;
    CallbackManager callbackManager;

    ArrayList<HashMap<String, String>> values;

    public FacebookCustomApi(Context c, LoginButton facebookloginButton) {

        this.c = c;
        this.loginButton = facebookloginButton;
        callbackManager = CallbackManager.Factory.create();
        AppEventsLogger.activateApp(c);
        values = new ArrayList<HashMap<String, String>>();
        doLoginListener();
    }

    public void doLoginListener() {
        LoginManager.getInstance().logInWithReadPermissions((Activity) c, Arrays.asList("public_profile", "email", "user_friends"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i("log", "successfully logged in");
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            Log.i("log", "access token : " + AccessToken.getCurrentAccessToken().getPermissions().toString() + " profile : " + Profile.getCurrentProfile());
                            GraphRequest request = GraphRequest.newMeRequest(
                                    AccessToken.getCurrentAccessToken(),
                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(
                                                JSONObject object,
                                                GraphResponse response) {
                                            // Application code
                                            Log.i("log", "login result : " + object.toString());
                                            values = new ArrayList<HashMap<String, String>>();
                                            HashMap<String, String> temp = new HashMap<String, String>();
                                            try {
                                                temp.put("email", object.getString("email").toString());
                                                values.add(temp);
                                                temp.put("name", object.getString("name").toString());
                                                values.add(temp);
                                                temp.put("id", object.getString("id").toString());
                                                values.add(temp);
                                                temp.put("first_name", object.getString("first_name").toString());
                                                values.add(temp);
                                                temp.put("link", object.getString("link").toString());
                                                values.add(temp);
                                                temp.put("gender", object.getString("gender").toString());
                                                values.add(temp);
                                                temp.put("locale", object.getString("locale").toString());
                                                values.add(temp);
                                                temp.put("verified", object.getString("verified").toString());
                                                values.add(temp);
                                                JSONObject jsonfriends = object.getJSONObject("friends");
                                                temp.put("total_Friends_count", jsonfriends.getJSONObject("summary").getString("total_count"));
                                                values.add(temp);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "email,name,id,first_name,link,gender,locale,verified,friends");
                            request.setParameters(parameters);
                            request.executeAsync();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }

            @Override
            public void onCancel() {
                Log.i("log", "cancel logging in");
            }

            @Override
            public void onError(FacebookException error) {
                Log.i("log", "error : " + error.toString());
            }
        });
    }

    /*
    this function is called from the calling activity when the onActivityResult is called. to call the callback manager function.
     */
    public void ActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /*
    this function is called when we want to logout of the logged in facebook account.
     */
    public void logoutFromFacebook() {
        if (LoginManager.getInstance() != null) {
            LoginManager.getInstance().logOut();
            Log.i("log", "logged out of the device");
        } else {
            Log.i("log", "the loginmanager instance is null");
        }
    }

    /*
    get all the values that are fetched from the facebook api.
     */
    public ArrayList<HashMap<String, String>> getValues() {
        return values;
    }
}

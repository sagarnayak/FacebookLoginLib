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


    public void ActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void logoutFromFacebook() {
        LoginManager.getInstance().logOut();
        Log.i("log", "logout");
    }

    public ArrayList<HashMap<String, String>> getValues() {
        return values;
    }
}

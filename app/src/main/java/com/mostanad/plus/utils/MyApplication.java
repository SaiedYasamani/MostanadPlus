package com.mostanad.plus.utils;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mostanad.plus.activity.CategoryActivity;
import com.mostanad.plus.activity.SplashActivity;
import com.mostanad.plus.activity.VideoActivity;
import com.mostanad.plus.receiver.SmsListener;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import ir.adonet.analytics.ADAnalytics;
import ir.mono.monolyticsdk.Monolyitcs;
import me.cheshmak.android.sdk.core.Cheshmak;
import me.cheshmak.android.sdk.core.CheshmakConfig;

import static com.mostanad.plus.utils.Constants.PERSON_INFO;

public class MyApplication extends Application {


    private SmsListener smsListener;

    private SharedPreferences personInfo;
    private static FirebaseAnalytics sAnalytics;


    @Override
    public void onCreate() {
        super.onCreate();

        personInfo = getApplicationContext().getSharedPreferences(PERSON_INFO, MODE_PRIVATE);

        ADAnalytics.initialize(this);


        if (Utilities.IsAutoDetect()){
            smsListener = new SmsListener(null,null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                registerReceiver(smsListener, new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));
            }
        }

        CheshmakConfig config = new CheshmakConfig();
        config.setIsEnableAutoActivityReports(true);
        config.setIsEnableExceptionReporting(true);
        Cheshmak.with(getApplicationContext(), config);
        Cheshmak.initTracker("YlflRL1naecTFUOe7VCk0A==");

        OneSignal.startInit(this)
                .setNotificationOpenedHandler(new NotificationHandler())
                .autoPromptLocation(true)
                .init();

        Monolyitcs.init(this,"EC17B556-8DAA-4486-8B51-FD53DD150B03", "061722f7-2a65-447a-a709-42af9cf4f23f");

        sAnalytics = FirebaseAnalytics.getInstance(this);
        FirebaseAnalytics.getInstance(this);
        FirebaseMessaging.getInstance().subscribeToTopic("MostandPlus");
    }

    private class NotificationHandler implements OneSignal.NotificationOpenedHandler {

        @Override
        public void notificationOpened(OSNotificationOpenResult result) {

            // OSNotificationAction.ActionType actionType = result.action.type;
            JSONObject additionalData = result.notification.payload.additionalData;
            Log.e("Additional Data is", String.valueOf(additionalData));
            if (additionalData != null) {
                try {
                    if (additionalData.has("action")) {

                        if (additionalData.getString("action").contentEquals("video")) {
                            Log.e("MESAJ:", "Additionaldata action is video");

                            String name = additionalData.getString("name");
                            String description = additionalData.getString("description");
                            String like = additionalData.getString("like");
                            String visit = additionalData.getString("visit");
                            String id = additionalData.getString("id");
                            String file_url = additionalData.getString("file_url");

                            Intent intent = new Intent(getApplicationContext(), VideoActivity.class);
                            intent.putExtra("name", name);
                            intent.putExtra("description", description);
                            intent.putExtra("like", like);
                            intent.putExtra("visit", visit);
                            intent.putExtra("id", id);
                            intent.putExtra("file_url", file_url);
                            intent.putExtra("label","notif");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                        } else if (additionalData.getString("action").contentEquals("category")) {
                            Log.e("MESAJ:", "Additionaldata action is category");

                            String categoryID = additionalData.getString("catID");
                            String categoryName = additionalData.getString("catName");

                            Intent intent = new Intent(getApplicationContext(), CategoryActivity.class);
                            intent.putExtra("categoryId", categoryID);
                            intent.putExtra("categoryName", categoryName);
                            intent.putExtra("label","notif");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                        else if (additionalData.getString("action").contentEquals("link")) {
                            try {
                                String url = additionalData.getString("url");
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                browserIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                                getApplicationContext().startActivity(browserIntent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        /*
                        else {
                            Log.d("MESAJ:", "Additionaldata action is nothing");

                            Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivity(intent);
                        }
                        */
                    } else {
                        Log.e("MESAJ:", "Additionaldata action is nothing");

                        Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                        intent.putExtra("label","notif");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }

                } catch (JSONException e) {
                    // e.printStackTrace();
                    Log.e("Push Exception : ", String.valueOf(e));
                }
            }else {
                Log.e("MESAJ eles:", "Additionaldata action is nothing");
                Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                intent.putExtra("label","notif");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }




        }

    }

    @Override
    public void onTerminate() {
        if (Utilities.IsAutoDetect() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && smsListener != null)
            unregisterReceiver(smsListener);
        super.onTerminate();
    }

    public SmsListener getSmsListener() {
        return smsListener;
    }

    public static FirebaseAnalytics getAnalytics() {
        return sAnalytics;
    }
}
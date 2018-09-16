package com.mostanad.plus.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONException;
import org.json.JSONObject;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;

import static android.content.Context.MODE_PRIVATE;

public class AnalyticHandler {
    private static final String prefName = "parsa_branch";
    private final static String PREF_CAMP = "~campaign";
    private final static String PREF_CHANNEL = "~channel";

    private final AnalyticHandlerCallback mAnalyticHandler;
    private Activity mActivity;

    public AnalyticHandler(Activity activity, AnalyticHandlerCallback mAnalyticHandler) {
        this.mAnalyticHandler = mAnalyticHandler;
        this.mActivity = activity;

        handle();

    }


    private void handle() {
        if (mActivity.getIntent() != null && mActivity.getIntent().getData() != null) {
//            ADAnalytics.countSession(mActivity);
            Branch branch = Branch.getInstance();
            branch.initSession(new Branch.BranchReferralInitListener() {
                @SuppressLint("ApplySharedPref")
                @Override
                public void onInitFinished(JSONObject referringParams, BranchError error) {

                    if (error == null && referringParams != null) {
                        try {
                            String params = referringParams.toString();
                            Log.d("AnalyticHandler", "deep link params: " + params);


                            String camp = null;
                            if (referringParams.has(PREF_CAMP))
                                camp = referringParams.getString(PREF_CAMP);
                            String channel = null;
                            if (referringParams.has(PREF_CHANNEL))
                                channel = referringParams.getString(PREF_CHANNEL);


                            SharedPreferences sharedPreferences = mActivity.getSharedPreferences(prefName, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            if (camp != null)
                                editor.putString(PREF_CAMP, camp);
                            if (channel != null)
                                editor.putString(PREF_CHANNEL, channel);

                            editor.commit();
                            mAnalyticHandler.onFinish();
                        } catch (JSONException e) {
                            mAnalyticHandler.onFinish();

                        }

                    } else {

                        Log.d("AnalyticHandler", "branch error: " + error.toString());
                        mAnalyticHandler.onFinish();
                    }

                }
            }, mActivity.getIntent().getData(), mActivity);
        } else
            mAnalyticHandler.onFinish();
    }


    public static void logRegister(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(prefName, MODE_PRIVATE);

        String camp = sharedPreferences.getString(PREF_CAMP, "no_camp");
        String channel = sharedPreferences.getString(PREF_CHANNEL, "no_channel");
        FirebaseAnalytics.getInstance(context).logEvent(camp + "_" + channel + "_register", null);

    }

    public static void logActivate(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(prefName, MODE_PRIVATE);

        String camp = sharedPreferences.getString(PREF_CAMP, "no_camp");
        String channel = sharedPreferences.getString(PREF_CHANNEL, "no_channel");
        FirebaseAnalytics.getInstance(context).logEvent(camp + "_" + channel + "_activate", null);

    }

}

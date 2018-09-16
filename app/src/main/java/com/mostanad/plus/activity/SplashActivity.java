package com.mostanad.plus.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.gson.JsonElement;
import com.mostanad.plus.BuildConfig;
import com.mostanad.plus.R;
import com.mostanad.plus.helper.AnalyticHandler;
import com.mostanad.plus.helper.AnalyticHandlerCallback;
import com.mostanad.plus.helper.RestCallBack;
import com.mostanad.plus.helper.RestHelper;
import com.mostanad.plus.utils.Constants;
import com.mostanad.plus.utils.Utilities;
import com.onesignal.OneSignal;
import com.worldsnas.forceupdate.ForceUpdateService;

import java.io.File;

import co.ronash.pushe.Pushe;

import static com.mostanad.plus.utils.Constants.IS_ICON_CREATED;
import static com.worldsnas.forceupdate.ForceUpdateService.CURRENT_VERSION;
import static com.worldsnas.forceupdate.ForceUpdateService.VERSION_CHECK_URL;


public class SplashActivity extends AppCompatActivity {

    private SharedPreferences personInfo;
    private SharedPreferences.Editor editor;

    private RestHelper restHelper;
    private String phone;
    private boolean isActive;

    private File file;
    private int version;
    long fileSizeDownloaded = 0;
    private ProgressDialog pDialog;
    private AlertDialog versionCheckDialog;
    private Intent homeIntent;

    @Override
    protected void onStart() {
        super.onStart();
//        ADAnalytics.countSession(this);
        super.onStart();
        new AnalyticHandler(this, new AnalyticHandlerCallback() {
            @Override
            public void onFinish() {
                openOtherActivity();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (Utilities.withPushe()) {
            Pushe.initialize(getApplicationContext(), false);
        }


    }

    private void openOtherActivity() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        restHelper = new RestHelper();

        personInfo = getApplicationContext().getSharedPreferences(Constants.PERSON_INFO, MODE_PRIVATE);
        editor = personInfo.edit();

        phone = personInfo.getString(Constants.PHONE_NUMBER, "");
        isActive = personInfo.getBoolean(Constants.IS_ACTIVE, false);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                editor.putString(Constants.PLAYER_ID, userId).apply();
            }
        });

        if (!personInfo.getBoolean(IS_ICON_CREATED, false)) {
            addShortcut();
            personInfo.edit().putBoolean(IS_ICON_CREATED, true).apply();
        }

        homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

//        getVersion();
        Intent firstCheckDownload = new Intent(Intent.ACTION_SYNC, null, SplashActivity.this, ForceUpdateService.class);
        Bundle bundle = new Bundle();

        bundle.putString(VERSION_CHECK_URL, "http://mostanadplus.com/api/v1/latest_version");
        bundle.putInt(CURRENT_VERSION, BuildConfig.VERSION_CODE);

        firstCheckDownload.putExtras(bundle);
        startService(firstCheckDownload);
        loadPage();
    }

    private boolean checkInternetConenction() {
        // get Connectivity Manager object to check connection
        ConnectivityManager connec
                = (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connec.getNetworkInfo(0).getState() ==
                android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() ==
                        android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() ==
                        android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {
            return true;
        } else if (
                connec.getNetworkInfo(0).getState() ==
                        android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() ==
                                android.net.NetworkInfo.State.DISCONNECTED) {

            return false;
        }
        return false;
    }

    private void loadPage() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (checkInternetConenction()) {
                    if ((phone.equals("")) || !phone.equals("") && !isActive) {
//                        if (Utilities.IsWebSiteVersion())
                        getTehranMessage();
//                        else {
//                            goToLogin();
//                        }
                    }
                    if (!phone.equals("") && isActive) {
                        goToMain();
                    }
                } else {
                    Intent mainIntent = new Intent(SplashActivity.this, InternetActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
            }
        }, 2000);
    }

    private void getTehranMessage() {
        restHelper.GetSpecialMessage(Constants.BASE_URL + Constants.RELATIVE_URL_SPECIAL_MESSAGE, new RestCallBack.ResponseVisitFinishListener() {
            @Override
            public void onFinish(JsonElement response) {
                if (response.getAsJsonObject().has(Constants.SPECIAL_MSG)) {
                    editor.putString(Constants.SPECIAL_MSG, response.getAsJsonObject().get(Constants.SPECIAL_MSG).getAsString()).apply();
                }
                if (response.getAsJsonObject().has(Constants.BTN_TXT)) {
                    editor.putString(Constants.BTN_TXT, response.getAsJsonObject().get(Constants.BTN_TXT).getAsString()).apply();
                }

                goToLogin();
            }
        }, new RestCallBack.ResponseErrorListener() {
            @Override
            public void onError(String error) {
                goToLogin();
            }
        });
    }

    private void goToLogin() {
        if (phone.equals("")) {
            Intent mainIntent = new Intent(SplashActivity.this, RegisterActivity.class);
            Log.d("SplashActivity :", "Register");
            startActivity(mainIntent);
            finish();
        }
        if (!phone.equals("") && !isActive) {
            Intent mainIntent = new Intent(SplashActivity.this, ActivateActivity.class);
            startActivity(mainIntent);
            Log.d("SplashActivity :", "Activate");
            finish();
        }
    }

    private void goToMain() {
        Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private void addShortcut() {
        Intent shortcutIntent = new Intent(getApplicationContext(), SplashActivity.class);
        shortcutIntent.setAction(Intent.ACTION_MAIN);
        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(getApplicationContext(),
                R.drawable.ic_launcher));
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        addIntent.putExtra("duplicate", false);  //may it's already there so don't duplicate
        getApplicationContext().sendBroadcast(addIntent);
    }

//    private void getVersion() {
//        try {
//            version = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        restHelper.CheckVersion(version, new RestCallBack.ResponseVersionCheckFinishListener() {
//            @Override
//            public void onFinish(final VersionCheckModel response) {
//                if (!response.getResponse().isUptodate()) {
//                    onCreateDialog();
////                    pDialog.hide();
//
//                    LayoutInflater layoutInflaterAndroid = LayoutInflater.from(SplashActivity.this);
//                    View mView = layoutInflaterAndroid.inflate(R.layout.dialog_download, null);
//                    AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(SplashActivity.this);
//                    alertDialogBuilderUserInput.setView(mView);
//                    alertDialogBuilderUserInput.setCancelable(false)
//                            .setPositiveButton("دانلود", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialogBox, int id) {
//                                    pDialog.show();
//                                    downloadUpdate(response.getResponse().getUrl());
//                                    versionCheckDialog.hide();
//                                }
//                            })
//
//                            .setNegativeButton("لغو",
//                                    new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialogBox, int id) {
//                                            // do nothing
//                                            finish();
//                                            startActivity(homeIntent);
//                                        }
//                                    });
//
//                    versionCheckDialog = alertDialogBuilderUserInput.create();
//
//                    versionCheckDialog.show();
//                    Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/shabnam.ttf");
//                    versionCheckDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTypeface(typeface);
//                    versionCheckDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTypeface(typeface);
//                } else {
//                    loadPage();
//                }
//            }
//        }, new RestCallBack.ResponseErrorListener() {
//            @Override
//            public void onError(String error) {
//                loadPage();
//            }
//        });
//    }

//    private void downloadUpdate(String url) {
//        restHelper.DownloadFile(url, new RestCallBack.ResponseGetIpFinishListener() {
//            @Override
//            public void onFinish(ResponseBody response) {
//                file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/Animatoon." + version + ".apk");
//                boolean writtenToDisk = writeResponseBodyToDisk(response);
//                if (writtenToDisk) {
//                    Intent promptInstall = new Intent(Intent.ACTION_VIEW)
//                            .setDataAndType(Uri.fromFile(file),
//                                    "application/vnd.android.package-archive");
//                    startActivity(promptInstall);
//                    finish();
//                }
//            }
//        }, new RestCallBack.ResponseErrorListener() {
//            @Override
//            public void onError(String error) {
//            }
//        });
//    }

//    protected void onCreateDialog() {
//
//        pDialog = new ProgressDialog(this);
//        pDialog.setMessage("درحال دانلود. لطفا منتظر بمانید...");
//        pDialog.setIndeterminate(false);
//
//        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        pDialog.setCancelable(false);
////        pDialog.show();
//
////        TextView textView = (TextView) pDialog.findViewById(android.R.id.message);
////        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/shabnam.ttf");
////        textView.setTypeface(face);
////        textView.setTextSize(12);
////
////        Button btn2 = (Button) pDialog.findViewById(android.R.id.button2);
////        btn2.setBackgroundColor(Color.parseColor("#f39803"));
////        btn2.setTypeface(face);
////        btn2.setTextSize(11);
//    }

//    private boolean writeResponseBodyToDisk(ResponseBody body) {
//        try {
//            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/Animatoon." + version + ".apk");
//
//            InputStream inputStream = null;
//            OutputStream outputStream = null;
//
//            try {
//                byte[] fileReader = new byte[4096];
//
//                long fileSize = body.contentLength();
//
//                inputStream = body.byteStream();
//                outputStream = new FileOutputStream(file);
//
//                while (true) {
//                    int read = inputStream.read(fileReader);
//
//                    if (read == -1) {
//                        break;
//                    }
//
//                    outputStream.write(fileReader, 0, read);
//
//                    fileSizeDownloaded += read;
//                    final int percent = (int) ((fileSizeDownloaded * 100) / fileSize);
//                    pDialog.setProgress(percent);
//                }
//
//                outputStream.flush();
//
//                return true;
//            } catch (IOException e) {
//                return false;
//            } finally {
//                if (inputStream != null) {
//                    inputStream.close();
//                }
//
//                if (outputStream != null) {
//                    outputStream.close();
//                }
//            }
//        } catch (IOException e) {
//            return false;
//        }
//    }
}

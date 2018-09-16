package com.mostanad.plus.utils;

import android.util.Base64;

import com.mostanad.plus.helper.HashGeneratorUtils;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.TimeZone;

public class Utilities {
    public static String randomString(int len) {

        final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();

        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    public static String signGeneration(String url, LinkedHashMap<String, String> params, String nonce) {

        byte[] data = new byte[0];
        try {
            data = url.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String remain = "";
        Set<String> keys = params.keySet();
        for (String k : keys) {
            remain += k + "=" + params.get(k) + "&";
        }
        if (remain.length() > 0)
            remain = remain.substring(0, remain.length() - 1);
        String base64 = Base64.encodeToString(data, Base64.DEFAULT);
        String s = nonce + Constants.SECRET_KEY + Constants.API_KEY + base64 + remain;
        s = s.replace("\n", "").replace("\r", "");
        String sha256Hash = HashGeneratorUtils.generateSHA256(s);
        return sha256Hash;
    }

    public static String getTimeAgo(long timestamp) {

        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();//get your local time zone.
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        sdf.setTimeZone(tz);//set time zone.
        String localTime = sdf.format(new Date(timestamp * 1000));
        Date date = new Date();
        try {
            date = sdf.parse(localTime);//get local date
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date == null) {
            return null;
        }

        long time = date.getTime();

        Date curDate = currentDate();
        long now = curDate.getTime();
        if (time > now || time <= 0) {
            return null;
        }

        int timeDIM = getTimeDistanceInMinutes(time);

        String timeAgo = null;

        if (timeDIM == 0) {
            timeAgo = "1 دقیقه";
        } else if (timeDIM == 1) {
            timeAgo = "1 دقیقه";
        } else if (timeDIM >= 2 && timeDIM <= 44) {
            timeAgo = timeDIM + " دقیقه";
        } else if (timeDIM >= 45 && timeDIM <= 89) {
            timeAgo = "1 ساعت";
        } else if (timeDIM >= 90 && timeDIM <= 1439) {
            timeAgo = "" + (Math.round(timeDIM / 60)) + " ساعت";
        } else if (timeDIM >= 1440 && timeDIM <= 2519) {
            timeAgo = "1 روز";
        } else if (timeDIM >= 2520 && timeDIM <= 43199) {
            timeAgo = (Math.round(timeDIM / 1440)) + " روز";
        } else if (timeDIM >= 43200 && timeDIM <= 86399) {
            timeAgo = "1 ماه";
        } else if (timeDIM >= 86400 && timeDIM <= 525599) {
            timeAgo = (Math.round(timeDIM / 43200)) + " ماه";
        } else if (timeDIM >= 525600 && timeDIM <= 655199) {
            timeAgo = "1 سال";
        } else if (timeDIM >= 655200 && timeDIM <= 914399) {
            timeAgo = "1 سال";
        } else if (timeDIM >= 914400 && timeDIM <= 1051199) {
            timeAgo = "2 سال";
        } else {
            timeAgo = "" + (Math.round(timeDIM / 525600)) + " سال";
        }

        return timeAgo + " پیش";
    }

    public static Date currentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    public static int getTimeDistanceInMinutes(long time) {
        long timeDistance = currentDate().getTime() - time;
        return Math.round((Math.abs(timeDistance) / 1000) / 60);
    }

    public static boolean isNumber(String str) {
        if (str != null) {
            int size = str.length();

            for (int i = 0; i < size; i++) {
                if (!Character.isDigit(str.charAt(i))) {
                    return false;
                }
            }

            return size > 0;
        } else
            return false;
    }

    public static boolean IsAutoDetect() {
        return true;
//        return false;
    }

    public static boolean IsWebSiteVersion() {
//        return true;
        return false;
    }

    public static String GetSensub() {
        if (IsAutoDetect())
            return "0";
        else
            return "1";
    }

    public static String GetAds() {
//        if (GetSensub()=="1" || GetSensub().equals("1"))
        return "0";
//        else
//            return "1";
    }

    public static boolean withPushe() {
        return false;
    }

    public static String getVerType() {

        if (IsWebSiteVersion())
            return "wnad";
        if (IsAutoDetect()) {
            return "ad";
        }
        else
            return "nad";
    }


    public static String getAdline() {
        if (IsAdline2())
            return "2";
        return "1";
    }

    public static boolean IsAdline2() {
        return true;
    }
}

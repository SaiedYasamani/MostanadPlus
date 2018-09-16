package com.mostanad.plus.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import com.mostanad.plus.interfaces.OnNewMessageListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static android.content.Context.MODE_PRIVATE;
import static com.mostanad.plus.utils.Constants.PERSON_INFO;
import static com.mostanad.plus.utils.Constants.PREF_REGEX;


public class SmsListener extends BroadcastReceiver {


    private String senderNum;
    private String message = "";
    private String regex = null;
    String message2 = null;

    OnNewMessageListener onNewMessageListener;


    public SmsListener(OnNewMessageListener onNewMessageListener, String regex) {
        this.onNewMessageListener = onNewMessageListener;
        this.regex = regex;
    }

    public SmsListener() {

    }

    public void onReceive(Context context, Intent intent) {


        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();

        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                if (pdusObj != null) {
                    for (int i = 0; i < pdusObj.length; i++) {
                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);

                        senderNum = smsMessage.getDisplayOriginatingAddress();
                        message += smsMessage.getMessageBody();
                    }
                    Log.e("SmsReceiver", "senderNum: " + senderNum + "; message: " + message);

                    if (message.contains("مستندپلاس") ||
                            message.contains("mostanadplus") || senderNum.contains("1000273794") ||
                            senderNum.contains("30007002222222") || senderNum.contains("500095360") ||
                            senderNum.contains("50001272") || senderNum.contains("982000102030") ||
                            senderNum.contains("2000102030") || senderNum.contains("3000881038") ||
                            senderNum.contains("408020") || senderNum.contains("98408020") ||
                            senderNum.contains("+98408020") || senderNum.contains("8938008")) {

                        regex = getRegex(context);
                        if (regex != null) {
                            Matcher matcher = Pattern.compile(regex).matcher(message);
                            if (matcher.find()) {
                                message2 = matcher.group();
                            }
                        } else if (senderNum.contains("8938008") || senderNum.contains("408020")) {

                            String text = message.replaceFirst("\\d+$", "");
                            if (senderNum.contains("408020")) {
                                message2 = getNumber(message, 4);
                            } else {
                                message2 = message.replace(text, "");
                            }

                        }
                        if (message2 == null) {
                            Matcher matcher = Pattern.compile("/^(\\d+)/").matcher(message);
                            if (matcher.find()) {
                                String temp = matcher.group();
                                int length = temp.length();
                                if (length == 4 || length == 5)
                                    message2 = temp;
                            }
                        }

                        if (message2 == null) {
                            message2 = findFirstNumber(message);
                        }
                        if (message2 == null) {
                            message2 = message.replaceAll("\\D+", "");
                        }
                        if (message2 != null) {
                            Log.d("sms Listener", "retrieved sms from sms listener: "+ message2);
                        }
                        if (onNewMessageListener != null)
                            onNewMessageListener.onNewMessageRecived(message2);
                    }

                }
            }

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);

        }
    }

    private String getNumber(String sms, int chars) {
        try {
            return findCode(sms, chars);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String findCode(String sms, int chars) {
        String text = sms.replaceAll("\\d+$", "");
        String code = sms.replace(text, "");
        int lastIndex;
        if (code.length() == chars)
            return code;

        StringBuilder codeBuilder = new StringBuilder();
        lastIndex = sms.lastIndexOf("کد ");
        if (lastIndex != -1) {
            String codeMessage = sms.substring(sms.lastIndexOf("کد "));
            for (int i = 0; i < codeMessage.length(); i++) {
                char singleChar = codeMessage.charAt(i);

                if (isNumeric(String.valueOf(singleChar)))
                    codeBuilder.append(singleChar);
            }

            if (codeBuilder.length() == chars)
                return codeBuilder.toString();
        }
        StringBuilder activeBuilder = new StringBuilder();
        lastIndex = sms.lastIndexOf("فعال سازی");
        if (lastIndex != -1) {
            String activeMessage = sms.substring(lastIndex);
            for (int i = 0; i < activeMessage.length(); i++) {
                char singleChar = activeMessage.charAt(i);

                if (isNumeric(String.valueOf(singleChar)))
                    activeBuilder.append(singleChar);
            }

            if (activeBuilder.length() == chars)
                return activeBuilder.toString();
        }
        if (chars < 6) {
            return findCode(sms, (chars + 1));
        }

        return null;
    }

    private String findFirstNumber(String sms) {
        StringBuilder builder = new StringBuilder();
        boolean shouldReturn = false;
        for (int i = 0; i < sms.length(); i++) {
            char index = sms.charAt(i);
            if (isNumeric("" + index)) {
                shouldReturn = true;
                builder.append(index);
            } else {
                if (shouldReturn)
                    break;
            }
        }
        if (builder.length() == 4 || builder.length() == 5)
            return builder.toString();
        else
            return null;
    }

    private boolean isNumeric(String value) {
        return value.matches("\\d+");
    }

    public void setOnNewMessageListener(OnNewMessageListener onNewMessageListener) {
        if (message2 != null && message2.length() > 1) {
            Log.d("====>", "setOnNewMessageListener" + message2);
            onNewMessageListener.onNewMessageRecived(message2);
        } else
            this.onNewMessageListener = onNewMessageListener;
    }

    private String getRegex(Context context) {
        return context.getSharedPreferences(PERSON_INFO, MODE_PRIVATE).getString(PREF_REGEX, null);
    }

}

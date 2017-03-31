package com.santhosh.jobportal.data;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.santhosh.jobportal.provider.DBHelper;
import com.santhosh.jobportal.provider.JobPortalProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by AUS8KOR on 3/27/2017.
 */

public class Util {
    public static String loadJSONFromAsset(Context context, String filename) {
        String json = null;
        try {

            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static boolean checkPermission(Context context, String permission) {
        boolean bRes = false;

        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            bRes = true;
        }

        return bRes;
    }

    public static boolean isUserLoggedOut(Context context) {
        boolean bResult;
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        bResult = myPrefs.getBoolean("bLogout", true);
        return bResult;
    }

    public static void updateSignOutScenario(Context context, boolean bLogout, int userid) {
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor e = myPrefs.edit();
        //storing preference values & access anywhere in programme
        e.putBoolean("bLogout", bLogout);
        e.putInt("userID", userid);
        e.apply();
    }

    public static int getCurrentUserID(Context cxt) {
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(cxt);
        return myPrefs.getInt("userID", -1);
    }

    public static int isEmployer(Context cxt, int accountId) {
        int bResult = 0;
        Cursor cursor = null;
        ContentResolver resolver = cxt.getContentResolver();

        String selectionClause = DBHelper.ACCOUNT_ID + "=?";
        String[] selectionArgs = new String[]{"" + accountId};

        try {
            cursor = resolver.query(JobPortalProvider.ACCOUNT_URI, null, selectionClause, selectionArgs, null);
            if ((cursor != null) && cursor.moveToFirst()) {
                bResult = cursor.getInt(cursor.getColumnIndex(DBHelper.ACCOUNT_IS_EMPLOYER));
            }
        } finally {
            if ((cursor != null) && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return bResult;
    }

    public static String convertToDate(long timestamp) {
        long difference = 0;
        Long mDate = System.currentTimeMillis();

        if (mDate > timestamp) {
            difference = mDate - timestamp;
            final long seconds = difference / 1000;
            final long minutes = seconds / 60;
            final long hours = minutes / 60;
            final long days = hours / 24;
            final long months = days / 31;
            final long years = days / 365;

            if (seconds < 0) {
                return "not yet";
            } else if (seconds < 60) {
                return seconds == 1 ? "one second ago" : seconds + " seconds ago";
            } else if (seconds < 120) {
                return "a minute ago";
            } else if (seconds < 3600) // 60 * 60
            {
                return minutes + " minutes ago";
            } else if (seconds < 7200) // 120 * 60 i.e <2 hours
            {
                return "an hour ago";
            } else if (seconds < 86400) // 24 * 60 * 60
            {
                return hours + " hours ago";
            } else if (seconds < 172800) // 48 * 60 * 60
            {
                return "yesterday";
            } else if (seconds < 2592000) // 30 * 24 * 60 * 60
            {
                return days + " days ago";
            } else if (seconds < 31104000) // 12 * 30 * 24 * 60 * 60
            {
                return months <= 1 ? "one month ago" : days + " months ago";
            } else {
                return years <= 1 ? "one year ago" : years + " years ago";
            }
        }
        return null;
    }


    public static HashMap<String, HashMap<String, JSONArray>> getAllNotiInfo(Context cxt) {
        HashMap<String, HashMap<String, JSONArray>> map = new HashMap<>();
        SharedPreferences pSharedPref = PreferenceManager.getDefaultSharedPreferences(cxt);
        if (pSharedPref != null) {
            String jsonString = pSharedPref.getString("noti_ops", (new JSONObject()).toString());
            try {
                JSONObject jsonObj = new JSONObject(jsonString);
                Iterator<String> keysItr = jsonObj.keys();
                while (keysItr.hasNext()) {
                    String key = keysItr.next();
                    String child = jsonObj.getString(key);
                    JSONObject childObj = new JSONObject(child);
                    Iterator<String> childKeys = childObj.keys();
                    HashMap<String, JSONArray> childMap = new HashMap<>();
                    while (childKeys.hasNext()) {
                        String childKey = childKeys.next();
                        JSONArray value = childObj.getJSONArray(childKey);
                        childMap.put(childKey, value);
                    }
                    map.put(key, childMap);
                }
            } catch (JSONException e) {
                Log.d(Constants.TAG, "getAllNotiInfo:: JSONException: " + e);
            }

        }
        return map;
    }

    /**
     * handleNotiUpdates
     *
     * @param ops
     */
    public static HashMap<String, JSONArray> handleNotiUpdates(Context cxt, int ownerId, int jobId,
                                                               int applicantId, int score, int jobStatus, int ops) {
        HashMap<String, JSONArray> accMap = new HashMap<String, JSONArray>();
        SharedPreferences pSharedPref = PreferenceManager.getDefaultSharedPreferences(cxt);
        HashMap<String, HashMap<String, JSONArray>> allNotiMap = getAllNotiInfo(cxt);

        switch (ops) {
            case Constants.NotiOps.SAVE:
                HashMap<String, JSONArray> childMap = new HashMap<String, JSONArray>();
                JSONArray childArr = new JSONArray();
                if (allNotiMap.containsKey("" + ownerId)) {
                    //get job ID
                    childMap = allNotiMap.get("" + ownerId);
                    if (childMap.containsKey("" + jobId)) {
                        //check for new applicant
                        childArr = childMap.get("" + jobId);
                        JSONArray childObj = new JSONArray();
                        childObj.put(applicantId);
                        childObj.put(jobStatus);
                        if(jobStatus == Constants.JobStatus.QUIZ_SUBMIT){
                            childObj.put(score);
                        }
                        if (childArr.toString().contains(childObj.toString())) {
                            return accMap;
                        } else {
                            childArr.put(childObj);
                        }
                    } else {
                        //no owner id scenarion i.e new account
                        JSONArray childObj = new JSONArray();
                        childObj.put(applicantId);
                        childObj.put(jobStatus);
                        if(jobStatus == Constants.JobStatus.QUIZ_SUBMIT){
                            childObj.put(score);
                        }
                        childArr.put(childObj);
                    }
                    childMap.put("" + jobId, childArr);
                } else {
                    //no owner id scenarion i.e new account
                    JSONArray childObj = new JSONArray();
                    childObj.put(applicantId);
                    childObj.put(jobStatus);
                    if(jobStatus == Constants.JobStatus.QUIZ_SUBMIT){
                        childObj.put(score);
                    }
                    childArr.put(childObj);
                    childMap.put("" + jobId, childArr);
                }
                allNotiMap.put("" + ownerId, childMap);

                if (pSharedPref != null) {
                    JSONObject jsonObject = new JSONObject(allNotiMap);
                    String jsonString = jsonObject.toString();
                    SharedPreferences.Editor editor = pSharedPref.edit();
                    editor.remove("noti_ops").apply();
                    editor.putString("noti_ops", jsonString);
                    editor.apply();
                }
                break;
            case Constants.NotiOps.REMOVE:
                if (allNotiMap.containsKey("" + ownerId)) {
                    //get job ID
                    childMap = allNotiMap.get("" + ownerId);
                    if (childMap.containsKey("" + jobId)) {
                        //remove job item from current account
                        childMap.remove(""+jobId);
                    }
                    allNotiMap.put("" + ownerId, childMap);
                    if (pSharedPref != null) {
                        JSONObject jsonObject = new JSONObject(allNotiMap);
                        String jsonString = jsonObject.toString();
                        SharedPreferences.Editor editor = pSharedPref.edit();
                        editor.remove("noti_ops").apply();
                        editor.putString("noti_ops", jsonString);
                        editor.apply();
                    }
                }
                break;
            case Constants.NotiOps.UPDATE:
                break;
            case Constants.NotiOps.GET:
                if (allNotiMap.containsKey("" + ownerId)) {
                    accMap = allNotiMap.get("" + ownerId);
                }
                break;
            default:
                break;
        }
        return accMap;
    }
}

package com.santhosh.jobportal.network;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.santhosh.jobportal.data.Account;
import com.santhosh.jobportal.data.JobItem;
import com.santhosh.jobportal.provider.DBHelper;
import com.santhosh.jobportal.provider.JobPortalProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AUS8KOR on 3/26/2017.
 */

public class ApiInterface {

    private Context mContext;

    public ApiInterface(Context context) {
        mContext = context;
    }

    /**
     * GET ACCOUNT INFO
     *
     * @param selectionClause
     * @param selectionArgs
     * @return
     */
    public Account getAccountInfo(String selectionClause, String[] selectionArgs) {
        Account account = null;
        Cursor cursor = null;
        ContentResolver resolver = mContext.getContentResolver();
        try {
            cursor = resolver.query(JobPortalProvider.ACCOUNT_URI, null, selectionClause, selectionArgs, null);
            if ((cursor != null) && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndex(DBHelper.ACCOUNT_ID));
                int bEmployer = cursor.getInt(cursor.getColumnIndex(DBHelper.ACCOUNT_IS_EMPLOYER));
                String name = cursor.getString(cursor.getColumnIndex(DBHelper.ACCOUNT_NAME));
                String org = cursor.getString(cursor.getColumnIndex(DBHelper.ACCOUNT_ORGANISATION));
                String org_address = cursor.getString(cursor.getColumnIndex(DBHelper.ACCOUNT_ORG_ADDRESS));
                account = new Account(id, name, bEmployer, org, org_address);
            }
        } finally {
            if ((cursor != null) && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return account;
    }

    /**
     * Authenticate
     *
     * @param username of user
     * @param password of user
     */
    public Account authenticate(String username, String password) {
        String selectionClause;
        Account account;

        try {
            Long.valueOf(username);
            selectionClause = DBHelper.ACCOUNT_MOBILE_NUMBER + "=?"
                    + " AND " + DBHelper.ACCOUNT_PASSWORD + "=?";
        } catch (Exception e) {
            selectionClause = DBHelper.ACCOUNT_EMAIL + "=?"
                    + " AND " + DBHelper.ACCOUNT_PASSWORD + "=?";
        }
        String[] selectionArgs = new String[]{username, password};

        //get account info
        account = getAccountInfo(selectionClause, selectionArgs);

        return account;
    }

    /**
     * GET JOB ITEM
     *
     * @param jobId job id
     * @return job item
     */
    public JobItem getJobItem(int jobId) {
        JobItem item = null;
        Cursor cursor = null;
        ContentResolver resolver = mContext.getContentResolver();
        try {
            cursor = resolver.query(JobPortalProvider.JOB_URI, null, DBHelper.JOB_ID + " =?",
                    new String[]{"" + jobId}, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndex(DBHelper.JOB_ID));
                    String name = cursor.getString(cursor.getColumnIndex(DBHelper.JOB_TITLE));
                    String org = cursor.getString(cursor.getColumnIndex(DBHelper.JOB_COMPANY));
                    String org_address = cursor.getString(cursor.getColumnIndex(DBHelper.JOB_LOC));
                    String exp = cursor.getString(cursor.getColumnIndex(DBHelper.JOB_EXP));
                    String describe = cursor.getString(cursor.getColumnIndex(DBHelper.JOB_DESCRIPTION));
                    long date = cursor.getLong(cursor.getColumnIndex(DBHelper.JOB_DATE));
                    String skills = cursor.getString(cursor.getColumnIndex(DBHelper.JOB_SKILL));
                    int ownerID = cursor.getInt(cursor.getColumnIndex(DBHelper.JOB_OWNER));
                    String status = cursor.getString(cursor.getColumnIndex(DBHelper.JOB_STATUS));
                    String appliers = cursor.getString(cursor.getColumnIndex(DBHelper.JOB_APPLICANTS));

                    item = new JobItem(id, name, org, exp, org_address, skills,
                            date, describe, ownerID, appliers, status);
                }
            }
        } finally {
            if ((cursor != null) && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return item;
    }

    /**
     * getMatchedJobs
     *
     * @param keyword for any skills
     */
    public List<JobItem> getMatchedJobs(String keyword) {
        Cursor cursor = null;
        List<JobItem> jobList = new ArrayList<>();

        //String selectionClause = DBHelper.JOB_SKILL + " LIKE ?" + " OR " + DBHelper.JOB_TITLE + " LIKE ?";
        //String[] selectionArgs = new String[]{"%" + keyword + "%", "%" + keyword + "%"};
        String selectionClause = null;
        String[] selectionArgs = null;
        if (!TextUtils.isEmpty(keyword)) {
            selectionClause = DBHelper.JOB_SKILL + " LIKE ?" + " OR " + DBHelper.JOB_TITLE + " LIKE ?";
            selectionArgs = new String[]{"%" + keyword + "%", "%" + keyword + "%"};
        }

        ContentResolver resolver = mContext.getContentResolver();
        try {
            cursor = resolver.query(JobPortalProvider.JOB_URI, null, selectionClause, selectionArgs, DBHelper.JOB_ID + " DESC");

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndex(DBHelper.JOB_ID));
                    String name = cursor.getString(cursor.getColumnIndex(DBHelper.JOB_TITLE));
                    String org = cursor.getString(cursor.getColumnIndex(DBHelper.JOB_COMPANY));
                    String org_address = cursor.getString(cursor.getColumnIndex(DBHelper.JOB_LOC));
                    String exp = cursor.getString(cursor.getColumnIndex(DBHelper.JOB_EXP));
                    String describe = cursor.getString(cursor.getColumnIndex(DBHelper.JOB_DESCRIPTION));
                    long date = cursor.getLong(cursor.getColumnIndex(DBHelper.JOB_DATE));
                    String skills = cursor.getString(cursor.getColumnIndex(DBHelper.JOB_SKILL));
                    int ownerID = cursor.getInt(cursor.getColumnIndex(DBHelper.JOB_OWNER));
                    String status = cursor.getString(cursor.getColumnIndex(DBHelper.JOB_STATUS));
                    String appliers = cursor.getString(cursor.getColumnIndex(DBHelper.JOB_APPLICANTS));

                    JobItem item = new JobItem(id, name, org, exp, org_address, skills,
                            date, describe, ownerID, appliers, status);
                    jobList.add(item);
                }
            }
        } finally {
            if ((cursor != null) && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return jobList;
    }

    public int updateJobItem(int jobID, ContentValues cv) {
        int nCount = mContext.getContentResolver().update(JobPortalProvider.JOB_URI, cv,
                DBHelper.JOB_ID + "=?", new String[]{String.valueOf(jobID)});

        return nCount;
    }
}

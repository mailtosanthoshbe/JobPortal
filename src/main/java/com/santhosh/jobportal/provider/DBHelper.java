package com.santhosh.jobportal.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.santhosh.jobportal.data.Constants;

import java.io.File;

/**
 * Created by AUS8KOR on 3/26/2017.
 */

public class DBHelper extends SQLiteOpenHelper {
    //Database name
    private static String DB_NAME = "JobPortal.db";

    /**
     * Version number of the database
     */
    private static int VERSION = 2;

    //Account table fields
    public static final String ACCOUNT_ID = "_id";
    public static final String ACCOUNT_NAME = "name";
    public static final String ACCOUNT_EMAIL = "email";
    public static final String ACCOUNT_PASSWORD = "password";
    public static final String ACCOUNT_MOBILE_NUMBER = "mobile";
    public static final String ACCOUNT_IS_EMPLOYER = "bEmployer";
    public static final String ACCOUNT_DOB = "bob";
    public static final String ACCOUNT_ORGANISATION = "organisation";
    public static final String ACCOUNT_ORG_ADDRESS = "orgAddress";
    public static final String ACCOUNT_CGPA = "cgpa";
    public static final String ACCOUNT_HSC = "hsc";
    public static final String ACCOUNT_SSLC = "sslc";

    //jobs
    public static final String JOB_ID       = "_id";
    public static final String JOB_TITLE    = "title";
    public static final String JOB_COMPANY  = "company";
    public static final String JOB_LOC      = "location";
    public static final String JOB_EXP      = "experience";
    public static final String JOB_SKILL    = "skill";
    public static final String JOB_DATE     = "date";
    public static final String JOB_DESCRIPTION = "description";
    public static final String JOB_OWNER    = "owner";
    public static final String JOB_APPLICANTS = "applicants";
    public static final String JOB_STATUS   = "status";


    //tables
    public static final String ACCOUNT_TABLE = "accounts";
    public static final String JOB_TABLE = "jobs";

    //create account table
    void createAccountTable(SQLiteDatabase db) {
        String sql = "create table " + ACCOUNT_TABLE + " ( " + ACCOUNT_ID +
                " integer primary key autoincrement , " + ACCOUNT_NAME + " text ," +
                ACCOUNT_EMAIL + " text ," +
                ACCOUNT_PASSWORD + " text ," +
                ACCOUNT_MOBILE_NUMBER + " integer ," +
                ACCOUNT_DOB + " text ," +
                ACCOUNT_ORGANISATION + " text ," +
                ACCOUNT_ORG_ADDRESS + " text ," +
                ACCOUNT_CGPA + " text ," +
                ACCOUNT_HSC + " text ," +
                ACCOUNT_SSLC + " text ," +
                ACCOUNT_IS_EMPLOYER + " boolean default 0 " + " ) ";

        db.execSQL(sql);
    }

    //create jobs table
    void createJOBTable(SQLiteDatabase db) {
        String sql = "create table " + JOB_TABLE + " ( " + JOB_ID +
                " integer primary key autoincrement , " + JOB_TITLE + " text ," +
                JOB_COMPANY + " text ," +
                JOB_LOC + " integer ," +
                JOB_EXP + " text ," +
                JOB_SKILL + " text ," +
                JOB_DATE + " integer ," +
                JOB_DESCRIPTION + " text ," +
                JOB_OWNER + " integer default 0 ," +
                JOB_APPLICANTS + " text ," +
                JOB_STATUS + " text" + " ) ";

        db.execSQL(sql);
    }

    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        /*super(context, Environment.getExternalStorageDirectory()
                + File.separator + Constants.TAG
                + File.separator + DB_NAME, null, VERSION);*/
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createAccountTable(db);
        createJOBTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ACCOUNT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + JOB_TABLE);
        onCreate(db);
    }
}
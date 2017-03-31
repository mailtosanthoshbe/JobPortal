package com.santhosh.jobportal.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

public class JobPortalProvider extends ContentProvider {

    public static final String PROVIDER_NAME = "com.santhosh.jobportal";

    /**
     * A uri to do operations on locations table. A content provider is identified by its uri
     */
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME);
    public static final Uri ACCOUNT_URI = Uri.parse("content://" + PROVIDER_NAME + "/accounts");
    public static final Uri JOB_URI = Uri.parse("content://" + PROVIDER_NAME + "/jobs");

    /**
     * Constant to identify the requested operation
     */
    private static final int ACCOUNT = 2;
    private static final int JOB = 3;
    private static final UriMatcher uriMatcher;
    public static final String LOGOUT_METHOD = "logout";
    public static final String SEARCH_METHOD = "search";

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "accounts", ACCOUNT);
        uriMatcher.addURI(PROVIDER_NAME, "jobs", JOB);
    }

    SQLiteDatabase mDB = null;

    @Override
    public boolean onCreate() {

        DBHelper dbHelper = new DBHelper(getContext());
        mDB = dbHelper.getWritableDatabase();


        return ((mDB != null) ? true : false);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri newUri = null;
        long id = 0;

        switch (uriMatcher.match(uri)) {
            case ACCOUNT:
                id = mDB.insert(DBHelper.ACCOUNT_TABLE, null, values);
                newUri = ContentUris.withAppendedId(uri, id);
                break;
            case JOB:
                id = mDB.insert(DBHelper.JOB_TABLE, null, values);
                newUri = ContentUris.withAppendedId(uri, id);
                break;
            default:
                break;
        }

        return newUri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;

        switch (uriMatcher.match(uri)) {
            case ACCOUNT:
                cursor = mDB.query(DBHelper.ACCOUNT_TABLE, projection, selection,
                        selectionArgs, null, null, null);
                break;
            case JOB:
                cursor = mDB.query(DBHelper.JOB_TABLE, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            default:
                break;
        }

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int id = 0;

        switch (uriMatcher.match(uri)) {
            case ACCOUNT:
                id = mDB.update(DBHelper.ACCOUNT_TABLE, values, selection, selectionArgs);
                break;
            case JOB:
                id = mDB.update(DBHelper.JOB_TABLE, values, selection, selectionArgs);
                break;
            default:
                break;
        }

        return id;
    }

    @Override
    public Bundle call(String method, String arg, Bundle extras) {
        Bundle bundle = null;

        switch (method) {
            case LOGOUT_METHOD:
                //reset databases
                mDB.beginTransaction();
                mDB.execSQL("DROP TABLE IF EXISTS " + DBHelper.ACCOUNT_TABLE);

                DBHelper dbHelper = new DBHelper(getContext());
                dbHelper.createAccountTable(mDB);

                mDB.setTransactionSuccessful();
                mDB.endTransaction();
                break;
            case SEARCH_METHOD:

                break;
            default:
                break;
        }
        return bundle;
    }
}

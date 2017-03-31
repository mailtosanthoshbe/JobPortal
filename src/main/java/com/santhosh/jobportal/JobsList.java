package com.santhosh.jobportal;

import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.santhosh.jobportal.adapter.RecyclerViewAdapter;
import com.santhosh.jobportal.data.Account;
import com.santhosh.jobportal.data.Constants;
import com.santhosh.jobportal.data.JobItem;
import com.santhosh.jobportal.data.Util;
import com.santhosh.jobportal.network.ApiInterface;
import com.santhosh.jobportal.provider.DBHelper;
import com.santhosh.jobportal.provider.JobPortalProvider;
import com.santhosh.jobportal.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class JobsList extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    //@Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;

    //recruiter screen
    //@Bind(R.id.et_job_name)
    EditText _nameText;
    //@Bind(R.id.et_job_exp)
    EditText _expText;
    //@Bind(R.id.et_job_skill)
    EditText _skillText;
    //@Bind(R.id.et_job_description)
    EditText _describeText;
    EditText _searchButton;
    Button _createButton;
    TextView _alertTv;

    RecyclerViewAdapter adapter;
    private List<JobItem> mList = new ArrayList<>();

    //private int mUserType;
    Account account;

    TextView _notiView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //check user info exists
        if (Util.isUserLoggedOut(this)) {
            launchLoginScreen();
            return;
        } else {
            //show list here
            int accountID = Util.getCurrentUserID(this);
            String selectionClause = DBHelper.ACCOUNT_ID + "=?";
            String[] selectionArgs = new String[]{"" + accountID};

            ApiInterface apiInf = new ApiInterface(this);
            account = apiInf.getAccountInfo(selectionClause, selectionArgs);
            if (account == null) {
                launchLoginScreen();
                return;
            }
        }

        switch (account.getEmployerType()) {
            case Constants.TYPE_JOBSEEKER:
                setContentView(R.layout.activity_list_jobs);
                setTitle("Jobs List");

                _searchButton = (EditText) findViewById(R.id.search_bar);
                _searchButton.setOnTouchListener(this);
                _alertTv = (TextView) findViewById(R.id.tv_alert);
                //UI
                mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
                mRecyclerView.setHasFixedSize(true);

                //set orientation to layout manager
                LinearLayoutManager lm = new LinearLayoutManager(this);
                lm.setOrientation(LinearLayoutManager.VERTICAL);
                mRecyclerView.setLayoutManager(lm);

                adapter = new RecyclerViewAdapter(this, mList, Constants.MODE_LIST);
                mRecyclerView.setAdapter(adapter);

                //show all jobs list
                fetchMatchedJobs();
                break;
            case Constants.TYPE_EMPLOYER:
                setContentView(R.layout.activity_create_job);
                setTitle("Create Job");

                //UI
                _nameText = (EditText) findViewById(R.id.et_job_name);
                _expText = (EditText) findViewById(R.id.et_job_exp);
                _skillText = (EditText) findViewById(R.id.et_job_skill);
                _describeText = (EditText) findViewById(R.id.et_job_description);
                _createButton = (Button) findViewById(R.id.btn_create_job);
                _createButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createJob();
                    }
                });

                //hardcoded
                _nameText.setText("Software Engineer");
                _expText.setText("2 Years");
                _skillText.setText("C, java");
                _describeText.setText("able to work in night shift");
                break;
            default:
                break;
        }
    }

    private boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String exp = _expText.getText().toString();
        String skill = _skillText.getText().toString();
        String describe = _describeText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (exp.isEmpty()) {
            _expText.setError("Enter experience");
            valid = false;
        } else {
            _expText.setError(null);
        }

        if (skill.isEmpty()) {
            _skillText.setError("Enter skills");
            valid = false;
        } else {
            _skillText.setError(null);
        }

        if (describe.isEmpty()) {
            _describeText.setError("Enter job description");
            valid = false;
        } else {
            _describeText.setError(null);
        }

        return valid;
    }

    private void onCreateFailed() {
        Toast.makeText(getBaseContext(), "Create failed", Toast.LENGTH_LONG).show();
        _createButton.setEnabled(true);
    }

    private void createJob() {
        Log.d(Constants.TAG, "Create Job");

        if (!validate()) {
            onCreateFailed();
            return;
        }
        _createButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Job...");
        progressDialog.show();

        final String name = _nameText.getText().toString();
        final String exp = _expText.getText().toString();
        final String skill = _skillText.getText().toString();
        final String describe = _describeText.getText().toString();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        //Store to local database
                        //insert account data
                        ContentValues values = new ContentValues();
                        values.put(DBHelper.JOB_TITLE, name);
                        values.put(DBHelper.JOB_COMPANY, account.getCompany());
                        values.put(DBHelper.JOB_EXP, exp);
                        values.put(DBHelper.JOB_SKILL, skill);
                        values.put(DBHelper.JOB_LOC, account.getAddress());
                        values.put(DBHelper.JOB_DESCRIPTION, describe);
                        values.put(DBHelper.JOB_DATE, System.currentTimeMillis());
                        values.put(DBHelper.JOB_OWNER, account.getUserId());
                        //values.put(DBHelper.JOB_STATUS, Constants.JobStatus.NEW);

                        Uri uri = getContentResolver().insert(JobPortalProvider.JOB_URI, values);
                        long nId = ContentUris.parseId(uri);
                        if (nId > 0) {
                            _createButton.setEnabled(true);
                            Toast.makeText(getBaseContext(), "Successfully created.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getBaseContext(), "Job creation failed", Toast.LENGTH_SHORT).show();
                            _createButton.setEnabled(true);
                        }
                        progressDialog.dismiss();
                    }
                }, 1000);
    }


    private void launchLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean isUserExists() {
        Cursor cursor = null;
        boolean bResult = false;
        try {
            cursor = getContentResolver().query(JobPortalProvider.ACCOUNT_URI, null, null, null, null);
            if ((cursor != null) && (cursor.getCount() > 0)) {
                bResult = true;
            }
        } finally {
            if ((cursor != null) && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return bResult;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        if (account.getEmployerType() == Constants.TYPE_EMPLOYER) {
            menu.findItem(R.id.action_refresh).setVisible(false);
        }

        MenuItem item = menu.findItem(R.id.badge);
        MenuItemCompat.setActionView(item, R.layout.feed_update_count);
        View view = MenuItemCompat.getActionView(item);
        _notiView = (TextView) view.findViewById(R.id.tv_noti_count);
        int cnt = Util.handleNotiUpdates(this, account.getUserId(), 0, 0, 0, 0, Constants.NotiOps.GET).size();
        if (cnt <= 0) {
            _notiView.setVisibility(View.GONE);
        }
        _notiView.setText(String.valueOf(cnt));
        _notiView.setOnClickListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_logout:
                //clear db values
                //getContentResolvero().call(JobPortalProvider.CONTENT_URI, JobPortalProvider.LOGOUT_METHOD, null, null);

                //set logout
                Util.updateSignOutScenario(this, true, -1);

                //launch login activity
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.action_refresh:
                fetchMatchedJobs();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_noti_count:
                //show list of updated Employer's job item
                Intent intent = new Intent(this, NotifiedActivity.class);
                intent.putExtra("type", account.getEmployerType());
                startActivityForResult(intent, 400);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            default:
                Intent i = new Intent(this, JobDetail.class);
                JobItem item = (JobItem) mList.get((int) v.getTag());
                i.putExtra("jobItem", item.getId());
                startActivityForResult(i, 500);
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int DRAWABLE_LEFT = 0;
        final int DRAWABLE_TOP = 1;
        final int DRAWABLE_RIGHT = 2;
        final int DRAWABLE_BOTTOM = 3;

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getRawX() >= (_searchButton.getRight() - _searchButton.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                fetchMatchedJobs();
                return true;
            }
        }
        return false;
    }

    private void fetchMatchedJobs() {
        final ProgressDialog progressDialog = new ProgressDialog(JobsList.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Fetching jobs...");
        progressDialog.show();

        new android.os.Handler().postDelayed(new Runnable() {
            public void run() {
                String searchTxt = _searchButton.getText().toString();
                //search in DB
                ApiInterface apiInf = new ApiInterface(JobsList.this);
                List<JobItem> list = apiInf.getMatchedJobs(searchTxt);

                mList.clear();
                if (list != null && !list.isEmpty()) {
                    Log.d(Constants.TAG, "job count: " + list.size());
                    mList.addAll(list);
                    _alertTv.setVisibility(View.GONE);
                } else {
                    _alertTv.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }
        }, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                if (requestCode == 400) {
                    //update noti count
                    int cnt = Util.handleNotiUpdates(this, account.getUserId(), 0, 0, 0, 0, Constants.NotiOps.GET).size();
                    if (cnt <= 0) {
                        _notiView.setVisibility(View.GONE);
                    }
                    _notiView.setText(String.valueOf(cnt));
                    _notiView.setOnClickListener(this);
                }
                break;
            default:
                break;
        }
    }

    MyAlertDialog dialog;

    @Override
    public void onBackPressed() {
        if (account.getEmployerType() == Constants.TYPE_EMPLOYER) {
            dialog = new MyAlertDialog(this, "Exit App",
                    "Are you sure you want to discard?", new MyAlertDialog.OnButtonClickListener() {
                @Override
                public void onMyAlertDlgButClick(int id, ContentValues values) {
                    switch (id) {
                        case R.id.custdlg_button_yes:
                            JobsList.super.onBackPressed();
                            break;
                        case R.id.custdlg_button_no:
                            dialog.dismiss();
                            break;
                        default:
                            break;
                    }
                }
            });
            dialog.show();
        } else {
            super.onBackPressed();
        }
    }
}

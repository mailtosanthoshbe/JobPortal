package com.santhosh.jobportal;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.santhosh.jobportal.data.Constants;
import com.santhosh.jobportal.data.JobItem;
import com.santhosh.jobportal.data.Util;
import com.santhosh.jobportal.network.ApiInterface;
import com.santhosh.jobportal.provider.DBHelper;
import com.santhosh.jobportal.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;


public class JobDetail extends AppCompatActivity {

    @Bind(R.id.tv_title)
    TextView tv_title;
    @Bind(R.id.tv_org)
    TextView tv_org;
    @Bind(R.id.tv_exp)
    TextView tv_exp;
    @Bind(R.id.tv_location)
    TextView tv_location;
    @Bind(R.id.tv_skill)
    TextView tv_skill;
    @Bind(R.id.tv_postdate)
    TextView tv_postdate;
    @Bind(R.id.et_jobDescription)
    EditText et_jobText;
    @Bind(R.id.but_apply)
    Button but_apply;

    private Map<Integer, Integer> convertStringToMap(String jobStatus) {
        Map<Integer, Integer> map = new HashMap<>();
        if (TextUtils.isEmpty(jobStatus)) {
            return map;
        }
        jobStatus = jobStatus.substring(1, jobStatus.length() - 1);           //remove curly brackets
        String[] keyValuePairs = jobStatus.split(",");              //split the string to creat key-value pairs

        for (String pair : keyValuePairs)                        //iterate over the pairs
        {
            String[] entry = pair.split("=");                   //split the pairs to get key and value
            map.put(Integer.parseInt(entry[0].trim()), Integer.parseInt(entry[1].trim()));          //add them to the hashmap and trim whitespaces
        }
        return map;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);
        ButterKnife.bind(this);

        //set Title
        setTitle("Job Detail");
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //final JobItem item = (JobItem) getIntent().getParcelableExtra("jobItem");
        int id = getIntent().getIntExtra("jobItem", -1);
        ApiInterface apiInf = new ApiInterface(this);
        final JobItem item = apiInf.getJobItem(id);

        if (item != null) {
            tv_title.setText(item.getTitle());
            tv_org.setText(item.getCompany());
            tv_exp.setText(item.getExperience());
            tv_location.setText(item.getLocation());
            tv_skill.setText(item.getSkills());
            tv_postdate.setText(Util.convertToDate(item.getDate()));
            et_jobText.setText(item.getJobDescription());

            final Map<Integer, Integer> statusMap = convertStringToMap(item.getStatus());
            final int accountId = Util.getCurrentUserID(JobDetail.this);
            //if(TextUtils.isEmpty(item.getApplicants())) {
            String appString = item.getApplicants();
            final String applicant[] = (appString == null) ? new String[]{} : appString.split(",");
            /*if (Arrays.asList(applicant).contains("" + accountId)) {
                if (!statusMap.isEmpty()) {
                    int status = statusMap.get(accountId);
                    switch (status) {
                        case Constants.JobStatus.SUBMIT:
                            but_apply.setText("Job applied");
                            but_apply.setEnabled(false);
                            break;
                        case Constants.JobStatus.SHORTLISTED:
                            break;
                        default:
                            break;
                    }
                }
            }*/


            but_apply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int status = Constants.JobStatus.NEW;
                    boolean bApplied = false;
                    if (Arrays.asList(applicant).contains("" + accountId)) {
                        if (!statusMap.isEmpty()) {
                            status = statusMap.get(accountId);
                            if (status != Constants.JobStatus.NEW) {
                                bApplied = true;
                            }
                        }
                    }
                    Log.d(Constants.TAG, "status: " + status);
                    Log.d(Constants.TAG, "bApplied: " + bApplied);
                    ContentValues cv = new ContentValues(2);
                    switch (status) {
                        case Constants.JobStatus.NEW:
                            statusMap.put(accountId, Constants.JobStatus.SUBMIT);
                            cv.put(DBHelper.JOB_STATUS, statusMap.toString());
                            String applicants = item.getApplicants();
                            if (TextUtils.isEmpty(applicants)) {
                                applicants = "" + accountId;
                            } else {
                                applicants = applicants.concat("," + accountId);
                            }
                            cv.put(DBHelper.JOB_APPLICANTS, applicants);

                            ApiInterface apiInf = new ApiInterface(JobDetail.this);
                            Log.d(Constants.TAG, "update item: " + cv);
                            int nCount = apiInf.updateJobItem(item.getId(), cv);
                            if (nCount > 0) {
                                item.setStatus(statusMap.toString());
                                but_apply.setText("Job applied");
                                but_apply.setEnabled(false);

                                //set noti info
                                Util.handleNotiUpdates(JobDetail.this, item.getOwnerId(), item.getId(),
                                        accountId, 0, Constants.JobStatus.SUBMIT, Constants.NotiOps.SAVE);
                            }
                            Toast.makeText(getBaseContext(), "Job Applied", Toast.LENGTH_SHORT).show();
                            break;
                        case Constants.JobStatus.SUBMIT:
                            Toast.makeText(getBaseContext(),
                                    "You have already applied for this job.", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            break;
                    }
                    /*ContentValues cv = new ContentValues(2);
                    boolean bApplied = false;
                    //int status = item.get;

                    //check for already applied
                    String applicant[] = item.getApplicants().split(",");
                    if(Arrays.asList(applicant).contains(""+item.getId())){
                        bApplied = true;
                        status
                    }

                    switch (item.getStatus()) {
                        case Constants.JobStatus.NEW:
                            cv.put(DBHelper.JOB_STATUS, Constants.JobStatus.SUBMIT);
                            if(bApplied) {
                                String applicants = item.getApplicants();
                                if (TextUtils.isEmpty(applicants)) {
                                    applicants = "" + Util.getCurrentUserID(JobDetail.this);
                                } else {
                                    applicants.concat("," + Util.getCurrentUserID(JobDetail.this));
                                }
                                cv.put(DBHelper.JOB_APPLICANTS, applicants);
                            }
                            ApiInterface apiInf = new ApiInterface(JobDetail.this);
                            apiInf.updateJobItem(item.getId(), cv);
                            Toast.makeText(getBaseContext(), "Job Applied", Toast.LENGTH_SHORT).show();
                            break;
                        case Constants.JobStatus.SUBMIT:
                            Toast.makeText(getBaseContext(),
                                    "You have already applied for this job.", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            break;

                    }*/

                }
            });
        }

    }

    /*private int getJobStatus(int accountId, String jobStatus) {
        int status = Constants.JobStatus.NEW;
        jobStatus = jobStatus.substring(1, jobStatus.length() - 1);           //remove curly brackets
        String[] keyValuePairs = jobStatus.split(",");              //split the string to creat key-value pairs
        Map<Integer, Integer> map = new HashMap<>();

        for (String pair : keyValuePairs)                        //iterate over the pairs
        {
            String[] entry = pair.split("=");                   //split the pairs to get key and value
            map.put(Integer.parseInt(entry[0].trim()), Integer.parseInt(entry[1].trim()));          //add them to the hashmap and trim whitespaces
        }

        if (!map.isEmpty()) {
            status = map.get(accountId);
        }
        return status;
    }*/

    private String[] convertStringToArray(String str) {
        String[] arr = str.split(",");
        return arr;
    }

    private String convertArrayToString(String[] array) {
        String str = "";
        for (int i = 0; i < array.length; i++) {
            str = str + array[i];
            // Do not append comma at the end of last element
            if (i < array.length - 1) {
                str = str + ",";
            }
        }
        return str;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

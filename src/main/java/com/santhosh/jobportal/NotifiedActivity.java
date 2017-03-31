package com.santhosh.jobportal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.santhosh.jobportal.adapter.RecyclerViewAdapter;
import com.santhosh.jobportal.data.Constants;
import com.santhosh.jobportal.data.JobItem;
import com.santhosh.jobportal.data.Quiz;
import com.santhosh.jobportal.data.Util;
import com.santhosh.jobportal.network.ApiInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NotifiedActivity extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.noti_recyclerView)
    RecyclerView mNotiView;

    private int empType;
    private boolean bQuiz;
    private HashMap<String, JSONArray> notiMap = new HashMap<>();
    private int mJobID;
    private RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noti_list);
        ButterKnife.bind(this);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        empType = getIntent().getIntExtra("type", -1);

        //UI
        mNotiView.setHasFixedSize(true);

        //set orientation to layout manager
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        mNotiView.setLayoutManager(lm);

        bQuiz = getIntent().getBooleanExtra("bQuiz", false);
        mJobID = getIntent().getIntExtra("jobId", -1);
        if (bQuiz) {
            setTitle("Online Test");
            String data = Util.loadJSONFromAsset(this, "quiz.json");
            List<Quiz> mQuizList = new ArrayList<>();
            if (!TextUtils.isEmpty(data)) {
                try {
                    JSONArray quizArr = new JSONArray(data);
                    for (int i = 0; i < quizArr.length(); i++) {
                        JSONObject quizItem = quizArr.getJSONObject(i);
                        String id = quizItem.getString("quizNumber");
                        String question = quizItem.getString("question");
                        String answer = quizItem.getString("answer");
                        String one = quizItem.getString("option1");
                        String two = quizItem.getString("option2");
                        String three = quizItem.getString("option3");
                        String four = quizItem.getString("option4");
                        String[] options = {one, two, three, four};
                        Quiz itm = new Quiz(id, question, answer, options);
                        mQuizList.add(itm);
                    }
                    adapter = new RecyclerViewAdapter(this, this, empType,
                            bQuiz, mQuizList, Constants.MODE_NOTI);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            setTitle("Job Notifications");
            notiMap = Util.handleNotiUpdates(this, Util.getCurrentUserID(this), 0, 0, 0, 0, Constants.NotiOps.GET);
            adapter = new RecyclerViewAdapter(this, this, notiMap, empType, Constants.MODE_NOTI);
        }
        mNotiView.setAdapter(adapter);

        /*switch (empType) {
            case Constants.TYPE_JOBSEEKER:
                break;
            case Constants.TYPE_EMPLOYER:
                notiMap = Util.handleNotiUpdates(this, Util.getCurrentUserID(this), 0, 0, 0, Constants.NotiOps.GET);
                adapter = new RecyclerViewAdapter(this, this, notiMap, empType, Constants.MODE_NOTI);
                mNotiView.setAdapter(adapter);
                break;
            default:
                break;
        }*/
    }

    @Override
    public void onClick(View v) {
        final int currentAccID = Util.getCurrentUserID(this);

        switch (v.getId()) {
            case R.id.noti_but_confirm:
                switch (empType) {
                    case Constants.TYPE_EMPLOYER:
                        int position = (int) v.getTag();
                        List<String> keyList = adapter.getNotiItemKeys();
                        List<JSONArray> valuesList = adapter.getNotiItemValues();
                        String jobId = keyList.get(position);
                        JSONArray notifiers = valuesList.get(position);

                        for (int i = 0; i < notifiers.length(); i++) {
                            try {
                                JSONArray item = (JSONArray) notifiers.get(i);
                                int accId = (int) item.get(0); //get account id
                                int status = (int) item.get(1);
                                if(status == Constants.JobStatus.QUIZ_SUBMIT){
                                    if(v.getTag(R.id.noti_but_confirm) != null){
                                        boolean select = (boolean) v.getTag(R.id.noti_but_confirm);
                                        if(select){
                                            Toast.makeText(this, "The candidate is selected.", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(this, "The candidate is rejected.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }else {
                                    //set noti info
                                    Util.handleNotiUpdates(NotifiedActivity.this, accId,
                                            Integer.parseInt(jobId), currentAccID, 0,
                                            Constants.JobStatus.SHORTLISTED, Constants.NotiOps.SAVE);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        //remove noti from current account
                        Util.handleNotiUpdates(NotifiedActivity.this, currentAccID,
                                Integer.parseInt(jobId), 0, 0, 0, Constants.NotiOps.REMOVE);

                        //after applying remove from list
                        valuesList.remove(position);
                        keyList.remove(position);
                        adapter.updateNotiKeys(keyList);
                        adapter.updateNotiValues(valuesList);
                        adapter.notifyDataSetChanged();
                        break;
                    case Constants.TYPE_JOBSEEKER:
                        if (bQuiz) {
                            final int score = (int) v.getTag();
                            final ProgressDialog progressDialog = new ProgressDialog(this,
                                    R.style.AppTheme_Dark_Dialog);
                            progressDialog.setIndeterminate(true);
                            progressDialog.setMessage("Submitting results...");
                            progressDialog.show();
                            new android.os.Handler().postDelayed(new Runnable() {
                                public void run() {
                                    ApiInterface apiInf = new ApiInterface(NotifiedActivity.this);
                                    JobItem jobItem = apiInf.getJobItem(mJobID);

                                    //update quiz submission status
                                    Util.handleNotiUpdates(NotifiedActivity.this, jobItem.getOwnerId(), jobItem.getId(),
                                            currentAccID, score, Constants.JobStatus.QUIZ_SUBMIT, Constants.NotiOps.SAVE);
                                    Toast.makeText(NotifiedActivity.this, "Your score " + score + " is submitted.",
                                            Toast.LENGTH_SHORT).show();
                                    //remove noti from current account
                                    Util.handleNotiUpdates(NotifiedActivity.this, currentAccID,
                                            mJobID, 0, 0, 0, Constants.NotiOps.REMOVE);
                                    progressDialog.dismiss();

                                    //send position to noti activity
                                    Intent i = new Intent();
                                    i.putExtra("jobId", mJobID);
                                    setResult(RESULT_OK, i);
                                    finish();
                                }
                            }, 1000);
                        } else {
                            int pos = (int) v.getTag();
                            List<String> list = adapter.getNotiItemKeys();
                            //show list of updated Employer's job item
                            Intent intent = new Intent(this, NotifiedActivity.class);
                            intent.putExtra("type", Constants.TYPE_JOBSEEKER);
                            intent.putExtra("bQuiz", true);
                            intent.putExtra("jobId", Integer.valueOf(list.get(pos)));
                            startActivityForResult(intent, 700);
                            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                        }
                        break;
                    default:
                        break;
                }
                break;
            case R.id.noti_but_back:
                if (bQuiz) {


                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                if (requestCode == 700) {
                    if (data == null) {
                        setResult(RESULT_OK);
                        finish();
                        return;
                    }
                    int jobId = data.getIntExtra("jobId", -1);
                    List<String> keyList = adapter.getNotiItemKeys();
                    List<JSONArray> valuesList = adapter.getNotiItemValues();
                    int position = keyList.indexOf("" + jobId);
                    valuesList.remove(position);
                    keyList.remove(position);
                    if (keyList.isEmpty()) {
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        adapter.updateNotiKeys(keyList);
                        adapter.updateNotiValues(valuesList);
                        adapter.notifyDataSetChanged();
                    }
                }
                break;
            default:
                break;
        }
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

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}

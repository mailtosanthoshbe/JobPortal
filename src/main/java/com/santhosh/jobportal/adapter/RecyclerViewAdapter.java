package com.santhosh.jobportal.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.santhosh.jobportal.data.Account;
import com.santhosh.jobportal.data.Constants;
import com.santhosh.jobportal.data.Quiz;
import com.santhosh.jobportal.data.Util;
import com.santhosh.jobportal.R;
import com.santhosh.jobportal.data.JobItem;
import com.santhosh.jobportal.network.ApiInterface;
import com.santhosh.jobportal.provider.DBHelper;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by AUS8KOR on 2/7/2017.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RVViewHolder> {
    private List<JobItem> mList;
    private View.OnClickListener mListener;
    //private HashMap<String, JSONArray> mNotiMap;
    private int mEmpType;
    private int mMode;
    private List<String> mNotiItemKeys;
    private List<JSONArray> mNotiItemValues;
    private Context mContext;
    private boolean bQuiz;
    private List<Quiz> mQuizList;
    private int mQuizScore;

    public RecyclerViewAdapter(Context cxt, View.OnClickListener listener,
                               HashMap<String, JSONArray> notiMap, int empType, int mode) {
        mListener = listener;
        //mNotiMap = notiMap;
        mEmpType = empType;
        mMode = mode;
        if (notiMap != null) {
            mNotiItemKeys = new ArrayList<String>(notiMap.keySet());
            mNotiItemValues = new ArrayList<JSONArray>(notiMap.values());
        }
        mContext = cxt;
    }

    public RecyclerViewAdapter(Context cxt, View.OnClickListener listener, int empType,
                               boolean bquiz, List<Quiz> quizList, int mode) {
        this(cxt, listener, null, empType, mode);
        bQuiz = bquiz;
        mQuizList = quizList;
    }

    public class RVViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_title, tv_org, tv_exp, tv_location, tv_skill, tv_postdate;
        public EditText et_jobText;
        Button but_submit, but_back;
        RadioGroup rg_options;

        public RVViewHolder(final View view) {
            super(view);
            if (mMode == Constants.MODE_NOTI) {
                //title
                tv_title = (TextView) view.findViewById(R.id.noti_tv_title);
                if (bQuiz) {
                    //hide label and description view
                    view.findViewById(R.id.noti_tv_label).setVisibility(View.GONE);
                    view.findViewById(R.id.noti_et_jd).setVisibility(View.GONE);

                    //show radio button
                    view.findViewById(R.id.noti_radio_options).setVisibility(View.VISIBLE);
                    rg_options = (RadioGroup) view.findViewById(R.id.noti_radio_options);

                    //back button
                    but_back = (Button) view.findViewById(R.id.noti_but_back);
                    but_back.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //v.setTag(getAdapterPosition());
                            //mListener.onClick(v);
                            if (mQuizList.size() == getAdapterPosition() - 1) {
                                v.setTag(mQuizScore);
                                mListener.onClick(v);
                            }
                            but_submit.setEnabled(false);
                            but_back.setEnabled(false);
                        }
                    });

                    //submit button
                    but_submit = (Button) view.findViewById(R.id.noti_but_confirm);
                    but_submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            int rdo_select = rg_options.getCheckedRadioButtonId();
                            if (rdo_select < 0) {
                                Toast.makeText(mContext, "Please select an option.", Toast.LENGTH_SHORT).show();
                            } else {
                                CharSequence selectedOption = ((RadioButton) view.findViewById(rdo_select))
                                        .getText();
                                String correctOption = mQuizList.get(getAdapterPosition()).getAnswer();
                                if (selectedOption.equals(correctOption)) {
                                    ++mQuizScore;
                                }
                                if (mQuizList.size() == getAdapterPosition() + 1) {
                                    v.setTag(mQuizScore);
                                    mListener.onClick(v);
                                }
                                but_submit.setEnabled(false);
                                but_back.setEnabled(false);
                            }
                            /*if(mQuizList.size() == getAdapterPosition()-1){
                                mQuizScore
                            }else{
                                ++mQuizScore;
                            }*/
                            //v.setTag(getAdapterPosition());
                            //mListener.onClick(v);
                        }
                    });

                } else {
                    //hide back button
                    view.findViewById(R.id.noti_but_back).setVisibility(View.GONE);
                    //label
                    tv_org = (TextView) view.findViewById(R.id.noti_tv_label);
                    //detail
                    et_jobText = (EditText) view.findViewById(R.id.noti_et_jd);
                    //submit button
                    but_submit = (Button) view.findViewById(R.id.noti_but_confirm);
                    but_submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            /*Object status = but_submit.getTag(R.id.noti_but_confirm);
                            if (status != null) {
                                v.setTag(R.id.noti_but_confirm, (boolean) status);
                                v.setTag(R.id.et_job_description, (int));
                            }*/
                            v.setTag(getAdapterPosition());

                            mListener.onClick(v);
                        }
                    });
                }

            } else {
                tv_title = (TextView) view.findViewById(R.id.tv_title);
                tv_org = (TextView) view.findViewById(R.id.tv_org);
                tv_exp = (TextView) view.findViewById(R.id.tv_exp);
                tv_location = (TextView) view.findViewById(R.id.tv_location);
                tv_skill = (TextView) view.findViewById(R.id.tv_skill);
                tv_postdate = (TextView) view.findViewById(R.id.tv_postdate);
                et_jobText = (EditText) view.findViewById(R.id.et_jobDescription);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setTag(getAdapterPosition());
                        mListener.onClick(v);
                    }
                });
            }
        }
    }

    public RecyclerViewAdapter(View.OnClickListener listener, List<JobItem> list, int mode) {
        mList = list;
        mMode = mode;
        mListener = listener;
    }

    @Override
    public RVViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (mMode == Constants.MODE_NOTI) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_notified, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_item_card, parent, false);
        }
        //view.setOnClickListener(mListener);
        return new RVViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RVViewHolder holder, int position) {
        if (mMode == Constants.MODE_NOTI) {
            TextView tv_title = holder.tv_title;
            TextView tv_label = holder.tv_org;
            EditText et_jd = holder.et_jobText;
            Button but_submit = holder.but_submit;
            Button but_back = holder.but_back;

            //quiz layout and return
            if (bQuiz) {
                Quiz item = mQuizList.get(position);
                tv_title.setText(item.getQuestion());
                for (int i = 0; i < holder.rg_options.getChildCount(); i++) {
                    ((RadioButton) holder.rg_options.getChildAt(i)).setText(item.getOptions()[i]);
                }
                return;
            }
            String jobId = mNotiItemKeys.get(position);
            JSONArray arrValues = mNotiItemValues.get(position);
            ApiInterface apiInf = new ApiInterface(mContext);
            JobItem jobItem = apiInf.getJobItem(Integer.parseInt(jobId));
            String applicants = "";
            tv_title.setText(jobItem.getTitle());
            switch (mEmpType) {
                case Constants.TYPE_EMPLOYER:
                    for (int i = 0; i < arrValues.length(); i++) {
                        try {
                            JSONArray item = (JSONArray) arrValues.get(i);
                            int accId = (int) item.get(0); //get account id
                            String selectionClause = DBHelper.ACCOUNT_ID + "=?";
                            String[] selectionArgs = new String[]{"" + accId};
                            Account account = apiInf.getAccountInfo(selectionClause, selectionArgs);
                            applicants += (i + 1) + ". " + account.getName() + "\n";
                            int status = (int) item.get(1); //get job status
                            switch (status) {
                                case Constants.JobStatus.SUBMIT:
                                    tv_label.setText("Job Applicants:");
                                    et_jd.setText(applicants);
                                    but_submit.setText("Call for Test");
                                    break;
                                case Constants.JobStatus.QUIZ_SUBMIT:
                                    int score = (int) item.get(2); //get Score
                                    tv_label.setText("The status of " + account.getName() + " is:");
                                    if (score >= 2) {
                                        et_jd.setTextColor(Color.GREEN);
                                        et_jd.setText("Score: " + score + "\n" + "Status: Passed");
                                        but_submit.setText("Send Offer Letter");
                                        but_submit.setTag(R.id.noti_but_confirm, true);
                                    } else {
                                        et_jd.setTextColor(Color.RED);
                                        et_jd.setText("Score: " + score + "\n" + "Status: Failed");
                                        but_submit.setText("Reject");
                                        but_submit.setTag(R.id.noti_but_confirm, false);
                                    }
                                    but_submit.setTag(R.id.et_job_description, Constants.JobStatus.CONFIRM);
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    break;
                case Constants.TYPE_JOBSEEKER: {
                    tv_label.setText("Job Description:");
                    et_jd.setText(jobItem.getJobDescription());
                    but_submit.setText("Start Test");
                }
                break;
                default:
                    break;
            }
        } else {
            TextView title = holder.tv_title;
            TextView company = holder.tv_org;
            TextView exp = holder.tv_exp;
            TextView location = holder.tv_location;
            TextView skill = holder.tv_skill;
            TextView date = holder.tv_postdate;


            title.setText(mList.get(position).getTitle());
            company.setText(mList.get(position).getCompany());
            exp.setText(mList.get(position).getExperience());
            location.setText(mList.get(position).getLocation());
            skill.setText(mList.get(position).getSkills());
            date.setText(Util.convertToDate(mList.get(position).getDate()));
        }
    }

    @Override
    public int getItemCount() {
        if (mMode == Constants.MODE_NOTI) {
            if (bQuiz) {
                return mQuizList.size();
            } else {
                return mNotiItemKeys.size();
            }
        } else {
            return mList.size();
        }
    }

    public void updateNotiKeys(List<String> keys) {
        mNotiItemKeys = keys;
    }

    public void updateNotiValues(List<JSONArray> values) {
        mNotiItemValues = values;
    }

    public List<String> getNotiItemKeys() {
        return mNotiItemKeys;
    }

    public List<JSONArray> getNotiItemValues() {
        return mNotiItemValues;
    }
}

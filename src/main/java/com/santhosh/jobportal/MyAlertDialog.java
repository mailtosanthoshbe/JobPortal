package com.santhosh.jobportal;


import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by aus8kor on 4/12/2016.
 */
public class MyAlertDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private OnButtonClickListener mCallback;
    private String mTitle;
    private String mMessage;

    public MyAlertDialog(Context context, String title, String message, OnButtonClickListener callback) {
        super(context);
        mContext = context;
        mCallback = callback;
        mTitle = title;
        mMessage = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(false);
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.custom_dialog, null);

        //inflate views
        TextView tvTitle = (TextView) dialogView.findViewById(R.id.custdlg_title);
        TextView tvMessage = (TextView) dialogView.findViewById(R.id.custdlg_msg);
        tvTitle.setText(mTitle);
        tvMessage.setText(mMessage);

        final Button btPositive = (Button) dialogView.findViewById(R.id.custdlg_button_yes);
        final Button btNegative = (Button) dialogView.findViewById(R.id.custdlg_button_no);
        btPositive.setOnClickListener(this);
        btNegative.setOnClickListener(this);
        btPositive.setText("Yes");
        btNegative.setText("No");

        setContentView(dialogView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.custdlg_button_yes:
            case R.id.custdlg_button_no:
                mCallback.onMyAlertDlgButClick(v.getId(), null);
                mCallback.onMyAlertDlgButClick(v.getId(), null);
                break;
            default:
                break;
        }

    }

    // Container Activity must implement this interface for onClick Event
    interface OnButtonClickListener {
        public void onMyAlertDlgButClick(int id, ContentValues values);
    }
}

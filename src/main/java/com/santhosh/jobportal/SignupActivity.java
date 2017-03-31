package com.santhosh.jobportal;

import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.santhosh.jobportal.data.Constants;
import com.santhosh.jobportal.provider.DBHelper;
import com.santhosh.jobportal.provider.JobPortalProvider;
import com.santhosh.jobportal.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignupActivity extends AppCompatActivity {

    @Bind(R.id.input_name)
    EditText _nameText;
    @Bind(R.id.input_address)
    EditText _addressText;
    @Bind(R.id.input_email)
    EditText _emailText;
    @Bind(R.id.input_mobile)
    EditText _mobileText;
    @Bind(R.id.input_password)
    EditText _passwordText;
    @Bind(R.id.input_reEnterPassword)
    EditText _reEnterPasswordText;
    @Bind(R.id.btn_signup)
    Button _signupButton;
    @Bind(R.id.link_login)
    TextView _loginLink;
    @Bind(R.id.input_cgpa)
    EditText _cgpaText;
    @Bind(R.id.input_hsc)
    EditText _hscText;
    @Bind(R.id.input_sslc)
    EditText _sslcText;
    @Bind(R.id.input_institute)
    EditText _companyText;
    @Bind(R.id.input_dob)
    EditText _dobText;

    @Bind(R.id.layout_cgpa)
    TextInputLayout _cgpaLayout;
    @Bind(R.id.layout_hsc)
    TextInputLayout _hscLayout;
    @Bind(R.id.layout_sslc)
    TextInputLayout _sslcLayout;

    private int userType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        userType = getIntent().getIntExtra("type", Constants.TYPE_JOBSEEKER);
        if (userType == Constants.TYPE_JOBSEEKER) {
            _cgpaLayout.setVisibility(View.VISIBLE);
            _hscLayout.setVisibility(View.VISIBLE);
            _sslcLayout.setVisibility(View.VISIBLE);
            _companyText.setHint("Institute Name");
        } else {
            _companyText.setHint("Organisation Name");
        }

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void signup() {
        Log.d(Constants.TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        final String name = _nameText.getText().toString();
        final String email = _emailText.getText().toString();
        final String mobile = _mobileText.getText().toString();
        final String password = _passwordText.getText().toString();
        final String dob = _dobText.getText().toString();
        final String org = _companyText.getText().toString();
        final String address = _addressText.getText().toString();



        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        //Store to local database
                        //insert account data
                        ContentValues values = new ContentValues();
                        values.put(DBHelper.ACCOUNT_NAME, name);
                        values.put(DBHelper.ACCOUNT_EMAIL, email);
                        values.put(DBHelper.ACCOUNT_PASSWORD, password);
                        values.put(DBHelper.ACCOUNT_MOBILE_NUMBER, mobile);
                        values.put(DBHelper.ACCOUNT_PASSWORD, password);
                        values.put(DBHelper.ACCOUNT_IS_EMPLOYER, userType);
                        values.put(DBHelper.ACCOUNT_DOB, dob);
                        values.put(DBHelper.ACCOUNT_ORGANISATION, org);
                        values.put(DBHelper.ACCOUNT_ORG_ADDRESS, address);
                        if(userType == Constants.TYPE_JOBSEEKER) {
                            values.put(DBHelper.ACCOUNT_CGPA, _cgpaText.getText().toString());
                            values.put(DBHelper.ACCOUNT_HSC, _hscText.getText().toString());
                            values.put(DBHelper.ACCOUNT_SSLC, _sslcText.getText().toString());
                        }


                        Uri uri = getContentResolver().insert(JobPortalProvider.ACCOUNT_URI, values);
                        long nId = ContentUris.parseId(uri);
                        if ( nId > 0) {
                            // On complete call either onSignupSuccess or onSignupFailed
                            // depending on success
                            onSignupSuccess(nId);
                        } else {
                            onSignupFailed();
                        }
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 1000);
    }


    public void onSignupSuccess(long nId) {
        _signupButton.setEnabled(true);
        //Intent i =new Intent();
        //i.putExtra("type", userType);
        //i.putExtra("userID", nId);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String address = _addressText.getText().toString();
        String email = _emailText.getText().toString();
        String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();
        String dob = _dobText.getText().toString();
        String company = _companyText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (address.isEmpty()) {
            _addressText.setError("Enter Valid Address");
            valid = false;
        } else {
            _addressText.setError(null);
        }


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (mobile.isEmpty() || mobile.length() != 10) {
            _mobileText.setError("Enter Valid Mobile Number");
            valid = false;
        } else {
            _mobileText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("Password Do not match");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        //dob
        if (dob.isEmpty()) {
            _dobText.setError("Enter Valid DOB(dd/mm/yyyy)");
            valid = false;
        } else {
            _dobText.setError(null);
        }
        //company
        if (company.isEmpty()) {
            _companyText.setError("Enter Valid Name");
            valid = false;
        } else {
            _companyText.setError(null);
        }

        if (userType == Constants.TYPE_JOBSEEKER) {
            String cgpa = _cgpaText.getText().toString();
            String hsc = _hscText.getText().toString();
            String sslc = _sslcText.getText().toString();

            //cgpa
            if (cgpa.isEmpty()) {
                _cgpaText.setError("Enter Academic CGPA");
                valid = false;
            } else {
                _cgpaText.setError(null);
            }

            //hsc
            if (hsc.isEmpty()) {
                _hscText.setError("Enter HSC %");
                valid = false;
            } else {
                _hscText.setError(null);
            }

            //sslc
            if (sslc.isEmpty()) {
                _sslcText.setError("Enter SSLC %");
                valid = false;
            } else {
                _sslcText.setError(null);
            }
        }
        return valid;
    }

    MyAlertDialog dialog;

    @Override
    public void onBackPressed() {
        dialog = new MyAlertDialog(this, "Exit App",
                "Are you sure you want to discard?", new MyAlertDialog.OnButtonClickListener() {
            @Override
            public void onMyAlertDlgButClick(int id, ContentValues values) {
                switch (id) {
                    case R.id.custdlg_button_yes:
                        SignupActivity.super.onBackPressed();
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

    }
}
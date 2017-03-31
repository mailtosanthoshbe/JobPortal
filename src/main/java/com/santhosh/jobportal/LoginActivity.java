package com.santhosh.jobportal;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.santhosh.jobportal.data.Account;
import com.santhosh.jobportal.data.Constants;
import com.santhosh.jobportal.data.Util;
import com.santhosh.jobportal.network.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @Bind(R.id.input_email)
    EditText _emailText;
    @Bind(R.id.input_password)
    EditText _passwordText;
    @Bind(R.id.btn_login)
    Button _loginButton;
    @Bind(R.id.link_signup)
    TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        //set permission(ie. android >= 6.0)
        checkAndRequestPermissions();

        _emailText.setText("a@gmail.com");
        _passwordText.setText("qwer");

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog.Builder alt_bld = new AlertDialog.Builder(LoginActivity.this);
                //alt_bld.setIcon(R.drawable.icon);
                alt_bld.setTitle("Select Option");
                final String[] user = {"Job Seeker", "Employer"};
                alt_bld.setSingleChoiceItems(user, -1, null);
                alt_bld.setPositiveButton("Sign Up", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                        switch (((AlertDialog) dialog).getListView().getCheckedItemPosition()) {
                            case 0:
                                intent.putExtra("type", Constants.TYPE_JOBSEEKER);
                                break;
                            case 1:
                                intent.putExtra("type", Constants.TYPE_EMPLOYER);
                                break;
                            default:
                                break;
                        }
                        startActivityForResult(intent, Constants.REQUEST_SIGNUP);
                        //finish();
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }
                });
                AlertDialog alert = alt_bld.create();
                alert.show();
            }
        });
    }

    public void login() {
        Log.d(Constants.TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        final String email = _emailText.getText().toString();
        final String password = _passwordText.getText().toString();

        new android.os.Handler().postDelayed(new Runnable() {
            public void run() {
                ApiInterface apiInf = new ApiInterface(getBaseContext());
                Account account = apiInf.authenticate(email, password);
                // On complete call either onLoginSuccess or onLoginFailed
                if (account != null && account.getUserId() > 0) {
                    onLoginSuccess(account);
                } else {
                    onLoginFailed();
                }
                progressDialog.dismiss();
            }
        }, 1000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Successfully Registered. sign in now!", Toast.LENGTH_SHORT).show();
            }
        }
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
                        LoginActivity.super.onBackPressed();
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

    public void onLoginSuccess(Account account) {
        _loginButton.setEnabled(true);
        //update prefs for signin info
        Util.updateSignOutScenario(this, false, account.getUserId());

        Intent intent = new Intent(this, JobsList.class);
        intent.putExtra("type", account.getEmployerType());
        startActivity(intent);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed. User not found", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    /**
     * check and request runtime permissions(>=Android 6.0)
     */
    private void checkAndRequestPermissions() {
        String[] PERMISSIONS = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CALL_PHONE
        };

        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : PERMISSIONS) {
            if (!Util.checkPermission(this, permission)) {
                listPermissionsNeeded.add(permission);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    Constants.REQUEST_ID_MULTIPLE_PERMISSIONS);
        } else {
            Log.d(Constants.TAG, "All Permissions granted!");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_ID_MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        switch (grantResults[i]) {
                            case PackageManager.PERMISSION_GRANTED:
                                Log.d(Constants.TAG, permissions[i] + ": PERMISSION GRANTED");
                                break;
                            default:
                                Log.d(Constants.TAG, permissions[i] + ": PERMISSION DENIED");
                                break;
                        }
                    }
                } else {
                    Log.d(Constants.TAG, "PERMISSION_DENIED: Go to settings and enable permissions!");
                }
            }
        }
    }
}

package com.shubham.smartparkingsystem;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import es.dmoral.toasty.Toasty;

public class loginActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    TextView signupActivity, forgotpassword;
    TextInputLayout et_email, et_password;
    Button loginbtn;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPreferences = getSharedPreferences("User", 0);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        loginbtn = findViewById(R.id.loginbtn);

        signupActivity = findViewById(R.id.signupActivity);
        forgotpassword = findViewById(R.id.forgotpassword);

        if (sharedPreferences.getBoolean("login_status_shared_preferences", false)) {
            Intent intent = new Intent(loginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        signupActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupActivity(v);
            }
        });
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = et_email.getEditText().getText().toString();
                String password = et_password.getEditText().getText().toString().trim();

                if (!validatePassword() | !validateEmail()) {
                    return;
                } else if (!isconnected(getApplicationContext())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(loginActivity.this);
                    builder.setTitle("Connection error")
                            .setMessage("Unable to connect with the server.Check your internet connection and try again")
                            .setCancelable(false)
                            .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(loginActivity.this, loginActivity.class));
                                    finish();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            });
                    builder.show();
                } else {
                    loginbtn.setText("Please wait...");
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(loginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            loginbtn.setText("LOGIN");
                            if (!task.isSuccessful()) {
                                if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                    et_email.setError("Email does not exist");
                                    et_email.requestFocus();
                                    YoYo.with(Techniques.Shake)
                                            .duration(200)
                                            .repeat(1)
                                            .playOn(et_email);
                                } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                    et_password.setError("Password does not match");
                                    et_password.requestFocus();
                                    YoYo.with(Techniques.Shake)
                                            .duration(200)
                                            .repeat(1)
                                            .playOn(et_password);
                                } else {
                                    Toasty.error(loginActivity.this, "Login Error", Toast.LENGTH_SHORT, true).show();
                                }
                            } else {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("login_status_shared_preferences", true);
                                editor.apply();

                                Toasty.success(loginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(loginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
            }
        });
        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = et_email.getEditText().getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
//                    Toast.makeText(getApplication(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
                    et_email.setError("Enter your Email ID");
                    et_email.requestFocus();
                } else {
                    final ProgressDialog pd = new ProgressDialog(loginActivity.this, R.style.MyAlertDialogStyle);
                    pd.setMessage("Sending Link...");
                    pd.show();
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @RequiresApi(api = Build.VERSION_CODES.P)
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                pd.dismiss();
                                Toasty.success(loginActivity.this, "Link sent to your registered email", Toast.LENGTH_SHORT, true).show();
                            } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                pd.dismiss();
                                Toasty.warning(loginActivity.this, "Please register", Toast.LENGTH_SHORT, true).show();
                            } else if (!isconnected(getApplicationContext())) {
                                pd.dismiss();
                                Toasty.error(loginActivity.this, "Check your Internet connection", Toast.LENGTH_SHORT, false).show();
                            } else {
                                pd.dismiss();
                                Toasty.error(loginActivity.this, "Some error occured Try again later", Toast.LENGTH_SHORT, false).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private boolean isconnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo Mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return wifi != null && wifi.isConnected() || Mobile != null && Mobile.isConnected();
    }

    private Boolean validateEmail() {
        String val = et_email.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (val.isEmpty()) {
            et_email.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(emailPattern)) {
            et_email.setError("Invalid email address");
            return false;
        } else {
            et_email.setError(null);
            et_email.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = et_password.getEditText().getText().toString();

        if (val.isEmpty()) {
            et_password.setError("Field cannot be empty");
            return false;
        } else {
            et_password.setError(null);
            et_password.setErrorEnabled(false);
            return true;
        }
    }

    public void signupActivity(View view) {
        Intent intent = new Intent(loginActivity.this , signupActivity.class);
        startActivity(intent);
        finish();
    }
}
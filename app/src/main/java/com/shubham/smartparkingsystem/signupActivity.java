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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;


public class signupActivity extends AppCompatActivity {

    TextInputLayout et_name, et_email, et_phone, et_password;
    Button signupbtn;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore fstore;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        et_name = findViewById(R.id.et_name);
        et_email = findViewById(R.id.et_email);
        et_phone = findViewById(R.id.et_phone);
        et_password = findViewById(R.id.et_password);
        signupbtn = findViewById(R.id.signupbtn);

        firebaseAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);


        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = et_name.getEditText().getText().toString().trim();
                final String email = et_email.getEditText().getText().toString().trim();
                final String phone = et_phone.getEditText().getText().toString().trim();
                final String password = et_password.getEditText().getText().toString().trim();
                if (!validateName() | !validatePassword() | !validatePhoneNo() | !validateEmail()) {
                    return;
                } else if (!isconnected(getApplicationContext())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(signupActivity.this);
                    builder.setTitle("Connection error")
                            .setMessage("Unable to connect with the server.Check your internet connection and try again")
                            .setCancelable(false)
                            .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(signupActivity.this, signupActivity.class));
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
                    final ProgressDialog pd = new ProgressDialog(signupActivity.this, R.style.MyAlertDialogStyle);
                    pd.setMessage("Registering...");
                    pd.setCancelable(false);
                    pd.show();
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(signupActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                pd.dismiss();
                                Toasty.error(signupActivity.this, "User with this email already exist.", Toast.LENGTH_SHORT, false).show();
                                et_email.setError("Email already exist");
                                YoYo.with(Techniques.Shake)
                                        .duration(200)
                                        .repeat(1)
                                        .playOn(et_email);
                            } else {
                                pd.dismiss();
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("login_status_shared_preferences", true);
                                editor.apply();
                                //Store User Data
                                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                                DocumentReference df = fstore.collection("users").document(firebaseUser.getUid());
                                Map<String, Object> userInfo = new HashMap<>();
                                userInfo.put("Name", name);
                                userInfo.put("UserEmail", email);
                                userInfo.put("UserPhone", phone);
                                userInfo.put("UserPassword", password);

                                df.set(userInfo);

                                Toasty.success(signupActivity.this, "Sign Up succesful", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(signupActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
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

    private Boolean validateName() {
        String val = et_name.getEditText().getText().toString();

        if (val.isEmpty()) {
            et_name.setError("Field cannot be empty");
            YoYo.with(Techniques.Shake)
                    .duration(200)
                    .repeat(1)
                    .playOn(et_name);
            return false;
        } else if (val.length() > 18) {
            et_name.setError("Name too long");
            YoYo.with(Techniques.Shake)
                    .duration(200)
                    .repeat(1)
                    .playOn(et_name);
            return false;
        } else if (val.matches(".*[0-9].*")) {
            et_name.setError("Name cannot contain a number");
            YoYo.with(Techniques.Shake)
                    .duration(200)
                    .repeat(1)
                    .playOn(et_name);
            return false;
        } else {
            et_name.setError(null);
            et_name.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateEmail() {
        String val = et_email.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (val.isEmpty()) {
            et_email.setError("Field cannot be empty");
            YoYo.with(Techniques.Shake)
                    .duration(200)
                    .repeat(1)
                    .playOn(et_email);
            return false;
        } else if (!val.matches(emailPattern)) {
            et_email.setError("Invalid email address");
            YoYo.with(Techniques.Shake)
                    .duration(200)
                    .repeat(1)
                    .playOn(et_email);
            return false;
        } else {
            et_email.setError(null);
            et_email.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePhoneNo() {
        String val = et_phone.getEditText().getText().toString();

        if (val.isEmpty()) {
            et_phone.setError("Field cannot be empty");
            YoYo.with(Techniques.Shake)
                    .duration(200)
                    .repeat(1)
                    .playOn(et_phone);
            return false;
        } else if (val.length() < 10 | val.length() > 10) {
            et_phone.setError("Please enter Valid phone number");
            YoYo.with(Techniques.Shake)
                    .duration(200)
                    .repeat(1)
                    .playOn(et_phone);
            return false;
        } else {
            et_phone.setError(null);
            et_phone.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = et_password.getEditText().getText().toString();

        if (val.isEmpty()) {
            et_password.setError("Field cannot be empty");
            YoYo.with(Techniques.Shake)
                    .duration(200)
                    .repeat(1)
                    .playOn(et_password);
            return false;
        } else if (val.length() < 6) {
            et_password.setError("Password too short...Must be greater than 6 digits");
            YoYo.with(Techniques.Shake)
                    .duration(200)
                    .repeat(1)
                    .playOn(et_password);
            return false;
        } else {
            et_password.setError(null);
            et_password.setErrorEnabled(false);
            return true;
        }
    }

    public void LoginActivity(View view) {
        startActivity(new Intent(this, loginActivity.class));
        finish();
    }
}
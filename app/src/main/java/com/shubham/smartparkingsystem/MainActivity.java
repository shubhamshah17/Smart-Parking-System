package com.shubham.smartparkingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.shubham.smartparkingsystem.utils.CheckInternetConnection;
import com.shubham.smartparkingsystem.utils.helpActivity;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore fstore;
    CardView location1,location2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        new CheckInternetConnection(this).checkConnection();
        sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE);

        location1 = findViewById(R.id.location1);
        location2 = findViewById(R.id.location2);

        location1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,parkingOne.class);
                startActivity(intent);
                sendData(1,0);
            }
        });
        location2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,parkingTwo.class);
                startActivity(intent);
                sendData(0,1);
//                Toasty.info(MainActivity.this,"Layout pending...",Toasty.LENGTH_LONG,true).show();

            }
        });
    }

    private void sendData(int i,int j) {
        Map<String, Object> update = new HashMap<>();
        update.put("0", i);
        update.put("1", j);
        DocumentReference df = fstore.collection("Parking Lots")
                .document("lotSelected");
        df.set(update, SetOptions.merge());

    }

    @Override
    protected void onResume() {
        super.onResume();
        sendData(0,0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        sendData(0,0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.view_help:
                startActivity(new Intent(MainActivity.this, helpActivity.class));
                break;
            case R.id.view_logout:
                userLogout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void userLogout() {
        if (!isConnected(MainActivity.this)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Connection error")
                    .setMessage("Unable to connect with the server.Check your internet connection and try again")
                    .setCancelable(false)
                    .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.show();
        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            alertDialog.setTitle("Confirm logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("YES",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseAuth.getInstance().signOut();
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("login_status_shared_preferences", false);
                                    editor.apply();
                                    final ProgressDialog pd = new ProgressDialog(MainActivity.this, R.style.MyAlertDialogStyle);
                                    pd.setMessage("Signing Out...");
                                    pd.setCancelable(false);
                                    pd.show();
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            pd.dismiss();
                                            Intent intent = new Intent(MainActivity.this, loginActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            Toasty.success(MainActivity.this, "Signed out", Toasty.LENGTH_SHORT).show();
                                        }
                                    }, 2000);
                                }
                            })
                    .setNegativeButton("NO",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).show();
        }
    }

    private boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo Mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return wifi != null && wifi.isConnected() || Mobile != null && Mobile.isConnected();
    }
}
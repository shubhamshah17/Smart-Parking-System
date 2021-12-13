package com.shubham.smartparkingsystem;

import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.shubham.smartparkingsystem.utils.CheckInternetConnection;

import es.dmoral.toasty.Toasty;

public class parkingOne extends AppCompatActivity {

    private ImageView slot1, slot2, slot3, slot4, slot5;
    private ImageView slot6, slot7, slot8, slot9, slot10;
    private Button refresh;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore fstore;

    Animation animation;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_one);
        changeStatusBarColor();
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primaryTextColor)));
        actionBar.setTitle("Parking");
        actionBar.setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_backbutton);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        firebaseAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        new CheckInternetConnection(this).checkConnection();
        pd = new ProgressDialog(parkingOne.this, R.style.MyAlertDialogStyle);
        pd.setMessage("Please wait...");
        pd.setCancelable(false);
        pd.show();

        slot1 = findViewById(R.id.slot1);
        slot2 = findViewById(R.id.slot2);
        slot3 = findViewById(R.id.slot3);
        slot4 = findViewById(R.id.slot4);
        slot5 = findViewById(R.id.slot5);
        slot6 = findViewById(R.id.slot6);
        slot7 = findViewById(R.id.slot7);
        slot8 = findViewById(R.id.slot8);
        slot9 = findViewById(R.id.slot9);
        slot10 = findViewById(R.id.slot10);
        refresh = findViewById(R.id.refresh);
        animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.bounce);
        getData();

        slot1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.info(parkingOne.this,"SLOT 1",Toasty.LENGTH_SHORT,false).show();
            }
        });
        slot2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.info(parkingOne.this,"SLOT 2",Toasty.LENGTH_SHORT,false).show();
            }
        });
        slot3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.info(parkingOne.this,"SLOT 3",Toasty.LENGTH_LONG,false).show();
            }
        });
        slot4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.info(parkingOne.this,"SLOT 4",Toasty.LENGTH_SHORT,false).show();
            }
        });
        slot5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.info(parkingOne.this,"SLOT 5",Toasty.LENGTH_SHORT,false).show();
            }
        });
        slot6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.info(parkingOne.this,"SLOT 6",Toasty.LENGTH_SHORT,false).show();
            }
        });
        slot7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.info(parkingOne.this,"SLOT 7",Toasty.LENGTH_SHORT,false).show();
            }
        });
        slot8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.info(parkingOne.this,"SLOT 8",Toasty.LENGTH_SHORT,false).show();
            }
        });
        slot9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.info(parkingOne.this,"SLOT 9",Toasty.LENGTH_SHORT,false).show();
            }
        });
        slot10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.info(parkingOne.this,"SLOT 10",Toasty.LENGTH_SHORT,false).show();
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });

    }

    private void getData() {
        Query query = fstore.collection("Parking Spots");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        for (int i = 1; i <= 10; i++) {
                            int j =Integer.parseInt((String)document.getData().get(String.valueOf(i)));
                            if (j == 1) {
                                switch (i) {
                                    case 1:
                                        slot1.setImageResource(R.drawable.ic_green_car);
                                        slot1.startAnimation(animation);
                                        break;
                                    case 2:
                                        slot2.setImageResource(R.drawable.ic_green_car);
                                        slot2.startAnimation(animation);
                                        break;
                                    case 3:
                                        slot3.setImageResource(R.drawable.ic_green_car);
                                        slot3.startAnimation(animation);
                                        break;
                                    case 4:
                                        slot4.setImageResource(R.drawable.ic_green_car);
                                        slot4.startAnimation(animation);
                                        break;
                                    case 5:
                                        slot5.setImageResource(R.drawable.ic_green_car);
                                        slot5.startAnimation(animation);
                                        break;
                                    case 6:
                                        slot6.setImageResource(R.drawable.ic_green_car);
                                        slot6.startAnimation(animation);
                                        break;
                                    case 7:
                                        slot7.setImageResource(R.drawable.ic_green_car);
                                        slot7.startAnimation(animation);
                                        break;
                                    case 8:
                                        slot8.setImageResource(R.drawable.ic_green_car);
                                        slot8.startAnimation(animation);
                                        break;
                                    case 9:
                                        slot9.setImageResource(R.drawable.ic_green_car);
                                        slot9.startAnimation(animation);
                                        break;
                                    case 10:
                                        slot10.setImageResource(R.drawable.ic_green_car);
                                        slot10.startAnimation(animation);
                                        break;
                                }
                            }
                        }
                        pd.dismiss();
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.register_bk_color));
        }
    }
}
package com.shubham.smartparkingsystem.utils;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;
import android.view.View;

import  com.shubham.smartparkingsystem.R;

import es.dmoral.toasty.Toasty;

public class CheckInternetConnection {

    Context context;

    public CheckInternetConnection() {
    }

    public CheckInternetConnection(Context context) {
        this.context = context;
    }

    public void checkConnection() {

        if (!isInternetConnected()) {
            showNoInternetDialog();
            Log.d("tag","No internet");
        }
    }


    public boolean isInternetConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isConnectedOrConnecting();

    }

    public void showNoInternetDialog() {
        final Dialog dialog = new Dialog(context, R.style.noInternet_dialog);
        dialog.setContentView(R.layout.dialog_no_internet);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.findViewById(R.id.connectBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInternetConnected()) {
                    dialog.dismiss();

                } else {
                    Toasty.warning(context,"Try again", Toasty.LENGTH_SHORT,false).show();
//                    Intent dialogIntent = new Intent(android.provider.Settings.ACTION_SETTINGS);
//                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(dialogIntent);
                }
            }
        });
        dialog.show();
    }

}

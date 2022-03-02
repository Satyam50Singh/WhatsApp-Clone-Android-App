package com.example.whatsappclone.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

public class Utils {
    // method to show toast message
    public static void showToastMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    // method to show and hide progress dialog
    public static ProgressDialog progressDialog = null;

    public static void showProgressDialog(Context context, String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(message);
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }

    public static void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.hide();
    }
}

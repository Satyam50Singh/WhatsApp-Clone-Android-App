package com.example.whatsappclone.utils;

import static android.provider.Settings.System.getString;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.widget.Toast;

import com.example.whatsappclone.R;
import com.google.android.material.textfield.TextInputEditText;

public class Utils {
    // method to show toast message
    public static void showToastMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    // method to show and hide progress dialog
    public static ProgressDialog progressDialog = null;

    public static void showProgressDialog(Context context, String message, String title) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            if(title.length() > 0 )
                progressDialog.setTitle(title);
            progressDialog.setMessage(message);
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }

    public static void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.hide();
    }

    // method for email & password validation
    public static boolean credentialsValidation(TextInputEditText etEmail, TextInputEditText etPassword) {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError(Resources.getSystem().getString(R.string.email_required));
            etEmail.requestFocus();
            return false;
        }
        if (!email.matches(Constants.EMAIL_PATTERN)) {
            etEmail.setError(Resources.getSystem().getString(R.string.invalid_email));
            etEmail.requestFocus();
            return false;
        }
        if (password.isEmpty()) {
            etPassword.setError(Resources.getSystem().getString(R.string.password_required));
            etPassword.requestFocus();
            return false;
        }
        if (password.length() < 6) {
            etPassword.setError(Resources.getSystem().getString(R.string.password_length_error));
            etPassword.requestFocus();
            return false;
        }
        return true;
    }

    public static boolean validateUsername(TextInputEditText etUsername) {
        String username = etUsername.getText().toString().trim();
        if (username.isEmpty()) {
            etUsername.setError(Resources.getSystem().getString(R.string.username_required));
            etUsername.requestFocus();
            return false;
        }
        return true;
    }
}

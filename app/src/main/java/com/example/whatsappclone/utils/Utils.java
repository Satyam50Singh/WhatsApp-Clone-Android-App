package com.example.whatsappclone.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.whatsappclone.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Utils {
    public static final int PICK_IMAGE_ACTIVITY_REQUEST_CODE = 1888;
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1889;

    // method to show toast message
    public static void showToastMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    // method to show and hide progress dialog
    public static ProgressDialog progressDialog = null;

    public static void showProgressDialog(Context context, String title, String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            if (title.length() > 0)
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
    public static boolean credentialsValidation(Context context, TextInputEditText etEmail, TextInputEditText etPassword) {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError(context.getString(R.string.email_required));
            etEmail.requestFocus();
            return false;
        }
        if (!email.matches(Constants.EMAIL_PATTERN)) {
            etEmail.setError(context.getString(R.string.invalid_email));
            etEmail.requestFocus();
            return false;
        }
        if (password.isEmpty()) {
            etPassword.setError(context.getString(R.string.password_required));
            etPassword.requestFocus();
            return false;
        }
        if (password.length() < 6) {
            etPassword.setError(context.getString(R.string.password_length_error));
            etPassword.requestFocus();
            return false;
        }
        return true;
    }

    public static boolean validateUsername(Context context, TextInputEditText etUsername) {
        String username = etUsername.getText().toString().trim();
        if (username.isEmpty()) {
            etUsername.setError(context.getString(R.string.username_required));
            etUsername.requestFocus();
            return false;
        }
        return true;
    }

    // method to show log
    public static void showLog(String tag, String message) {
        Log.d(tag, message);
    }

    // method to show snackBar
    public static void snackBar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);
        View snackBarView = snackbar.getView();
        TextView snackBarTextView = snackBarView.findViewById(R.id.snackbar_text);
        snackBarTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_wifi_off, 0);
        snackBarTextView.setGravity(Gravity.CENTER);
        snackbar.show();
    }

    // camera permission and picking images from camera and gallery
    public static boolean checkAndRequestPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            int cameraPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
            if (cameraPermission == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, 21);
                return false;
            }
        }
        return true;
    }

    public static void takePictureFromCamera(Activity activity) {
        Intent pickPhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pickPhoto.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(pickPhoto, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    public static void takePictureFromGallery(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(Constants.FILE_TYPE);
        activity.startActivityForResult(intent,
                PICK_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    public static Bitmap getBitmapFromUri(Uri uri, Context context) {
        Bitmap selectedBitmap = null;
        final InputStream imageStream;
        try {
            imageStream = context.getContentResolver().openInputStream(uri);
            selectedBitmap = BitmapFactory.decodeStream(imageStream);
        } catch (FileNotFoundException e) {
            e.fillInStackTrace();
        }
        return selectedBitmap;
    }

    // converting Bitmap to base64 string
    public static String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encImage;
    }

    // setting image/file quality
    public static int getFileQuality(Bitmap bm) {

        int fileSize = 0;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
            fileSize = bm.getRowBytes() * bm.getHeight();
        } else {
            fileSize = bm.getByteCount();
        }

        if (fileSize > 0 && fileSize < 1000)
            return 50;
        else if (fileSize > 1000 && fileSize < 2000)
            return 40;
        else if (fileSize > 2000 && fileSize < 4000)
            return 30;
        else if (fileSize > 4000 && fileSize < 10000)
            return 25;
        else
            return 10;
    }

    // converting base64 string to Bitmap
    public static Bitmap decodeImage(String img) {
        byte[] decodedString = Base64.decode(img, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    // method to remove image
    public static void removeProfilePicture(Activity context, String userId) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(Constants.DB_PATH);
        new AlertDialog.Builder(context)
                .setTitle(R.string.remove_profile_photo)
                .setPositiveButton(R.string.remove, (dialogInterface, i) -> {
                    firebaseDatabase.getReference()
                            .child(Constants.USER_COLLECTION_NAME)
                            .child(userId)
                            .child("profilePicture")
                            .setValue(null)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Utils.showToastMessage(context, context.getString(R.string.profile_picture_removed));
                                }
                            });

                })
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }
}

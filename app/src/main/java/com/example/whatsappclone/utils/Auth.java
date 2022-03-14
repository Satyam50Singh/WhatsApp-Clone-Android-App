package com.example.whatsappclone.utils;

import android.content.Context;
import android.content.Intent;

import com.example.whatsappclone.R;
import com.example.whatsappclone.ui.activities.LoginWithPhoneActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class Auth {

    public static GoogleSignInOptions getGoogleSignInOptions(Context context) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.request_token_id))
                .requestEmail()
                .build();
        return gso;
    }

    public static void navigateToLoginWithPhoneActivity(Context context) {
        context.startActivity(new Intent(context, LoginWithPhoneActivity.class));
    }

}

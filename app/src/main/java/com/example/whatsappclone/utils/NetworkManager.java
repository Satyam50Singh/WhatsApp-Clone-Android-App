package com.example.whatsappclone.utils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkManager {

    public static void checkNetworkConnectedStatus(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(content.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();
        if(!activeInfo.isConnected()){
            Utils.showToast(context, "No Internet Connection!");
        }else{
            Utils.showLog("Network Status : ", "Yes");
        }
    }
}
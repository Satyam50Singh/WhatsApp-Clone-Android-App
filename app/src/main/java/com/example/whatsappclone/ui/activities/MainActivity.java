package com.example.whatsappclone.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapter.ViewPagerAdapter;
import com.example.whatsappclone.ui.fragments.CallsFragment;
import com.example.whatsappclone.ui.fragments.CameraFragment;
import com.example.whatsappclone.ui.fragments.ChatFragment;
import com.example.whatsappclone.ui.fragments.NoNetworkFragment;
import com.example.whatsappclone.ui.fragments.StatusFragment;
import com.example.whatsappclone.utils.Auth;
import com.example.whatsappclone.utils.NetworkManager;
import com.example.whatsappclone.utils.Utils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private FirebaseAuth firebaseAuth;
    GoogleSignInClient googleSignInClient;
    private ChatFragment chatFragment;
    private boolean connStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // for removing shadow of action bar
        getSupportActionBar().setElevation(0);
        init();
        settingViewPagerAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (connStatus) {
            getMenuInflater().inflate(R.menu.main_menu, menu);
            MenuItem item = menu.findItem(R.id.action_search);
            SearchView searchView = (SearchView) item.getActionView();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    chatFragment.searchUser(s);
                            return false;
                }
            });
        }
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;
            case R.id.group_chat:
                startActivity(new Intent(MainActivity.this, GroupChatActivity.class));
                return true;
            case R.id.new_broadcast:
                Toast.makeText(getApplicationContext(), "New BroadCast", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.linked_devices:
                Toast.makeText(getApplicationContext(), "Linked Devices", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.payments:
                Toast.makeText(getApplicationContext(), "Payments", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.starred_messages:
                startActivity(new Intent(MainActivity.this, StarredMessageActivity.class));

                return true;
            case R.id.settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            case R.id.logout:
                userLogOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void init() {
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        tabLayout.setupWithViewPager(viewPager);
        firebaseAuth = FirebaseAuth.getInstance();
        // Configure Google Sign In
        googleSignInClient = GoogleSignIn.getClient(this, Auth.getGoogleSignInOptions(this));
    }

    // this method is used to set tabs in tab layout
    private void settingViewPagerAdapter() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        connStatus = NetworkManager.checkNetworkConnectedStatus(MainActivity.this);
        if (connStatus) {
            chatFragment = new ChatFragment();
            viewPagerAdapter.addFragment(new CameraFragment(), "");
            viewPagerAdapter.addFragment(chatFragment, getString(R.string.chats_tab));
            viewPagerAdapter.addFragment(new StatusFragment(), getString(R.string.status_tab));
            viewPagerAdapter.addFragment(new CallsFragment(), getString(R.string.calls_tab));
            viewPager.setAdapter(viewPagerAdapter);
            tabLayout.getTabAt(0).setIcon(R.drawable.ic_photo_camera);
            tabLayout.getTabAt(1).select();
        } else {
            viewPagerAdapter.addFragment(new NoNetworkFragment(), "");
            viewPager.setAdapter(viewPagerAdapter);
            tabLayout.getTabAt(0).setIcon(R.drawable.ic_wifi_off);
            tabLayout.getTabAt(0).select();
        }
    }

    // method for user log out
    private void userLogOut() {
        try {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.user_log_out)
                    .setMessage(R.string.user_log_out_message)
                    .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                        firebaseAuth.signOut();
                        googleSignInClient.signOut()
                                .addOnCompleteListener(this, task -> {
                                    if (task.isSuccessful()) {
                                        Utils.showLog(getString(R.string.success), getString(R.string.logout_successfully));
                                    }
                                });
                        startActivity(new Intent(MainActivity.this, SignInActivity.class));
                        finish();
                        Utils.showToastMessage(MainActivity.this, getString(R.string.user_log_out));
                    })
                    .setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss())
                    .show();
        } catch (Exception e) {
            Utils.showLog(getString(R.string.error), getString(R.string.google_sign_out_failed) + e.getMessage());
        }
    }
}

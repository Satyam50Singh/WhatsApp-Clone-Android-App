package com.example.whatsappclone.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapter.ViewPagerAdapter;
import com.example.whatsappclone.ui.fragments.CallsFragment;
import com.example.whatsappclone.ui.fragments.CameraFragment;
import com.example.whatsappclone.ui.fragments.ChatFragment;
import com.example.whatsappclone.ui.fragments.StatusFragment;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;

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
                Toast.makeText(getApplicationContext(), "Search : " + s, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;
            case R.id.new_group:
                Toast.makeText(getApplicationContext(), "New Group", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(), "Starred Messages", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.settings:
                Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void init() {
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        tabLayout.setupWithViewPager(viewPager);
    }

    // this method is used to set tabs in tab layout
    private void settingViewPagerAdapter() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        viewPagerAdapter.addFragment(new CameraFragment(), "");
        viewPagerAdapter.addFragment(new ChatFragment(), getString(R.string.chats_tab));
        viewPagerAdapter.addFragment(new StatusFragment(), getString(R.string.status_tab));
        viewPagerAdapter.addFragment(new CallsFragment(), getString(R.string.calls_tab));

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_photo_camera);
        tabLayout.getTabAt(1).select();
    }
}

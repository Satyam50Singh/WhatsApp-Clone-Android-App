package com.example.whatsappclone.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapter.CallAdapter;
import com.example.whatsappclone.models.UserModel;
import com.example.whatsappclone.utils.Constants;
import com.example.whatsappclone.utils.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CallsFragment extends Fragment {

    private RecyclerView rcvUserCallList;
    private CallAdapter callAdapter;
    private ArrayList<UserModel> userWithPhoneList = new ArrayList<>();
    private TextView tvNoUserFound;
    private ShimmerFrameLayout shimmerFrameLayout;
    private FirebaseDatabase database;

    private static final String TAG = "CallsFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_calls, container, false);
        initUI(rootView);
        return rootView;
    }

    private void initUI(View rootView) {
        database = FirebaseDatabase.getInstance(Constants.DB_PATH);

        rcvUserCallList = rootView.findViewById(R.id.rcv_user_call_list);
        tvNoUserFound = rootView.findViewById(R.id.tv_no_user_record);
        shimmerFrameLayout = rootView.findViewById(R.id.shimmer_user_calls_container);

        rcvUserCallList.setVisibility(View.GONE);
        shimmerFrameLayout.startShimmer();
        loadUsers();

        callAdapter = new CallAdapter(getActivity(), getContext(), userWithPhoneList);
        rcvUserCallList.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvUserCallList.setAdapter(callAdapter);
    }

    private void loadUsers() {
        try {
            database.getReference()
                    .child(Constants.USER_COLLECTION_NAME)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            userWithPhoneList.clear();
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                UserModel user = snapshot1.getValue(UserModel.class);
                                if (!user.getUserId().equals(FirebaseAuth.getInstance().getUid()) && user.getPhone() != null) {
                                    userWithPhoneList.add(user);
                                }
                            }
                            if (userWithPhoneList.size() <= 0) {
                                tvNoUserFound.setVisibility(View.VISIBLE);
                                rcvUserCallList.setVisibility(View.GONE);
                            } else {
                                rcvUserCallList.setVisibility(View.VISIBLE);
                                tvNoUserFound.setVisibility(View.GONE);
                            }
                            callAdapter.notifyDataSetChanged();
                            shimmerFrameLayout.hideShimmer();
                            shimmerFrameLayout.setVisibility(View.GONE);
                            rcvUserCallList.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            shimmerFrameLayout.hideShimmer();
                            shimmerFrameLayout.setVisibility(View.GONE);
                            rcvUserCallList.setVisibility(View.GONE);
                            Log.e(TAG, "onCancelled: "  + error.getMessage() );
                        }
                    });
        } catch (Exception e) {
            Utils.showLog(getString(R.string.error), e.getMessage());
        }
    }
}
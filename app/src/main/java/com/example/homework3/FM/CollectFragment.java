package com.example.homework3.FM;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.homework3.R;
import com.example.homework3.adapter.CardAdapter;
import com.example.homework3.object.CardData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CollectFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference mDbCollectPath;
    ProgressBar CollectProgressbar;
    LinearLayoutCompat layout;
    RecyclerView recyclerView;
    CardAdapter cardAdapter;
    List<CardData> GroupCardData = new ArrayList<CardData>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_collect, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        CollectProgressbar = getActivity().findViewById(R.id.progressBarCollect);
        layout = getActivity().findViewById(R.id.fragment_collectXML);
        recyclerView = getActivity().findViewById(R.id.rvCollect);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDbCollectPath = FirebaseDatabase.getInstance().getReference().child(user.getUid()).child("Collect");


        //設定SharedPreferences
        setColor();

        //顯示畫面
        if (SearchFragment.isNetworkAvailable(getActivity())) {
            reload(mDbCollectPath);
        } else {
            Toast.makeText(getActivity(), "請連接網路", Toast.LENGTH_SHORT).show();
            CollectProgressbar.setVisibility(View.GONE);
        }

    }

    //讀取資料庫並放入GroupCarData中
    private void reload(DatabaseReference db) {
        GroupCardData.clear();
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    CardData card = ds.getValue(CardData.class);
                    card.setCollectFragment(true); // Adapter會判斷是在收藏畫面創立的
                    GroupCardData.add(card);
                }
                cardAdapter = new CardAdapter(GroupCardData, getActivity());
                recyclerView.setAdapter(cardAdapter);
                CollectProgressbar.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //設定顏色
    private void setColor() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (sharedPreferences.getBoolean("checkToGray", false))
            layout.setBackgroundColor(Color.parseColor("gray"));
        else
            layout.setBackgroundColor(Color.parseColor("white"));
    }
}
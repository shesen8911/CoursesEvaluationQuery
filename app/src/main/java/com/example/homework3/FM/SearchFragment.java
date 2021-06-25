package com.example.homework3.FM;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.homework3.adapter.FragmentAdapter;
import com.example.homework3.R;
import com.google.android.material.tabs.TabLayout;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class SearchFragment extends Fragment {

    Button SearchBtn;
    EditText SearchEdt;
    LinearLayoutCompat layout;
    String search;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    FragmentAdapter fragmentAdapter;


    // 有沒有什麼快速使用recyclerView的方法啊= =
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        SearchEdt = getView().findViewById(R.id.edtSearch);
        SearchEdt.setText(search);
        System.out.println("SF_onviewStateRestored");
    }

    @Override
    public void onStart() {

        System.out.println("SF_Start");

        super.onStart();
        layout = getActivity().findViewById(R.id.fragment_searchXML);
        SearchBtn = getActivity().findViewById(R.id.btnSearch);
        tabLayout = getActivity().findViewById(R.id.tab_layout);
        viewPager2 = getActivity().findViewById(R.id.view_pager2);
        viewPager2.setOffscreenPageLimit(3); // ！！！加這行才能讓3個畫面重啟後也都能開

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        //點擊換頁
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //滑動跟上
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        //sharedPreferences~~~不同頁都要設定= =
        setColor();

        if (!SearchEdt.getText().toString().equals("")) {
            fragmentAdapter = new FragmentAdapter(fragmentManager, getLifecycle(), SearchEdt.getText().toString());
            viewPager2.setAdapter(fragmentAdapter);
        }
        //點擊查詢讓三個Fragment去跑
        SearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SearchEdt.getText().toString().equals("")) {
                    fragmentAdapter = new FragmentAdapter(fragmentManager, getLifecycle(), SearchEdt.getText().toString());
                    viewPager2.setAdapter(fragmentAdapter);
                }
            }
        });
    }

    //判斷是否有網路
    public static boolean isNetworkAvailable(Activity activity) {
        Context context = activity.getApplicationContext();
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null)
            return false;
        else {
            NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
            if (networkInfos != null && networkInfos.length > 0)
                for (int i = 0; i < networkInfos.length; i++)
                    if (networkInfos[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
        }
        return false;
    }

    //分析網頁
    public static Document analyzeHTML(String url) throws IOException {
        Connection connection = Jsoup.connect(url); //此處還可設定cookie, timeout
        //value從 http://www.useragentstring.com/ 複製的
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.106 Safari/537.36");
        final Document document = connection.get();
        return document;
    }

    //設定顏色
    private void setColor() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (sharedPreferences.getBoolean("checkToGray", false)) {
            layout.setBackgroundColor(Color.parseColor("gray"));
            SearchEdt.setHintTextColor(Color.parseColor("#505050"));
            SearchEdt.setTextColor(Color.parseColor("white"));
            tabLayout.setBackgroundColor(Color.parseColor("gray"));
            tabLayout.setTabTextColors(Color.parseColor("white"), Color.parseColor("#6200ED"));
        } else {
            layout.setBackgroundColor(Color.parseColor("white"));
            SearchEdt.setHintTextColor(Color.parseColor("gray"));
            SearchEdt.setTextColor(Color.parseColor("black"));
            tabLayout.setBackgroundColor(Color.parseColor("white"));
            tabLayout.setTabTextColors(Color.parseColor("black"), Color.parseColor("#6200ED"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    //處理BUG
    @Override
    public void onStop() {
        super.onStop();
        search = SearchEdt.getText().toString();
        SearchEdt.setText("");
    }
}
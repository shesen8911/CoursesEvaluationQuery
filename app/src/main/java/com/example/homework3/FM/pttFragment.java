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
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.homework3.R;
import com.example.homework3.adapter.CardAdapter;
import com.example.homework3.object.CardData;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class pttFragment extends Fragment {

    String search;
    public pttFragment(String search) {
        this.search = search;
    }

    ProgressBar progressBar;
    RecyclerView recyclerView;
    CardAdapter cardAdapter;
    private List<CardData> GroupCardData = new ArrayList<CardData>();
    private CardData cardData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ptt, container, false);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        recyclerView = getView().findViewById(R.id.rvSearch);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        progressBar = getView().findViewById(R.id.progressBarSearch);

        System.out.println("ptt_onViewStateRestored");
    }

    @Override
    public void onStart() {
        super.onStart();

        System.out.println("ptt_onStart");

        //判斷是否原本偵測過，只是按了詳細
        if(GroupCardData.size() > 0) {
            cardAdapter.notifyItemRangeRemoved(0, GroupCardData.size());
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            cardAdapter = new CardAdapter(GroupCardData, getContext());
            recyclerView.setAdapter(cardAdapter);
        }

        if (SearchFragment.isNetworkAvailable(getActivity())) {
            Runnable runnable = () -> {
                try {
                    //分析網頁
                    String url = "https://www.ptt.cc/bbs/NTUST_STUDY/search?q="+search;
                    final Document document = SearchFragment.analyzeHTML(url);

                    //網頁的主Class
                    Elements elements = document.getElementsByClass("r-ent");
                    GroupCardData = findThree(elements);

                    //抓每行的網址
//                                elements = document.select("div.title > a");
//                                GroupCardData = findContent(elements);

                    getActivity().runOnUiThread(() -> {
                        cardAdapter = new CardAdapter(GroupCardData, getActivity());
                        recyclerView.setAdapter(cardAdapter);
                        progressBar.setVisibility(View.GONE);
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                }
            };
            new Thread(runnable).start();
        } else {
            Toast.makeText(getActivity(), "請連接網路", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    //根據主class抓框框內的內容
    protected List<CardData> findThree(Elements ThemeContext)
    {
        List<CardData> GroupCardData = new ArrayList<CardData>();
        for (Element domElement: ThemeContext) {
            //標題
            String Title = domElement.getElementsByClass("title").text();
            //發文者
            String AuthorName = "作者: " + domElement.getElementsByClass("author").text();
            //日期
            String Date = "日期: " + domElement.getElementsByClass("date").text();
            //網址
            String href = "https://www.ptt.cc" + domElement.select("div.title > a").attr("href");


            cardData = new CardData(Title, AuthorName, Date, href, false, "ptt");
            GroupCardData.add(cardData);
        }
        return  GroupCardData;
    }

    //重新抓RV
    private void resetAdapter() {
        if(GroupCardData.size() > 0) {
            cardAdapter.notifyItemRangeRemoved(0, GroupCardData.size());
            cardAdapter = new CardAdapter(GroupCardData, getActivity());
            recyclerView.setAdapter(cardAdapter);
        }
    }
}
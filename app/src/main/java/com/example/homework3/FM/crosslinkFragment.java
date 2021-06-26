package com.example.homework3.FM;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class crosslinkFragment extends Fragment {

    String search;
    public crosslinkFragment(String search) {
        this.search = search;
    }

    ProgressBar progressBar;
    RecyclerView recyclerView;
    CardAdapter cardAdapter;
    private List<CardData> GroupCardData = new ArrayList<CardData>();
    private CardData cardData;

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        recyclerView = getView().findViewById(R.id.rvSearch2);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        progressBar = getView().findViewById(R.id.progressBarSearch2);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (SearchFragment.isNetworkAvailable(getActivity())) {
            Runnable runnable = () -> {
                try {
                    //分析網頁
                    String url = "https://www.crosslink.tw/questions?&q=" + search + "&commit=Search";
                    final Document document = SearchFragment.analyzeHTML(url);

                    //網頁的主Class
                    Elements elements = document.select("div.question-list > div.row");
                    GroupCardData = findThree(elements);

                    getActivity().runOnUiThread(() -> {
                        cardAdapter = new CardAdapter(GroupCardData, getActivity());
                        recyclerView.setAdapter(cardAdapter);
                        progressBar.setVisibility(View.GONE);
                        onStop();
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                }
            };
            new Thread(runnable).start();
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    //根據主class抓框框內的內容
    protected List<CardData> findThree(Elements ThemeContext)
    {
        List<CardData> GroupCardData = new ArrayList<CardData>();
        for (Element domElement: ThemeContext) {

            //標題
            String Title = domElement.getElementsByClass("col-md-4 col-sm-4 col-xs-12 text-center").get(0).text();
            //觸及數
            String ClickNum = domElement.getElementsByClass("col-md-1 col-sm-1 col-xs-6 text-center").get(0).text();
            //解答數
            String AnsNum = domElement.getElementsByClass("col-md-1 col-sm-1 col-xs-6 text-center").get(1).text();
            //網址 class長太奇怪要用[class=~~]，不能用點
            String href = domElement.select("div[class=col-md-2 col-sm-2 col-xs-12] > a").attr("href");

            cardData = new CardData(Title, ClickNum, AnsNum, href, false, "crosslink");
            GroupCardData.add(cardData);
        }
        return  GroupCardData;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_crosslink, container, false);
    }
}
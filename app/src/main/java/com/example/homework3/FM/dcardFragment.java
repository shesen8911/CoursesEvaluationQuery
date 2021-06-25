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

public class dcardFragment extends Fragment {

    String search;
    public dcardFragment(String search) {
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
        recyclerView = getView().findViewById(R.id.rvSearch3);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        progressBar = getView().findViewById(R.id.progressBarSearch3);
    }

    @Override
    public void onStart() {
        super.onStart();

        //爬蟲 && RV
        if (SearchFragment.isNetworkAvailable(getActivity())) {
            Runnable runnable = () -> {
                try {
                    //分析網頁
                    String url = "https://www.dcard.tw/search?query="+search+"&forum=ntust";
                    final Document document = SearchFragment.analyzeHTML(url);

//                    Elements elements = document.getElementsByClass("tgn9uw-0 bReysV");
//                    String s = elements.get(1).text();
//                    System.out.println(s);
                    //網頁的主Class
                    Elements elements = document.getElementsByClass("tgn9uw-0 bReysV");
                    GroupCardData = findThree(elements);

                    getActivity().runOnUiThread(() -> {
                        cardAdapter = new CardAdapter(GroupCardData, getActivity());
                        recyclerView.setAdapter(cardAdapter);
                        progressBar.setVisibility(View.GONE);
                        onStop();
                    });
                } catch (IOException e) {
                    e.printStackTrace();
//                    progressBar.setVisibility(View.GONE);
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
            String Title = domElement.getElementsByClass("tgn9uw-3 cUGTXH").text();
            //愛心
            String Heart = "愛心: " + domElement.getElementsByClass("cgoejl-3 jMiYgp").text();
            //解答數
            String Response = "回應: " + domElement.getElementsByClass("uj732l-2 ghvDya").text();
            //網址
            String href = "https://www.dcard.tw" + domElement.select("a[class=tgn9uw-3 cUGTXH]").attr("href");

            cardData = new CardData(Title, Heart, Response, href, false, "dcard");
            GroupCardData.add(cardData);
        }
        return  GroupCardData;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dcard, container, false);
    }
}
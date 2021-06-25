package com.example.homework3.FM;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.homework3.R;
import com.example.homework3.object.CardData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentFragment extends Fragment {

    FloatingActionButton CollectionBtn;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference mDbCollectPath;
    final List<Map<String, Object>> allItems = new ArrayList<Map<String, Object>>();
    CardData cardData;
    String content;
    TextView contentTV;
    ConstraintLayout layout;
    Button CrosslinkBtn;
    String author; //dcard用的 我有點懶

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        progressBar = getActivity().findViewById(R.id.progressBarContent);
        CrosslinkBtn = getActivity().findViewById(R.id.btnCrosslink);
        layout = getActivity().findViewById(R.id.fragment_contentXML);
        CollectionBtn = getActivity().findViewById(R.id.btnCollection);
        contentTV = getActivity().findViewById(R.id.tvContent);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDbCollectPath = FirebaseDatabase.getInstance().getReference().child(user.getUid()).child("Collect");
        ContentFragmentArgs args = ContentFragmentArgs.fromBundle(getArguments());

        //設定顏色
        setColor();

        //下面是畫面顯示
        reload(mDbCollectPath, args);
        if (SearchFragment.isNetworkAvailable(getActivity())) {
            Runnable runnable = () -> {
                try {
                    //content要再另外爬蟲 所以要多執行緒
                    if (args.getSite().equals("ptt")) {
                        content = ptt_findContent(args.getHref());
                        content = args.getLeft() + "\n標題\n" + args.getTitle() + "\n時間\n" + content;
                    }
                    if (args.getSite().equals("dcard")) {
                        content = dcard_findContent(args.getHref());
                        content = "作者\n" + author + "\n標題\n" + args.getTitle() + "\n時間\n" + content;
                    }
                    //TODO: 開啟瀏覽器改成爬蟲(需要了解OAUTH)
                    if (args.getSite().equals("crosslink")) {
                        CrosslinkBtn.setVisibility(View.VISIBLE);
                        CrosslinkBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                openWebPage(args.getHref());
                            }
                        });
//                        content = crosslink_findContent(args.getHref());
                        content = args.getTitle() + "\n\nCrosslink讀取資料待開發中~因需要FB OAUTH授權登入，若要瀏覽請按下方按鈕，將導至Crosslink。";
                    }
                    getActivity().runOnUiThread(() -> {
                        contentTV.setText(content);
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

        //下面是收藏功能
        CollectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean canceled = false;
                for (Map<String, Object> allItem : allItems) {
                    String key = allItem.get("key").toString();
                    boolean collected = (boolean) allItem.get("star");
                    String ahref = allItem.get("href").toString();
                    //確認在資料庫中  取消收藏 && 星星off
                    if (ahref.equals(args.getHref()) && collected) {
                        mDbCollectPath.child(key).removeValue();
                        reload(mDbCollectPath, args);
                        CollectionBtn.setImageResource(R.drawable.staroff);
                        canceled = true;
                        break;
                    }
                }
                //不在資料庫中  增加收藏 && 星星on
                if (!canceled) {
                    cardData = new CardData(args.getTitle(), args.getLeft(), args.getRight(), args.getHref(), true, args.getSite());
                    mDbCollectPath.push().setValue(cardData); //更新資料庫
                    reload(mDbCollectPath, args); //呼叫監聽器
                    CollectionBtn.setImageResource(R.drawable.staron); //星星變黑色
                }
            }
        });
    }

    //可以改用Transaction 使用者在多個地方登入就不會出錯 或是禁止多方登入?

    //firebase監聽器 語法問題研究兩天= =...(ListenerForSingle只會讀一次, 另一個一直讀的就算關閉這個Fragment還是會繼續讀)
    private void reload(DatabaseReference db, ContentFragmentArgs args) {
        //要根據key刪除，所以創MAP
        allItems.clear();
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {

                    CardData card = ds.getValue(CardData.class); //！！！！在CardData物件類別要做set不然無法傳值
                    Map<String, Object> item = new HashMap<String, Object>();
                    item.put("key", ds.getKey()); //資料庫的新增、刪除需要
                    item.put("href", card.getHref()); //所有card只有網址是不會重複的
                    item.put("star", card.isStar()); //所有card跑完拿來判斷有沒有收藏
                    allItems.add(item);

                    if (card.getHref().equals(args.getHref())) {
                        CollectionBtn.setImageResource(R.drawable.staron); //星星變黑色 TODO:可不可以變黃色啊
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //ptt: 網址截內容&推文
    protected String ptt_findContent(String link) throws IOException {

        final Document document = SearchFragment.analyzeHTML(link);

        String content = document.select("div#main-content").text();
        Elements pushes = document.getElementsByClass("push");
        String push = ptt_findPush(pushes);

        //切割要的內容
        int firstIndex = content.indexOf("時間")+2; //抓第一個出現時間的地方...如果作者或標題包含時間這兩個字會有誤 我懶得用
        int lastIndex = content.lastIndexOf("--");
        String str = content.substring(firstIndex, lastIndex);
        String T_str = str.substring(0, 24); // ptt上顯示的時間長度為23-24
        String C_str = str.substring(24).trim(); //時間之後的都是內容

        String allContent = T_str + "\n\n" + C_str + push;
        return  allContent;
    }

    //ptt: 下面推文
    protected String ptt_findPush(Elements pushes){
        String push = "";
        for (Element domElement: pushes){
            //箭頭推踩 $= : value ending with "hl push-tag"
            String tag = domElement.select("span[class$=hl push-tag]").text();
            //推文者
            String PusherName = domElement.getElementsByClass("f3 hl push-userid").text();
            //推文內容
            String PushContent = domElement.getElementsByClass("f3 push-content").text();
            //推文時間
            String Date = domElement.getElementsByClass("push-ipdatetime").text();
            push = push + "\n\n" + tag + " " + PusherName + " " + PushContent + "\n" + Date;
        }
        return  push;
    }

    //dcard: 網址截問題&回應
    protected String dcard_findContent(String link) throws IOException {

        final Document document = SearchFragment.analyzeHTML(link);

        //作者
        author = document.getElementsByClass("s3d701-2 kBmYXB").text();
        //時間
        String time = document.getElementsByClass("sc-1eorkjw-4 boQZzA").get(1).text();
        //問題內容
        String Qcontent = document.getElementsByClass("sc-1npvbtq-0 gfjrnD").text();
        //回應
        Elements Responses = document.getElementsByClass("pj3ky0-0 cpOUHp");
        String Respnose = dcard_findResponse(Responses);

        String allContent = time + "\n\n\n" + Qcontent + Respnose;
        return  allContent;
    }

    //dcard: 下面回應
    private String dcard_findResponse(Elements responses) {
        String response = "";
        for (Element domElement: responses){
            //回應者
            String Responser = domElement.getElementsByClass("sc-7fxob4-4 dbFiwE").text();
            //地下室&時間
            String BaseAndTime = domElement.getElementsByClass("sc-7fxob4-6 neiWc").text();
            //回應者愛心數
            String ResponserHeart = domElement.getElementsByClass("jt7qse-1 lhEwzj").text();
            //回應內容
            String ResponseContent = domElement.getElementsByClass("pj3ky0-3 jbAASD").text();
            response = response + "\n\n\n" + Responser + "\n" + BaseAndTime + "  愛心: " + ResponserHeart + "\n" + ResponseContent;
        }
        return  response;
    }

    //corsslink: 網址截問題&回答 TODO: 需要FB OAUTH授權登入
    protected String crosslink_findContent(String link) throws IOException {

//        System.out.println(link);
        final Document document = SearchFragment.analyzeHTML(link);
        String Q = document.getAllElements().text();
//        System.out.println(Q);
//        Elements elements = document.select("div.message");
//        String Q = document.select("div.message").text();
//        String Q = elements.text();
//        System.out.println(Q);
        Elements Answers = document.getElementsByClass("message left");
        String A = crosslink_findAns(Answers);

        return  A;
    }

    //corsslink: 下面回答
    private String crosslink_findAns(Elements Answers) {
        String A = "";
        for (Element domElement: Answers){
            //推文內容
            String AnsContent = domElement.text();
            A = A + "\n\n" + AnsContent;
        }
        return  A;
    }

    //設定顏色
    private void setColor() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (sharedPreferences.getBoolean("checkToGray", false)) {
            layout.setBackgroundColor(Color.parseColor("gray"));
            contentTV.setTextColor(Color.parseColor("white"));
        } else {
            layout.setBackgroundColor(Color.parseColor("white"));
            contentTV.setTextColor(Color.parseColor("black"));
        }
    }

    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        startActivity(intent);
//        getFragmentManager().beginTransaction().remove(ContentFragment.this).commit();
    }
}
package com.example.homework3.object;

public class CardData {
    private String Title;
    private String Left;
    private String Right;
    private String Href;
    private boolean Star;
    private String Site;
    private boolean IsCollectFragment; //判斷是不是在收藏畫面建立的 我想要搜尋和收藏共用點擊事件

    public CardData() {

    }
    public CardData(String title, String left, String right, String href, boolean star, String site) {
        Title = title;
        Left = left;
        Right = right;
        Href = href;
        Star = star;
        Site = site;
        IsCollectFragment = false;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getLeft() {
        return Left;
    }

    public void setLeft(String left) {
        Left = left;
    }

    public String getRight() {
        return Right;
    }

    public void setRight(String right) {
        Right = right;
    }

    public String getHref() {
        return Href;
    }

    public void setHref(String href) {
        Href = href;
    }

    public boolean isStar() {
        return Star;
    }

    public void setStar(boolean star) {
        Star = star;
    }

    public String getSite() {
        return Site;
    }

    public void setSite(String site) {
        Site = site;
    }

    public boolean isCollectFragment() {
        return IsCollectFragment;
    }

    public void setCollectFragment(boolean collectFragment) {
        IsCollectFragment = collectFragment;
    }
}
package com.example.homework3;

import com.example.homework3.object.CardData;

import org.junit.Test;

import static org.junit.Assert.*;

public class CardDataUnitTest {
    CardData cardData = new CardData("標題",
            "左標題", "右標題", "網址",
            false, "網站");
    @Test
    public void setup() {
        cardData = new CardData("標題",
                "左標題", "右標題", "網址",
                false, "網站");
    }
    @Test
    public void testContent() {
        assertEquals("標題", cardData.getTitle());
        assertEquals("左標題", cardData.getLeft());
        assertEquals("右標題", cardData.getRight());
        assertEquals("網址", cardData.getHref());
        assertEquals(false, cardData.isStar());
        assertEquals("網站", cardData.getSite());
        assertEquals(false, cardData.isCollectFragment());
    }
    @Test
    public void testSettings() {
        cardData.setTitle("標題2");
        cardData.setLeft("左標題2");
        cardData.setRight("右標題2");
        cardData.setHref("網址2");
        cardData.setStar(true);
        cardData.setSite("網站2");
        cardData.setCollectFragment(true);
        assertEquals("標題2", cardData.getTitle());
        assertEquals("左標題2", cardData.getLeft());
        assertEquals("右標題2", cardData.getRight());
        assertEquals("網址2", cardData.getHref());
        assertEquals(true, cardData.isStar());
        assertEquals("網站2", cardData.getSite());
        assertEquals(true, cardData.isCollectFragment());
    }
}
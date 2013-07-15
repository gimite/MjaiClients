package org.ymatsux.mjai.client;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class XScorerTest {

    private final boolean DEBUG = false;

    private List<Hai> readHaiList(String string) {
        String[] haiStrings = string.split(",");
        List<Hai> haiList = new ArrayList<Hai>();
        for (String haiString : haiStrings) {
            haiList.add(Hai.parse(haiString));
        }
        return haiList;
    }

    private int calculateShantensu(String tehaisString) {
        List<Hai> tehais = readHaiList(tehaisString);
        return ShantensuUtil.calculateShantensu(tehais);
    }

    private double calculateXScore(String tehaisString, int shantensu) {
        List<Hai> tehais = readHaiList(tehaisString);
        XScorer yScorer = new XScorer(
                tehais,
                false, readHaiList("5m"), Hai.parse("E"), Hai.parse("S"));
        double yScore = yScorer.calculateXScore(shantensu);
        if (DEBUG) {
            System.out.println(tehaisString);
            System.out.println(yScore);
            System.out.println();
        }
        return yScore;
    }

    public void assertGraterThan(double x, double y) {
        if (x <= y) {
            throw new AssertionError("Expected x > y however x = " + x + " y = " + y);
        }
    }

    @Test
    public void test() {
        assertEquals(4, calculateShantensu("1m,2m,3m,4m,5p,6p,7s,8s,E,S,P,F,3s"));
        assertEquals(4, calculateShantensu("1m,2m,3m,4m,5p,6p,7s,8s,E,S,P,F,C"));
        assertGraterThan(
                calculateXScore("1m,2m,3m,4m,5p,6p,7s,8s,E,S,P,F,3s", 4),
                calculateXScore("1m,2m,3m,4m,5p,6p,7s,8s,E,S,P,F,C", 4));
    }
}

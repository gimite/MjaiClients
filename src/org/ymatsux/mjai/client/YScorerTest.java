package org.ymatsux.mjai.client;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class YScorerTest {

    private final boolean DEBUG = false;

    private List<Hai> readHaiList(String string) {
        String[] haiStrings = string.split(",");
        List<Hai> haiList = new ArrayList<Hai>();
        for (String haiString : haiStrings) {
            haiList.add(Hai.parse(haiString));
        }
        return haiList;
    }

    private double calculateYScore(String tehaisString, int maxShantensu) {
        List<Hai> tehais = readHaiList(tehaisString);
        YScorer yScorer = new YScorer(
                tehais,
                false, readHaiList("5m"), Hai.parse("E"), Hai.parse("S"));
        double yScore = yScorer.calculateYScore(maxShantensu);
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
        assertGraterThan(
                calculateYScore("1m,3m,5m,7m,9m,1p,3p,5p,7p,9p,P,F,5p", 4),
                calculateYScore("1m,3m,5m,7m,9m,1p,3p,5p,7p,9p,P,F,C", 4));
    }
}

package org.ymatsux.mjai.client;

import static org.junit.Assert.assertEquals;
import static org.ymatsux.mjai.client.TestUtil.createRemainingVector;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class YScorerTest {

    private final boolean DEBUG = false;

    private int calculateShantensuWithinRemainingHais(
            String tehaisString, String otherVisibleHaisString) {
        List<Hai> tehais = readHaiList(tehaisString);
        List<Hai> otherVisibleHais = readHaiList(otherVisibleHaisString);
        int[] remainingVector = createRemainingVector(tehais, otherVisibleHais);
        return ShantensuUtil.calculateShantensuWithinRemainingHais(tehais, remainingVector);
    }

    private List<Hai> readHaiList(String string) {
        String[] haiStrings = string.split(",");
        List<Hai> haiList = new ArrayList<Hai>();
        for (String haiString : haiStrings) {
            haiList.add(Hai.parse(haiString));
        }
        return haiList;
    }

    private double calculateYScore(
            String tehaisString, String otherVisibleHaisString, int maxShantensu) {
        List<Hai> tehais = readHaiList(tehaisString);
        List<Hai> otherVisibleHais = readHaiList(otherVisibleHaisString);
        int[] remainingVector = createRemainingVector(tehais, otherVisibleHais);
        YScorer yScorer = new YScorer(
                tehais,
                remainingVector,
                false,
                readHaiList("N"),
                Hai.parse("E"),
                Hai.parse("S"));
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
        assertEquals(3, calculateShantensuWithinRemainingHais(
                "1m,3m,5m,7m,9m,1p,2p,3p,7p,9p,P,F,5p", "W"));
        assertEquals(3, calculateShantensuWithinRemainingHais(
                "1m,3m,5m,7m,9m,1p,2p,3p,7p,9p,P,F,C", "W"));
        assertGraterThan(
                calculateYScore("1m,3m,5m,7m,9m,1p,2p,3p,7p,9p,P,F,5p", "W", 3),
                calculateYScore("1m,3m,5m,7m,9m,1p,2p,3p,7p,9p,P,F,C", "W", 3));

        assertEquals(3, calculateShantensuWithinRemainingHais(
                "1m,3m,5m,7m,9m,1p,2p,3p,5p,7p,9p,P,F", "W,C"));
        assertEquals(3, calculateShantensuWithinRemainingHais(
                "1m,3m,5m,7m,9m,1p,2p,3p,5p,7p,9p,P,C", "W,C"));
        assertGraterThan(
                calculateYScore("1m,3m,5m,7m,9m,1p,2p,3p,5p,7p,9p,P,F", "W,C", 3),
                calculateYScore("1m,3m,5m,7m,9m,1p,2p,3p,5p,7p,9p,P,C", "W,C", 3));

        assertEquals(0, calculateShantensuWithinRemainingHais(
                "2m,3m,4m,5m,1p,2p,3p,4p,5p,6p,E,E,E", "W"));
        assertEquals(0, calculateShantensuWithinRemainingHais(
                "2m,3m,4m,6m,1p,2p,3p,4p,5p,6p,E,E,E", "W"));
        assertGraterThan(
                calculateYScore("2m,3m,4m,5m,1p,2p,3p,4p,5p,6p,E,E,E", "W", 0),
                calculateYScore("2m,3m,4m,6m,1p,2p,3p,4p,5p,6p,E,E,E", "W", 0));

        assertEquals(0, calculateShantensuWithinRemainingHais(
                "2m,3m,4m,6m,1p,2p,3p,4p,5p,6p,E,E,E", "W,2m,2m,5m,5m"));
        assertEquals(0, calculateShantensuWithinRemainingHais(
                "2m,3m,4m,5m,1p,2p,3p,4p,5p,6p,E,E,E", "W,2m,2m,5m,5m"));
        assertGraterThan(
                calculateYScore("2m,3m,4m,6m,1p,2p,3p,4p,5p,6p,E,E,E", "W,2m,2m,5m,5m", 0),
                calculateYScore("2m,3m,4m,5m,1p,2p,3p,4p,5p,6p,E,E,E", "W,2m,2m,5m,5m", 0));
    }
}

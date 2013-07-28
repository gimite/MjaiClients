package org.ymatsux.mjai.client;

import static org.junit.Assert.assertEquals;
import static org.ymatsux.mjai.client.TestUtil.createRemainingVector;
import static org.ymatsux.mjai.client.TestUtil.readHaiList;

import java.util.List;

import org.junit.Test;

public class XScorerTest {

    private final boolean DEBUG = false;

    private int calculateShantensuWithinRemainingHais(
            String tehaisString, String otherVisibleHaisString) {
        List<Hai> tehais = readHaiList(tehaisString);
        List<Hai> otherVisibleHais = readHaiList(otherVisibleHaisString);
        int[] remainingVector = createRemainingVector(tehais, otherVisibleHais);
        return ShantensuUtil.calculateShantensuWithinRemainingHais(tehais, remainingVector);
    }

    private double calculateXScore(
            String tehaisString,
            String otherVisibleHaisString,
            int shantensuWithinRemainingHais) {
        List<Hai> tehais = readHaiList(tehaisString);
        List<Hai> otherVisibleHais = readHaiList(otherVisibleHaisString);
        int[] remainingVector = createRemainingVector(tehais, otherVisibleHais);
        XScorer xScorer = new XScorer(
                tehais,
                remainingVector,
                false,
                readHaiList("N"),
                Hai.parse("E"),
                Hai.parse("S"));
        double xScore = xScorer.calculateXScore(shantensuWithinRemainingHais);
        if (DEBUG) {
            System.out.println(tehaisString);
            System.out.println(xScore);
            System.out.println();
        }
        return xScore;
    }

    public void assertGraterThan(double x, double y) {
        if (x <= y) {
            throw new AssertionError("Expected x > y however x = " + x + " y = " + y);
        }
    }

    @Test
    public void test() {
        assertEquals(4, calculateShantensuWithinRemainingHais(
                "1m,2m,3m,4m,5p,6p,7s,8s,E,S,P,F,3s", "W"));
        assertEquals(4, calculateShantensuWithinRemainingHais(
                "1m,2m,3m,4m,5p,6p,7s,8s,E,S,P,F,C", "W"));
        assertGraterThan(
                calculateXScore("1m,2m,3m,4m,5p,6p,7s,8s,E,S,P,F,3s", "W", 4),
                calculateXScore("1m,2m,3m,4m,5p,6p,7s,8s,E,S,P,F,C", "W", 4));

        assertEquals(4, calculateShantensuWithinRemainingHais(
                "1m,2m,3m,4m,5p,6p,7s,8s,E,S,W,P,F", "W,C"));
        assertEquals(4, calculateShantensuWithinRemainingHais(
                "1m,2m,3m,4m,5p,6p,7s,8s,E,S,W,P,C", "W,C"));
        assertGraterThan(
                calculateXScore("1m,2m,3m,4m,5p,6p,7s,8s,E,S,W,P,F", "W,C", 4),
                calculateXScore("1m,2m,3m,4m,5p,6p,7s,8s,E,S,W,P,C", "W,C", 4));
    }
}

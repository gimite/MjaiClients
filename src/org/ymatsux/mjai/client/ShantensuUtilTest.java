package org.ymatsux.mjai.client;

import static org.junit.Assert.assertEquals;
import static org.ymatsux.mjai.client.CommonConsts.NUM_HAI_ID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class ShantensuUtilTest {

    private List<Hai> readHaiList(String string) throws Exception {
        if (string.isEmpty()) {
            return new ArrayList<Hai>();
        }
        String[] haiStrings = string.split(",");
        List<Hai> haiList = new ArrayList<Hai>();
        for (String haiString : haiStrings) {
            haiList.add(Hai.parse(haiString));
        }
        return haiList;
    }

    private int calculateShantensuWithinRemainingHais (
            String tehaisString, String otherVisibleHaisString) throws Exception {
        List<Hai> tehais = readHaiList(tehaisString);
        List<Hai> otherVisibleHais = readHaiList(otherVisibleHaisString);
        int[] remainingVector = new int[NUM_HAI_ID];
        Arrays.fill(remainingVector, 4);
        for (Hai tehai : tehais) {
            remainingVector[tehai.getId()]--;
        }
        for (Hai otherVisibleHai : otherVisibleHais) {
            remainingVector[otherVisibleHai.getId()]--;
        }
        return ShantensuUtil.calculateShantensuWithinRemainingHais(tehais, remainingVector);
    }

    @Test
    public void testCalculateShantensu() throws Exception {
        assertEquals(0, ShantensuUtil.calculateShantensu(readHaiList(
                "1m,1m,1m,2m,3m,4m,5m,6m,7m,8m,9m,9m,9m")));
        assertEquals(4, ShantensuUtil.calculateShantensu(readHaiList(
                "1m,3m,5m,7m,9m,1p,3p,5p,7p,9p,P,F,C")));
        assertEquals(6, ShantensuUtil.calculateShantensu(readHaiList(
                "1m,5m,9m,1p,5p,9p,1s,5s,9s,E,S,W,N")));
        assertEquals(5, ShantensuUtil.calculateShantensu(readHaiList(
                "1m,5m,9m,3p,5p,7p,4s,5s,6s,E,S,W,N")));
        assertEquals(1, ShantensuUtil.calculateShantensu(readHaiList(
                "1m,2m,3m,4m,5m,1p,2p,3p,4p,5p,E,E,E")));
        assertEquals(2, ShantensuUtil.calculateShantensu(readHaiList(
                "1m,1m,5m,5m,9m,9m,2p,5p,8p,2s,8s,C,C")));
    }

    @Test
    public void testCalculateShantensuWithinRemainingHais() throws Exception {
        assertEquals(0, calculateShantensuWithinRemainingHais(
                "1m,2m,3m,4m,5m,1p,2p,3p,4p,4p,E,E,E",
                ""));
        assertEquals(1, calculateShantensuWithinRemainingHais(
                "1m,2m,3m,4m,5m,1p,2p,3p,4p,4p,E,E,E",
                "3m,3m,3m,6m,6m,6m,6m"));
        assertEquals(1, calculateShantensuWithinRemainingHais(
                "1m,1m,5m,5m,9m,9m,2p,2p,2s,5s,8s,C,C",
                ""));
        assertEquals(3, calculateShantensuWithinRemainingHais(
                "1m,1m,5m,5m,9m,9m,2p,2p,2s,5s,8s,C,C",
                "2s,2s,2s,5s,5s,5s,8s,8s,8s"));
        assertEquals(2, calculateShantensuWithinRemainingHais(
                "1m,1m,5m,5m,9m,9m,2p,2p,2s,5s,8s,C,C",
                "2s,2s,2s,5s,5s,5s"));
        assertEquals(1, calculateShantensuWithinRemainingHais(
                "1m,1m,5m,5m,9m,9m,2p,2p,2s,5s,8s,C,C",
                "2s,2s,2s"));
    }
}

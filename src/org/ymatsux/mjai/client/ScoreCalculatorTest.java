package org.ymatsux.mjai.client;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ScoreCalculatorTest {

    @Test
    public void testMenzenchintsumoho() {
        // Menzenchintsumoho (1 fan)
        // tsumo (2 fu) + anko (4 fu)
        // 1 fan 30 fu
        assertEquals(
                1000, calculateScore("1m,2m,3m,4p,5p,6p,7s,8s,9s,4s,4s,6s,6s", "4s", true));
        // Menzenchintsumoho (1 fan)
        // tsumo (2 fu) + anko(4 fu) + tanki (2 fu)
        // 1 fan 30 fu
        assertEquals(
                1300, calculateScore("1m,2m,3m,4p,5p,6p,7s,7s,7s,4s,4s,4s,6s", "6s", true));

        // Menzenchintsumoho (1 fan)
        // tsumo (2 fu) + yaochuhai anko (8 fu)
        // 1 fan 30 fu
        assertEquals(
                1000, calculateScore("1m,2m,3m,4p,5p,6p,9p,9p,9p,1s,1s,7s,8s", "9s", true));
        // Menzenchintsumoho (1 fan)
        // tsumo (2 fu) + yaochuhai anko (8 fu) + kanchan (2 fu)
        // 1 fan 40 fu
        assertEquals(
                1300, calculateScore("1m,2m,3m,4p,5p,6p,9p,9p,9p,1s,1s,7s,9s", "8s", true));
        // Menzenchintsumoho (1 fan)
        // tsumo (2 fu) + yaochuhai anko (8 fu) + penchan (2 fu)
        // 1 fan 40 fu
        assertEquals(
                1300, calculateScore("1m,2m,3m,4p,5p,6p,9p,9p,9p,1s,1s,8s,9s", "7s", true));
        // Menzenchintsumoho (1 fan)
        // tsumo (2 fu) + yaochuhai anko (8 fu) + tanki (2 fu)
        // 1 fan 40 fu
        assertEquals(
                1300, calculateScore("1m,2m,3m,4p,5p,6p,9p,9p,9p,1s,7s,8s,9s", "1s", true));
    }

    @Test
    public void testPinfu() {
        // Not Pinfu (kanchan)
        // Menzenchintsumoho (1 fan)
        // tsumo (2 fu) + yaochuhai anko (8 fu) + kanchan (2 fu)
        // 1 fan 30 fu
        assertEquals(
                1000, calculateScore("1m,2m,3m,4p,5p,6p,7p,8p,9p,1s,1s,7s,9s", "8s", true));

        // Not Pinfu (penchan)
        // Menzenchintsumoho (1 fan)
        // tsumo (2 fu) + yaochuhai anko (8 fu) + penchan (2 fu)
        // 1 fan 30 fu
        assertEquals(
                1000, calculateScore("1m,2m,3m,4p,5p,6p,7p,8p,9p,1s,1s,8s,9s", "7s", true));

        // Not pinfu (janto sangenpai)
        // Menzenchintsumoho (1 fan)
        // tsumo (2fu) + janto sangenpai (2 fu)
        // 1 fan 30 fu
        assertEquals(1000, calculateScore("1m,2m,3m,4p,5p,6p,P,P,4s,5s,5s,6s,7s", "6s", true));

        // Not pinfu (janto bakaze)
        // Menzenchintsumoho (1 fan)
        // tsumo (2fu) + janto sangenpai (2 fu)
        // 1 fan 30 fu
        assertEquals(1000, calculateScore("1m,2m,3m,4p,5p,6p,E,E,4s,5s,5s,6s,7s", "6s", true));

        // Not pinfu (janto jikaze)
        // Menzenchintsumoho (1 fan)
        // tsumo (2fu) + janto sangenpai (2 fu)
        // 1 fan 30 fu
        assertEquals(1000, calculateScore("1m,2m,3m,4p,5p,6p,S,S,4s,5s,5s,6s,7s", "6s", true));

        // Menzenchintsumoho (1 fan) + Pinfu (1 fan)
        // 2 fan 20 fu
        assertEquals(1300, calculateScore("1m,2m,3m,4p,5p,6p,7p,7p,4s,5s,5s,6s,7s", "6s", true));

        // Menzenchintsumoho (1 fan) + Pinfu (1 fan)
        // 2 fan 20 fu
        assertEquals(1300, calculateScore("1m,2m,3m,4p,5p,6p,N,N,4s,5s,5s,6s,7s", "6s", true));
    }

    private List<Hai> readHaiList(String string) {
        String[] haiStrings = string.split(",");
        List<Hai> haiList = new ArrayList<Hai>();
        for (String haiString : haiStrings) {
            haiList.add(Hai.parse(haiString));
        }
        return haiList;
    }

    public int calculateScore(String tehaisString, String agarihaiString, boolean isTsumoho) {
        List<Hai> tehais = readHaiList(tehaisString);
        Hai agarihai = Hai.parse(agarihaiString);
        ScoreCalculator calculator = new ScoreCalculator(
                tehais, agarihai, isTsumoho, false,
                false, readHaiList("5m"), Hai.parse("E"), Hai.parse("S"));
        return calculator.calculateScore();
    }
}

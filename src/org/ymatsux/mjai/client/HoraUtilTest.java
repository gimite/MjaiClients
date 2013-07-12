package org.ymatsux.mjai.client;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class HoraUtilTest {

    private List<Hai> readHaiList(String string) throws Exception {
        String[] haiStrings = string.split(",");
        List<Hai> haiList = new ArrayList<Hai>();
        for (String haiString : haiStrings) {
            haiList.add(Hai.parse(haiString));
        }
        return haiList;
    }

    @Test
    public void testIsHora() throws Exception {
        assertTrue(HoraUtil.isHoraIgnoreYaku(readHaiList(
                "E,E,S,S,W,W,N,N,P,P,F,F,C,C")));
        assertFalse(HoraUtil.isHoraIgnoreYaku(readHaiList(
                "E,E,S,S,W,W,N,N,P,P,F,F,F,F")));
        assertTrue(HoraUtil.isHoraIgnoreYaku(readHaiList(
                "E,E,E,S,S,S,W,W,W,N,N,N,P,P")));
        assertTrue(HoraUtil.isHoraIgnoreYaku(readHaiList(
                "1m,1m,1m,2m,3m,4m,5m,5m,6m,7m,8m,9m,9m,9m")));
        assertTrue(HoraUtil.isHoraIgnoreYaku(readHaiList(
                "1m,2m,3m,4p,5p,6p,7s,8s,9s,E,E,E,F,F")));
        assertTrue(HoraUtil.isHoraIgnoreYaku(readHaiList(
                "1m,1m,1m,1m,2m,2m,2m,2m,3m,3m,3m,3m,E,E")));
        assertFalse(HoraUtil.isHoraIgnoreYaku(readHaiList(
                "1m,1m,1m,1m,2m,2m,2m,2m,4m,4m,4m,4m,E,E")));

        assertTrue(HoraUtil.isHoraIgnoreYaku(
                readHaiList("1m,1m,1m,1m,2m,2m,2m,3m,3m,3m,3m,E,E"),
                Hai.parse("2m")));
        assertFalse(HoraUtil.isHoraIgnoreYaku(
                readHaiList("1m,1m,1m,1m,2m,2m,2m,3m,3m,3m,3m,E,E"),
                Hai.parse("W")));
    }
}

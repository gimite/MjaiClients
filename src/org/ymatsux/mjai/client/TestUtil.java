package org.ymatsux.mjai.client;

import static org.ymatsux.mjai.client.CommonConsts.NUM_HAI_ID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestUtil {

    public static List<Hai> readHaiList(String string) {
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

    public static int[] createRemainingVector(List<Hai> tehais, List<Hai> otherVisibleHais) {
        int[] remainingVector = new int[NUM_HAI_ID];
        Arrays.fill(remainingVector, 4);
        for (Hai tehai : tehais) {
            remainingVector[tehai.getId()]--;
        }
        for (Hai otherVisibleHai : otherVisibleHais) {
            remainingVector[otherVisibleHai.getId()]--;
        }
        return remainingVector;
    }

    private TestUtil() {
    }
}

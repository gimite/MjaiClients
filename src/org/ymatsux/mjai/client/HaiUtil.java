package org.ymatsux.mjai.client;

import static org.ymatsux.mjai.client.CommonConsts.NUM_HAI_ID;

import java.util.List;

public class HaiUtil {

    public static boolean isYaochuhai(int haiId) {
        return haiId == 0 || haiId == 8 ||
                haiId == 9 || haiId == 17 ||
                haiId == 18 || haiId == 26 ||
                (27 <= haiId && haiId < 34);
    }

    public static int[] haiListToCountVector(List<Hai> haiList) {
        int[] countVector = new int[NUM_HAI_ID];
        for (Hai hai : haiList) {
            countVector[hai.getId()]++;
        }
        return countVector;
    }

    private HaiUtil() {
    }
}

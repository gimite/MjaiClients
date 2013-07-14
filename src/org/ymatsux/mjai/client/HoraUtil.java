package org.ymatsux.mjai.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HoraUtil {

    private static final int NUM_HAI_ID = 34;
    private static final int NUM_MENTSU_ID = 55;

    public static boolean isHoraIgnoreYaku(List<Hai> hais) {
        int[] countVector = new int[NUM_HAI_ID];
        for (Hai hai : hais) {
            countVector[hai.getId()]++;
        }
        if (isChitoitsu(countVector)) {
            return true;
        }
        // TODO: Handle Kokushimuso.
        return isHoraIgnoreYakuInternal(countVector, new int[NUM_HAI_ID], 4, 0);
    }

    public static boolean isHoraIgnoreYaku(List<Hai> tehais, Hai agarihai) {
        List<Hai> hais = new ArrayList<Hai>(tehais);
        hais.add(agarihai);
        return isHoraIgnoreYaku(hais);
    }

    private static boolean isHoraIgnoreYakuInternal(
            int[] currentVector, int[] targetVector, int leftMentsu, int minMentsuId) {
        if (leftMentsu == 0) {
            // Add janto
            for (int haiId = 0; haiId < NUM_HAI_ID; haiId++) {
                targetVector[haiId] += 2;
                if (Arrays.equals(currentVector, targetVector)) {
                    return true;
                }
                targetVector[haiId] -= 2;
            }
            return false;
        }

        for (int mentsuId = minMentsuId; mentsuId < NUM_MENTSU_ID; mentsuId++) {
            MentsuUtil.addMentsu(targetVector, mentsuId);
            boolean isValid = true;
            for (int haiId = 0; haiId < NUM_HAI_ID; haiId++) {
                if (currentVector[haiId] < targetVector[haiId]) {
                    isValid = false;
                    break;
                }
            }
            if (isValid) {
                boolean isHora = isHoraIgnoreYakuInternal(
                        currentVector, targetVector, leftMentsu - 1, mentsuId);
                if (isHora) {
                    return true;
                }
            }
            MentsuUtil.removeMentsu(targetVector, mentsuId);
        }
        return false;
    }

    private static boolean isChitoitsu(int[] countVector) {
        for (int haiId = 0; haiId < NUM_HAI_ID; haiId++) {
            if (countVector[haiId] != 0 && countVector[haiId] != 2) {
                return false;
            }
        }
        return true;
    }

    private HoraUtil() {
    }
}

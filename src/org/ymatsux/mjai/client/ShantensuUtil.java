package org.ymatsux.mjai.client;

import java.util.List;

public class ShantensuUtil {

    private static final int NUM_HAI_ID = 34;
    private static final int NUM_MENTSU_ID = 55;

    public static int calculateShantensu(List<Hai> hais) {
        int[] countVector = new int[NUM_HAI_ID];
        for (Hai hai : hais) {
            countVector[hai.getId()]++;
        }
        int chitoitsuShantensu = calculateChitoitsuShantensu(countVector);
        // TODO: Handle Kokushimuso
        return calculateShantensuInternal(
                countVector, new int[NUM_HAI_ID], 4, 0, chitoitsuShantensu);
    }

    private static int calculateChitoitsuShantensu(int[] currentVector) {
        int numPairs = 0;
        for (int haiId = 0; haiId < NUM_HAI_ID; haiId++) {
            if (currentVector[haiId] == 2) {
                numPairs++;
            }
        }
        return 6 - numPairs;
    }

    private static int calculateShantensuInternal(
            int[] currentVector, int[] targetVector, int leftMentsu,
            int minMentsuIndex, int foundMinShantensu) {
        if (leftMentsu == 0) {
            // Add janto.
            int minShantensu = foundMinShantensu;
            for (int haiId = 0; haiId < NUM_HAI_ID; haiId++) {
                targetVector[haiId] += 2;
                if (isValidTargetVector(targetVector)) {
                    int shantensu = calculateShantensuLowerBound(currentVector, targetVector);
                    minShantensu = Math.min(shantensu, minShantensu);
                }
                targetVector[haiId] -= 2;
            }
            return minShantensu;
        }

        int minShantensu = foundMinShantensu;
        for (int mentsuIndex = minMentsuIndex; mentsuIndex < NUM_MENTSU_ID; mentsuIndex++) {
            MentsuUtil.addMentsu(targetVector, mentsuIndex);
            int lowerBound = calculateShantensuLowerBound(currentVector, targetVector);
            if (isValidTargetVector(targetVector) && lowerBound < foundMinShantensu) {
                int shantensu = calculateShantensuInternal(
                        currentVector, targetVector, leftMentsu - 1, mentsuIndex, minShantensu);
                minShantensu = Math.min(shantensu, minShantensu);
            }
            MentsuUtil.removeMentsu(targetVector, mentsuIndex);
        }
        return minShantensu;
    }

    private static int calculateShantensuLowerBound(int[] currentVector, int[] targetVector) {
        int count = 0;
        for (int haiIndex = 0; haiIndex < NUM_HAI_ID; haiIndex++) {
            if (targetVector[haiIndex] > currentVector[haiIndex]) {
                count += targetVector[haiIndex] - currentVector[haiIndex];
            }
        }
        return count - 1;
    }

    private static boolean isValidTargetVector(int[] targetVector) {
        for (int haiIndex = 0; haiIndex < NUM_HAI_ID; haiIndex++) {
            if (targetVector[haiIndex] > 4) {
                return false;
            }
        }
        return true;
    }

    private ShantensuUtil() {
    }
}

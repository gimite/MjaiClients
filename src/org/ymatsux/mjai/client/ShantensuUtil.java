package org.ymatsux.mjai.client;

import java.util.List;

public class ShantensuUtil {

    private static final int NUM_HAI = 34;
    private static final int NUM_MENTSU = 55;

    public static int calculateShantensu(List<Hai> hais) {
        int[] countVector = new int[NUM_HAI];
        for (Hai hai : hais) {
            countVector[hai.getIndex()]++;
        }
        int chitoitsuShantensu = calculateChitoitsuShantensu(countVector);
        // TODO: Handle Kokushimuso
        return calculateShantensuInternal(countVector, new int[NUM_HAI], 4, 0, chitoitsuShantensu);
    }

    private static int calculateChitoitsuShantensu(int[] currentVector) {
        int numPairs = 0;
        for (int haiIndex = 0; haiIndex < NUM_HAI; haiIndex++) {
            if (currentVector[haiIndex] == 2) {
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
            for (int haiIndex = 0; haiIndex < NUM_HAI; haiIndex++) {
                targetVector[haiIndex] += 2;
                if (isValidTargetVector(targetVector)) {
                    int shantensu = calculateShantensuLowerBound(currentVector, targetVector);
                    minShantensu = Math.min(shantensu, minShantensu);
                }
                targetVector[haiIndex] -= 2;
            }
            return minShantensu;
        }

        int minShantensu = foundMinShantensu;
        for (int mentsuIndex = minMentsuIndex; mentsuIndex < NUM_MENTSU; mentsuIndex++) {
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
        for (int haiIndex = 0; haiIndex < NUM_HAI; haiIndex++) {
            if (targetVector[haiIndex] > currentVector[haiIndex]) {
                count += targetVector[haiIndex] - currentVector[haiIndex];
            }
        }
        return count - 1;
    }

    private static boolean isValidTargetVector(int[] targetVector) {
        for (int haiIndex = 0; haiIndex < NUM_HAI; haiIndex++) {
            if (targetVector[haiIndex] > 4) {
                return false;
            }
        }
        return true;
    }

    private ShantensuUtil() {
    }
}

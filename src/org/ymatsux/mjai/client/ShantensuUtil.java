package org.ymatsux.mjai.client;

import java.util.List;

public class ShantensuUtil {

    private static final int NUM_HAI = 34;
    private static final int MANZU_START = 0;
    private static final int PINZU_START = 9;
    private static final int SOZU_START = 18;

    private static final int NUM_MENTSU = 55;
    private static final int MANZU_SHUNTSU_START = 0;
    private static final int PINZU_SHUNTSU_START = 7;
    private static final int SOZU_SHUNTSU_START = 14;
    private static final int KOTSU_START = 21;
    private static final int[][] MENTSUS;

    static {
        MENTSUS = new int[NUM_MENTSU][3];
        for (int i = MANZU_SHUNTSU_START; i < PINZU_SHUNTSU_START; i++) {
            MENTSUS[i][0] = i - MANZU_SHUNTSU_START + MANZU_START;
            MENTSUS[i][1] = i - MANZU_SHUNTSU_START + MANZU_START + 1;
            MENTSUS[i][2] = i - MANZU_SHUNTSU_START + MANZU_START + 2;
        }
        for (int i = PINZU_SHUNTSU_START; i < SOZU_SHUNTSU_START; i++) {
            MENTSUS[i][0] = i - PINZU_SHUNTSU_START + PINZU_START;
            MENTSUS[i][1] = i - PINZU_SHUNTSU_START + PINZU_START + 1;
            MENTSUS[i][2] = i - PINZU_SHUNTSU_START + PINZU_START + 2;
        }
        for (int i = SOZU_SHUNTSU_START; i < KOTSU_START; i++) {
            MENTSUS[i][0] = i - SOZU_SHUNTSU_START + SOZU_START;
            MENTSUS[i][1] = i - SOZU_SHUNTSU_START + SOZU_START + 1;
            MENTSUS[i][2] = i - SOZU_SHUNTSU_START + SOZU_START + 2;
        }
        for (int i = KOTSU_START; i < NUM_MENTSU; i++) {
            MENTSUS[i][0] = MENTSUS[i][1] = MENTSUS[i][2] = i - KOTSU_START;
        }
    }

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
            addMentsu(targetVector, mentsuIndex);
            int lowerBound = calculateShantensuLowerBound(currentVector, targetVector);
            if (isValidTargetVector(targetVector) && lowerBound < foundMinShantensu) {
                int shantensu = calculateShantensuInternal(
                        currentVector, targetVector, leftMentsu - 1, mentsuIndex, minShantensu);
                minShantensu = Math.min(shantensu, minShantensu);
            }
            removeMentsu(targetVector, mentsuIndex);
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

    private static void addMentsu(int[] countVector, int mentsuIndex) {
        countVector[MENTSUS[mentsuIndex][0]]++;
        countVector[MENTSUS[mentsuIndex][1]]++;
        countVector[MENTSUS[mentsuIndex][2]]++;
    }

    private static void removeMentsu(int[] countVector, int mentsuIndex) {
        countVector[MENTSUS[mentsuIndex][0]]--;
        countVector[MENTSUS[mentsuIndex][1]]--;
        countVector[MENTSUS[mentsuIndex][2]]--;
    }

    private ShantensuUtil() {
    }
}

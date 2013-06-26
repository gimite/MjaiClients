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
                    int distance = calculateShantensuLowerBound(currentVector, targetVector);
                    minShantensu = Math.min(distance, minShantensu);
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
        if (mentsuIndex < 0 || NUM_MENTSU <= mentsuIndex) {
            throw new IllegalArgumentException();
        }
        if (MANZU_SHUNTSU_START <= mentsuIndex && mentsuIndex < PINZU_SHUNTSU_START) {
            // Manzu shuntsu
            int startIndex = mentsuIndex - MANZU_SHUNTSU_START + MANZU_START;
            countVector[startIndex]++;
            countVector[startIndex + 1]++;
            countVector[startIndex + 2]++;
        } else if (PINZU_SHUNTSU_START <= mentsuIndex && mentsuIndex < SOZU_SHUNTSU_START) {
            // Pinzu shuntsu
            int startIndex = mentsuIndex - PINZU_SHUNTSU_START + PINZU_START;
            countVector[startIndex]++;
            countVector[startIndex + 1]++;
            countVector[startIndex + 2]++;
        } else if (SOZU_SHUNTSU_START <= mentsuIndex && mentsuIndex < KOTSU_START) {
            // Sozu shuntsu
            int startIndex = mentsuIndex - SOZU_SHUNTSU_START + SOZU_START;
            countVector[startIndex]++;
            countVector[startIndex + 1]++;
            countVector[startIndex + 2]++;
        } else {
            // Kotsu
            countVector[mentsuIndex - KOTSU_START] += 3;
        }
    }

    private static void removeMentsu(int[] countVector, int mentsuIndex) {
        if (mentsuIndex < 0 || NUM_MENTSU <= mentsuIndex) {
            throw new IllegalArgumentException();
        }
        if (MANZU_SHUNTSU_START <= mentsuIndex && mentsuIndex < PINZU_SHUNTSU_START) {
            // Manzu shuntsu
            int startIndex = mentsuIndex - MANZU_SHUNTSU_START + MANZU_START;
            countVector[startIndex]--;
            countVector[startIndex + 1]--;
            countVector[startIndex + 2]--;
        } else if (PINZU_SHUNTSU_START <= mentsuIndex && mentsuIndex < SOZU_SHUNTSU_START) {
            // Pinzu shuntsu
            int startIndex = mentsuIndex - PINZU_SHUNTSU_START + PINZU_START;
            countVector[startIndex]--;
            countVector[startIndex + 1]--;
            countVector[startIndex + 2]--;
        } else if (SOZU_SHUNTSU_START <= mentsuIndex && mentsuIndex < KOTSU_START) {
            // Sozu shuntsu
            int startIndex = mentsuIndex - SOZU_SHUNTSU_START + SOZU_START;
            countVector[startIndex]--;
            countVector[startIndex + 1]--;
            countVector[startIndex + 2]--;
        } else {
            // Kotsu
            countVector[mentsuIndex - KOTSU_START] -= 3;
        }
    }

    private ShantensuUtil() {
    }
}

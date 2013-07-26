package org.ymatsux.mjai.client;

import static org.ymatsux.mjai.client.CommonConsts.NUM_HAI_ID;
import static org.ymatsux.mjai.client.CommonConsts.NUM_MENTSU_ID;

import java.util.List;

public class ShantensuUtil {

    public static int calculateShantensu(List<Hai> hais) {
        int[] countVector = HaiUtil.haiListToCountVector(hais);
        int chitoitsuShantensu = calculateChitoitsuShantensu(countVector);
        // TODO: Handle Kokushimuso
        return calculateShantensuInternal(
                countVector, new int[NUM_HAI_ID], 4, 0, chitoitsuShantensu);
    }

    private static int calculateChitoitsuShantensu(int[] currentVector) {
        int numPairs = 0;
        int numSingles = 0;
        for (int haiId = 0; haiId < NUM_HAI_ID; haiId++) {
            if (currentVector[haiId] >= 2) {
                numPairs++;
            } else if (currentVector[haiId] == 1) {
                numSingles++;
            }
        }
        int requiredPairs = 7 - numPairs;
        if (numSingles >= requiredPairs) {
            return requiredPairs - 1;
        } else {
            return numSingles + (requiredPairs - numSingles) * 2 - 1;
        }
    }

    private static int calculateShantensuInternal(
            int[] currentVector, int[] targetVector, int leftMentsu, int minMentsuId,
            int foundMinShantensu) {
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
        for (int mentsuId = minMentsuId; mentsuId < NUM_MENTSU_ID; mentsuId++) {
            MentsuUtil.addMentsu(targetVector, mentsuId);
            int lowerBound = calculateShantensuLowerBound(currentVector, targetVector);
            if (isValidTargetVector(targetVector) && lowerBound < foundMinShantensu) {
                int shantensu = calculateShantensuInternal(
                        currentVector, targetVector, leftMentsu - 1, mentsuId, minShantensu);
                minShantensu = Math.min(shantensu, minShantensu);
            }
            MentsuUtil.removeMentsu(targetVector, mentsuId);
        }
        return minShantensu;
    }

    private static int calculateShantensuLowerBound(int[] currentVector, int[] targetVector) {
        int count = 0;
        for (int haiId = 0; haiId < NUM_HAI_ID; haiId++) {
            if (targetVector[haiId] > currentVector[haiId]) {
                count += targetVector[haiId] - currentVector[haiId];
            }
        }
        return count - 1;
    }

    private static boolean isValidTargetVector(int[] targetVector) {
        for (int haiId = 0; haiId < NUM_HAI_ID; haiId++) {
            if (targetVector[haiId] > 4) {
                return false;
            }
        }
        return true;
    }

    public static int calculateShantensuWithinRemainingHais(List<Hai> hais, int[] remainingVector) {
        int[] countVector = HaiUtil.haiListToCountVector(hais);
        int chitoitsuShantensu = calculateChitoitsuShantensuWithinRemainingHais(
                countVector, remainingVector);
        // TODO: Handle Kokushimuso
        return calculateShantensuWithinRemainingHaisInternal(
                countVector, new int[NUM_HAI_ID], remainingVector, 4, 0, chitoitsuShantensu);
    }

    private static int calculateChitoitsuShantensuWithinRemainingHais(
            int[] currentVector, int[] remainingVector) {
        int numPairs = 0;
        int numEffectiveSingles = 0;
        for (int haiId = 0; haiId < NUM_HAI_ID; haiId++) {
            if (currentVector[haiId] >= 2) {
                numPairs++;
            } else if (currentVector[haiId] == 1 && remainingVector[haiId] >= 1) {
                numEffectiveSingles++;
            }
        }
        int requiredPairs = 7 - numPairs;
        if (numEffectiveSingles >= requiredPairs) {
            return requiredPairs - 1;
        } else {
            return numEffectiveSingles + (requiredPairs - numEffectiveSingles) * 2 - 1;
        }
    }

    private static int calculateShantensuWithinRemainingHaisInternal(
            int[] currentVector, int[] targetVector, int[] remainingVector, int leftMentsu,
            int minMentsuId, int foundMinShantensu) {
        if (leftMentsu == 0) {
            // Add janto.
            int minShantensu = foundMinShantensu;
            for (int haiId = 0; haiId < NUM_HAI_ID; haiId++) {
                targetVector[haiId] += 2;
                if (isValidTargetVectorWithinRemainingHais(
                        targetVector, currentVector, remainingVector)) {
                    int shantensu = calculateShantensuLowerBound(currentVector, targetVector);
                    minShantensu = Math.min(shantensu, minShantensu);
                }
                targetVector[haiId] -= 2;
            }
            return minShantensu;
        }

        int minShantensu = foundMinShantensu;
        for (int mentsuId = minMentsuId; mentsuId < NUM_MENTSU_ID; mentsuId++) {
            MentsuUtil.addMentsu(targetVector, mentsuId);
            int lowerBound = calculateShantensuLowerBound(currentVector, targetVector);
            if (isValidTargetVectorWithinRemainingHais(
                    targetVector, currentVector, remainingVector) &&
                    lowerBound < foundMinShantensu) {
                int shantensu = calculateShantensuWithinRemainingHaisInternal(
                        currentVector, targetVector, remainingVector, leftMentsu - 1, mentsuId,
                        minShantensu);
                minShantensu = Math.min(shantensu, minShantensu);
            }
            MentsuUtil.removeMentsu(targetVector, mentsuId);
        }
        return minShantensu;
    }

    private static boolean isValidTargetVectorWithinRemainingHais(
            int[] targetVector, int[] currentVector, int[] remainingVector) {
        for (int haiId = 0; haiId < NUM_HAI_ID; haiId++) {
            if (targetVector[haiId] > 4) {
                return false;
            }
            if (targetVector[haiId] - currentVector[haiId] > remainingVector[haiId]) {
                return false;
            }
        }
        return true;
    }

    private ShantensuUtil() {
    }
}

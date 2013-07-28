package org.ymatsux.mjai.client;

import static org.ymatsux.mjai.client.CommonConsts.NUM_HAI_ID;
import static org.ymatsux.mjai.client.CommonConsts.NUM_MENTSU_ID;

import java.util.Arrays;
import java.util.List;

/**
 * XScoerer is a simple but fast scorer which is useful when the current shantensu is large.
 * This scorer is intended to use to compare tehais of the same shantensu. Thus it requires the
 * shantensu as the input parameter.
 */
public class XScorer {

    private final List<Hai> tehais;
    private final int[] currentVector;
    private final int[] remainingVector;
    private final boolean isOya;
    List<Hai> doras;
    private final Hai bakaze;
    private final Hai jikaze;

    public XScorer(
            List<Hai> tehais,
            int[] remainingVector,
            boolean isOya, List<Hai> doras, Hai bakaze, Hai jikaze) {
        this.tehais = tehais;
        this.currentVector = HaiUtil.haiListToCountVector(tehais);
        this.remainingVector = remainingVector;
        this.isOya = isOya;
        this.doras = doras;
        this.bakaze = bakaze;
        this.jikaze = jikaze;
    }

    public double calculateXScore(int shantensu) {
        int[] countVector = new int[NUM_HAI_ID];
        for (Hai hai : tehais) {
            countVector[hai.getId()]++;
        }
        int[] mentsuIds = new int[4];
        Arrays.fill(mentsuIds, -1);
        return calculateXScoreInternal(new int[NUM_HAI_ID], 0, 0,mentsuIds, shantensu);
    }

    private double calculateXScoreInternal(
            int[] targetVector,
            int indexInMentsuIds, int minMentsuId, int[] mentsuIds, int shantensu) {
        if (indexInMentsuIds == mentsuIds.length) {
            double xScore = 0.0;
            // Add janto.
            for (int haiId = 0; haiId < NUM_HAI_ID; haiId++) {
                targetVector[haiId] += 2;
                if (isValidTargetVectorWithinRemainingHais(
                        targetVector, currentVector, remainingVector)) {
                    if (calculateShantensuLowerBound(currentVector, targetVector) <= shantensu) {
                        xScore += calculateXScoreInternalForFixedMentsu(
                                targetVector, mentsuIds, haiId, shantensu);
                    }
                }
                targetVector[haiId] -= 2;
            }
            return xScore;
        }

        double xScore = 0.0;
        for (int mentsuId = minMentsuId; mentsuId < NUM_MENTSU_ID; mentsuId++) {
            MentsuUtil.addMentsu(targetVector, mentsuId);
            mentsuIds[indexInMentsuIds] = mentsuId;
            int lowerBound = calculateShantensuLowerBound(currentVector, targetVector);
            if (isValidTargetVectorWithinRemainingHais(
                    targetVector, currentVector, remainingVector) &&
                    lowerBound <= shantensu) {
                xScore += calculateXScoreInternal(
                        targetVector, indexInMentsuIds + 1, mentsuId, mentsuIds, shantensu);
            }
            MentsuUtil.removeMentsu(targetVector, mentsuId);
            mentsuIds[indexInMentsuIds] = -1;
        }
        return xScore;
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

    private double calculateXScoreInternalForFixedMentsu(
            int[] targetVector, int[] mentsuIds, int jantoHaiId, int shantensu) {
        int pseudoFan = 1;
        // Dora
        for (Hai dora : doras) {
            pseudoFan += targetVector[dora.getId()];
        }
        // Tanyao
        if (isTanyao(mentsuIds, jantoHaiId)) {
            pseudoFan += 1;
        }
        // TODO: Do not include pinfu logic for now.
        // Yakuhai
        pseudoFan += countYakuhai(mentsuIds);

        double numCombinationsAsDouble = 1;
        for (int haiId = 0; haiId < NUM_HAI_ID; haiId++) {
            if (targetVector[haiId] - currentVector[haiId] > 0) {
                int numRequired = targetVector[haiId] - currentVector[haiId];
                int numRemaining = remainingVector[haiId];
                if (numRequired > numRemaining) {
                    throw new IllegalStateException();
                }
                numCombinationsAsDouble *= MathUtil.combinationAsDouble(numRemaining, numRequired);
            }
        }

        return scoreFromPseudoFan(pseudoFan) *
                numCombinationsAsDouble * Math.pow(1.0E-2, shantensu);
    }

    private boolean isTanyao(int[] mentsuIds, int jantoHaiId) {
        for (int mentsuId : mentsuIds) {
            if (MentsuUtil.hasYaochuhai(mentsuId)) {
                return false;
            }
        }
        if (HaiUtil.isYaochuhai(jantoHaiId)) {
            return false;
        }

        return true;
    }

    private int countYakuhai(int[] mentsuIds) {
        int count = 0;
        for (int mentsuId : mentsuIds) {
            if (52 <= mentsuId && mentsuId < 55) {
                count++;
            }
            if (mentsuId - 21 == bakaze.getId()) {
                count++;
            }
            if (mentsuId - 21 == jikaze.getId()) {
                count++;
            }
        }
        return count;
    }

    private double scoreFromPseudoFan(int pseudoFan) {
        if (pseudoFan >= 11) {
            return 24000;
        } else if (pseudoFan >= 8) {
            return 16000;
        } else if (pseudoFan >= 6) {
            return 12000;
        } else if (pseudoFan >= 4) {
            return 8000;
        } else if (pseudoFan == 3) {
            return 4000;
        } else if (pseudoFan == 2) {
            return 2000;
        } else if (pseudoFan == 1) {
            return 1000;
        } else {
            return 0;
        }
    }
}

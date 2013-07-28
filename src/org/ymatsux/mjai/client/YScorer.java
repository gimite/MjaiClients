package org.ymatsux.mjai.client;

import static org.ymatsux.mjai.client.CommonConsts.NUM_HAI_ID;
import static org.ymatsux.mjai.client.CommonConsts.NUM_MENTSU_ID;

import java.util.ArrayList;
import java.util.List;

public class YScorer {

    private final List<Hai> tehais;
    private final int[] currentVector;
    private final int[] remainingVector;
    private final boolean isOya;
    List<Hai> doras;
    private final Hai bakaze;
    private final Hai jikaze;

    public YScorer(
            List<Hai> tehais,
            int[] remainingVector, boolean isOya, List<Hai> doras, Hai bakaze, Hai jikaze) {
        this.tehais = tehais;
        this.currentVector = HaiUtil.haiListToCountVector(tehais);
        this.remainingVector = remainingVector;
        this.isOya = isOya;
        this.doras = doras;
        this.bakaze = bakaze;
        this.jikaze = jikaze;
    }

    public double calculateYScore(int maxShantensu) {
        int[] countVector = new int[NUM_HAI_ID];
        for (Hai hai : tehais) {
            countVector[hai.getId()]++;
        }

        return calculateYScoreInternal(new int[NUM_HAI_ID], 0, 0, 4, maxShantensu);
    }

    private double calculateYScoreInternal(
            int[] targetVector,
            int indexInMentsuIds, int minMentsuId, int numMentsu, int maxShantensu) {
        if (indexInMentsuIds == numMentsu) {
            double yScore = 0.0;
            // Add janto.
            for (int haiId = 0; haiId < NUM_HAI_ID; haiId++) {
                targetVector[haiId] += 2;
                if (isValidTargetVectorWithinRemainingHais(
                        targetVector, currentVector, remainingVector)) {
                    int shantensu = calculateShantensuLowerBound(currentVector, targetVector);
                    if (shantensu <= maxShantensu) {
                        yScore += calculateYScoreInternalForFixedTarget(
                                currentVector, targetVector);
                    }
                }
                targetVector[haiId] -= 2;
            }
            return yScore;
        }

        double yScore = 0.0;
        for (int mentsuId = minMentsuId; mentsuId < NUM_MENTSU_ID; mentsuId++) {
            MentsuUtil.addMentsu(targetVector, mentsuId);
            int lowerBound = calculateShantensuLowerBound(currentVector, targetVector);
            if (isValidTargetVectorWithinRemainingHais(
                    targetVector, currentVector, remainingVector) &&
                    lowerBound <= maxShantensu) {
                yScore += calculateYScoreInternal(
                        targetVector, indexInMentsuIds + 1, mentsuId, numMentsu, maxShantensu);
            }
            MentsuUtil.removeMentsu(targetVector, mentsuId);
        }
        return yScore;
    }

    private double calculateYScoreInternalForFixedTarget(
            int[] currentVector, int[] targetVector) {
        List<Integer> agarihaiIdCandidates = new ArrayList<Integer>();
        int shantensu = 0;
        for (int haiId = 0; haiId < NUM_HAI_ID; haiId++) {
            if (targetVector[haiId] > currentVector[haiId]) {
                for (int count = 0; count < targetVector[haiId] - currentVector[haiId]; count++) {
                    agarihaiIdCandidates.add(haiId);
                }
                shantensu += targetVector[haiId] - currentVector[haiId];
            }
        }

        double yScore = 0.0;
        for (int agarihaiId : agarihaiIdCandidates) {
            List<Hai> tehais = new ArrayList<Hai>();
            Hai agarihai = Hai.ofId(agarihaiId);
            for (int haiId = 0; haiId < NUM_HAI_ID; haiId++) {
                for (int count = 0; count < targetVector[haiId] - (haiId == agarihaiId ? 1 : 0);
                        count++) {
                    tehais.add(Hai.ofId(haiId));
                }
            }
            ScoreCalculator scoreCalculator = new ScoreCalculator(
                    tehais, agarihai, true, true, isOya, doras, bakaze, jikaze);
            double expectedScore = scoreCalculator.calculateScore();

            double numCombinationsAsDouble = 1;
            for (int haiId = 0; haiId < NUM_HAI_ID; haiId++) {
                if (targetVector[haiId] - currentVector[haiId] > 0) {
                    int numRequired = targetVector[haiId] - currentVector[haiId];
                    int numRemaining = remainingVector[haiId];
                    if (numRequired > numRemaining) {
                        throw new IllegalStateException();
                    }
                    numCombinationsAsDouble *=
                            MathUtil.combinationAsDouble(numRemaining, numRequired);
                }
            }

            yScore += expectedScore * numCombinationsAsDouble * Math.pow(1.0E-2, shantensu);
        }

        return yScore;
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
}

package org.ymatsux.mjai.client;

import java.util.ArrayList;
import java.util.List;

public class YScorer {

    private static final int NUM_HAI_ID = 34;
    private static final int NUM_MENTSU_ID = 55;

    private final List<Hai> tehais;
    private final boolean isOya;
    List<Hai> doras;
    private final Hai bakaze;
    private final Hai jikaze;

    public YScorer(
            List<Hai> tehais,
            boolean isOya, List<Hai> doras, Hai bakaze, Hai jikaze) {
        this.tehais = tehais;
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

        return calculateYScoreInternal(
                countVector, new int[NUM_HAI_ID], 0, 0, 4, maxShantensu);
    }

    private double calculateYScoreInternal(
            int[] currentVector, int[] targetVector,
            int indexInMentsuIds, int minMentsuId, int numMentsu, int maxShantensu) {
        if (indexInMentsuIds == numMentsu) {
            double yScore = 0.0;
            // Add janto.
            for (int haiId = 0; haiId < NUM_HAI_ID; haiId++) {
                targetVector[haiId] += 2;
                if (isValidTargetVector(targetVector)) {
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
            if (isValidTargetVector(targetVector) && lowerBound <= maxShantensu) {
                yScore += calculateYScoreInternal(
                        currentVector, targetVector, indexInMentsuIds + 1, mentsuId, numMentsu,
                        maxShantensu);
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
            yScore += expectedScore * Math.pow(1 / 34.0, shantensu);
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

    private static boolean isValidTargetVector(int[] targetVector) {
        for (int haiId = 0; haiId < NUM_HAI_ID; haiId++) {
            if (targetVector[haiId] > 4) {
                return false;
            }
        }
        return true;
    }
}

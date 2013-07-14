package org.ymatsux.mjai.client;

import java.util.Arrays;
import java.util.List;

public class ScoreCalculator {

    private static final int NUM_HAI_ID = 34;
    private static final int NUM_MENTSU_ID = 55;

    private final List<Hai> tehais;
    private final Hai agarihai;
    private final boolean isTsumoho;
    private final boolean doneRichi;
    private final boolean isOya;
    private final List<Hai> doras;
    private final Hai bakaze;
    private final Hai jikaze;

    public ScoreCalculator(
            List<Hai> tehais, Hai agarihai, boolean isTsumoho, boolean doneRichi,
            boolean isOya, List<Hai> doras, Hai bakaze, Hai jikaze) {
        this.tehais = tehais;
        this.agarihai = agarihai;
        this.isTsumoho = isTsumoho;
        this.doneRichi = doneRichi;
        this.isOya = isOya;
        this.doras = doras;
        this.bakaze = bakaze;
        this.jikaze = jikaze;
    }

    public int calculateScore() {
        if (!HoraUtil.isHoraIgnoreYaku(tehais, agarihai)) {
            return 0;
        }

        int[] countVector = new int[NUM_HAI_ID];
        for (Hai hai : tehais) {
            countVector[hai.getId()]++;
        }
        countVector[agarihai.getId()]++;

        int[] mentsuIndexes = new int[4];
        return calculateScoreDetermineMentsuPhase(
                countVector, new int[NUM_HAI_ID], 0, 0, mentsuIndexes);
    }

    private int calculateScoreDetermineMentsuPhase(
            int[] currentVector, int[] targetVector,
            int indexInMentsuIds, int minMentsuId, int[] mentsuIds) {
        if (indexInMentsuIds == mentsuIds.length) {
            // Add janto
            for (int haiId = 0; haiId < NUM_HAI_ID; haiId++) {
                targetVector[haiId] += 2;
                if (Arrays.equals(currentVector, targetVector)) {
                    int jantoHaiAd = haiId;
                    return calculateScoreDetermineMachiPhase(mentsuIds, jantoHaiAd);
                }
                targetVector[haiId] -= 2;
            }
            return 0;
        }

        int score = 0;
        for (int mentsuId = minMentsuId; mentsuId < NUM_MENTSU_ID; mentsuId++) {
            MentsuUtil.addMentsu(targetVector, mentsuId);
            mentsuIds[indexInMentsuIds] = mentsuId;
            boolean isValid = true;
            for (int haiId = 0; haiId < NUM_HAI_ID; haiId++) {
                if (currentVector[haiId] < targetVector[haiId]) {
                    isValid = false;
                    break;
                }
            }
            if (isValid) {
                score = Math.max(
                        calculateScoreDetermineMentsuPhase(
                                currentVector, targetVector, indexInMentsuIds + 1, mentsuId,
                                mentsuIds),
                        score);
            }
            MentsuUtil.removeMentsu(targetVector, mentsuId);
            mentsuIds[indexInMentsuIds] = -1;
        }
        return score;
    }

    // Decide the machi.
    private int calculateScoreDetermineMachiPhase(int[] mentsuIndexes, int jantoHaiId) {
        int score = 0;
        for (int machiMentsuIndex = 0; machiMentsuIndex < mentsuIndexes.length;
                machiMentsuIndex++) {
            int[] machiMentsuHaiIds = MentsuUtil.getMentsu(mentsuIndexes[machiMentsuIndex]);
            if (machiMentsuHaiIds[0] == agarihai.getId() ||
                    machiMentsuHaiIds[1] == agarihai.getId() ||
                    machiMentsuHaiIds[2] == agarihai.getId()) {
                Mentsu[] mentsus = new Mentsu[mentsuIndexes.length];
                for (int mentsuIndex = 0; mentsuIndex < mentsuIndexes.length; mentsuIndex++) {
                    int mentsuId = mentsuIndexes[mentsuIndex];
                    if (mentsuIndex == machiMentsuIndex) {
                        mentsus[mentsuIndex] = Mentsu.ofId(mentsuId, agarihai.getId(), isTsumoho);
                    } else {
                        mentsus[mentsuIndex] = Mentsu.ofId(mentsuId);
                    }
                }
                Toitsu janto = Toitsu.ofId(jantoHaiId);
                score = Math.max(
                        calculateScoreForFixedMentsuAndMachi(mentsus, janto),
                        score);
            }
        }

        if (agarihai.getId() == jantoHaiId) {
            Mentsu[] mentsus = new Mentsu[mentsuIndexes.length];
            for (int mentsuIndex = 0; mentsuIndex < mentsuIndexes.length; mentsuIndex++) {
                int mentsuId = mentsuIndexes[mentsuIndex];
                mentsus[mentsuIndex] = Mentsu.ofId(mentsuId);
            }
            Toitsu janto = Toitsu.ofId(jantoHaiId, agarihai.getId());
            score = Math.max(
                    calculateScoreForFixedMentsuAndMachi(mentsus, janto),
                    score);
        }

        return score;
    }

    private int calculateScoreForFixedMentsuAndMachi(Mentsu[] mentsus, Toitsu janto) {
        boolean isPinfu = false;

        // Fan
        int fan = 0;

        // Richi
        if (doneRichi) {
            fan += 1;
        }

        // Menzenchintsumoho
        if (isTsumoho) {
            // TODO: Add menzen check when furo is implemented.
            fan += 1;
        }
        // Tanyao
        if (isTanyao(mentsus, janto)) {
            fan += 1;
        }
        // Pinfu
        if (isPinfu(mentsus, janto)) {
            fan += 1;
            isPinfu = true;
        }

        // Fu
        int fu = 20;

        if (isTsumoho) {
            if (!isPinfu) {
                fu += 2;
            }
        }

        for (Mentsu mentsu : mentsus) {
            if (mentsu.getMachiType() == MachiType.KANCHAN) {
                fu += 2;
            } else if (mentsu.getMachiType() == MachiType.PENCHAN) {
                fu += 2;
            }
        }
        if (janto.getMachiType() == MachiType.TANKI) {
            fu += 2;
        }

        for (Mentsu mentsu : mentsus) {
            if (mentsu.getMentsuType() == MentsuType.MINKO) {
                if (mentsu.isYaochuhai()) {
                    fu += 4;
                } else {
                    fu += 2;
                }
            } else if (mentsu.getMentsuType() == MentsuType.ANKO) {
                if (mentsu.isYaochuhai()) {
                    fu += 8;
                } else {
                    fu += 4;
                }
            }
        }

        if (janto.getHaiId() == bakaze.getId()) {
            fu += 2;
        }
        if (janto.getHaiId() == jikaze.getId()) {
            fu += 2;
        }
        if (31 <= janto.getHaiId() && janto.getHaiId() < 34) {
            fu += 2;
        }

        if (!isTsumoho) {
            // TODO: Add menzen check when furo is implemented.
            fu += 10;
        }

        fu = (fu + 9) / 10 * 10;

        return calculateScoreFromFanAndFu(fan, fu);
    }

    private int calculateScoreFromFanAndFu(int fan, int fu) {
        if (fan == 0) {
            return 0;
        }
        int score = fu * (1 << (fan + 2)) * (isOya ? 6 : 4);
        return (score + 99) / 100 * 100;
    }

    private boolean isTanyao(Mentsu[] mentsus, Toitsu janto) {
        for (Mentsu mentsu : mentsus) {
            if (mentsu.hasYaochuhai()) {
                return false;
            }
        }
        if (janto.isYaochuhai()) {
            return false;
        }
        return true;
    }

    private boolean isPinfu(Mentsu[] mentsus, Toitsu janto) {
        for (Mentsu mentsu : mentsus) {
            if (mentsu.getMentsuType() != MentsuType.SHUNTSU) {
                return false;
            }
        }

        if (janto.getHaiId() == bakaze.getId()) {
            return false;
        }
        if (janto.getHaiId() == jikaze.getId()) {
            return false;
        }
        if (31 <= janto.getHaiId() && janto.getHaiId() < 34) {
            return false;
        }

        MachiType machiType = getMachiType(mentsus, janto);
        return machiType == MachiType.RYANMEN;
    }

    private MachiType getMachiType(Mentsu[] mentsus, Toitsu janto) {
        for (Mentsu mentsu : mentsus) {
            if (mentsu.getMachiType() != null) {
                return mentsu.getMachiType();
            }
        }
        if (janto.getMachiType() != null) {
            return janto.getMachiType();
        }
        return null;
    }
}

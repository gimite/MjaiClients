package org.ymatsux.mjai.client;


public class Mentsu {

    private final int mentsuId;
    private final MentsuType mentsuType;
    private final MachiType machiType;

    private Mentsu(int mentsuId, MentsuType mentsuType) {
        this(mentsuId, mentsuType, null);
    }

    private Mentsu(int mentsuId, MentsuType mentsuType, MachiType machiType) {
        this.mentsuId = mentsuId;
        this.mentsuType = mentsuType;
        this.machiType = machiType;
    }

    public static Mentsu ofId(int mentsuId) {
        if (0 <= mentsuId && mentsuId < 21) {
            return new Mentsu(mentsuId, MentsuType.SHUNTSU, null);
        } else if (21 <= mentsuId && mentsuId < 55) {
            return new Mentsu(mentsuId, MentsuType.ANKO, null);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static Mentsu ofId(int mentsuId, int agarihaiId, boolean isTsumoho) {
        MachiType machiType = calculateMachiType(mentsuId, agarihaiId);
        if (0 <= mentsuId && mentsuId < 21) {
            return new Mentsu(mentsuId, MentsuType.SHUNTSU, machiType);
        } else if (21 <= mentsuId && mentsuId < 55) {
            if (isTsumoho) {
                return new Mentsu(mentsuId, MentsuType.ANKO, machiType);
            } else {
                return new Mentsu(mentsuId, MentsuType.MINKO, machiType);
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static MachiType calculateMachiType(int mentsuId, int agarihaiId) {
        if (21 <= mentsuId && mentsuId < 55) {
            return MachiType.SHANPON;
        } else if (0 <= mentsuId && mentsuId < 21) {
            int mentsuStartKazu = (mentsuId % 7) + 1;
            int agarihaiKazu = (agarihaiId % 9) + 1;
            if (agarihaiKazu == mentsuStartKazu + 1) {
                return MachiType.KANCHAN;
            } else {
                if ((mentsuStartKazu == 1 && agarihaiKazu == 3) ||
                        (mentsuStartKazu == 7 && agarihaiKazu == 7)) {
                    return MachiType.PENCHAN;
                } else {
                    return MachiType.RYANMEN;
                }
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    public MentsuType getMentsuType() {
        return mentsuType;
    }

    public MachiType getMachiType() {
        return machiType;
    }

    public boolean isYaochuhai() {
        return mentsuId == 21 || mentsuId == 29 ||
                mentsuId == 30 || mentsuId == 38 ||
                mentsuId == 39 || mentsuId == 47 ||
                (48 <= mentsuId && mentsuId < 55);
    }

    public boolean isSangenpai() {
        return 52 <= mentsuId && mentsuId < 55;
    }

    public int getMentsuId() {
        return mentsuId;
    }

    public boolean hasYaochuhai() {
        return mentsuId == 0 || mentsuId == 6 ||
                mentsuId == 7 || mentsuId == 13 ||
                mentsuId == 14 || mentsuId == 20 ||
                isYaochuhai();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        int[] haiIndexes = MentsuUtil.getMentsu(mentsuId);
        builder.append("[");
        for (int i = 0; i < 3; i++) {
            if (i > 0) {
                builder.append(",");
            }
            builder.append(Hai.ofId(haiIndexes[i]));
        }
        builder.append("]");
        builder.append(" " + mentsuType);
        if (machiType != null) {
            builder.append(" " + machiType);
        }
        builder.append("}");
        return builder.toString();
    }
}

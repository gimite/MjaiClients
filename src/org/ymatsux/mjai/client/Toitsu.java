package org.ymatsux.mjai.client;


public class Toitsu {

    private final int haiId;
    private final MachiType machiType;

    private Toitsu(int haiId, MachiType machiType) {
        this.haiId = haiId;
        this.machiType = machiType;
    }

    public static Toitsu ofId(int haiId) {
        return new Toitsu(haiId, null);
    }

    public static Toitsu ofId(int haiId, int agarihaiId) {
        return new Toitsu(haiId, MachiType.TANKI);
    }

    public int getHaiId() {
        return haiId;
    }

    public MachiType getMachiType() {
        return machiType;
    }

    public boolean isYaochuhai() {
        return haiId == 0 || haiId == 8 ||
                haiId == 9 || haiId == 17 ||
                haiId == 18 || haiId == 26 ||
                (27 <= haiId && haiId < 34);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append("[");
        builder.append(Hai.ofId(haiId));
        builder.append(",");
        builder.append(Hai.ofId(haiId));
        builder.append("]");
        if (machiType != null) {
            builder.append(" " + machiType);
        }
        builder.append("}");
        return builder.toString();
    }
}

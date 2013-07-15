package org.ymatsux.mjai.client;

public class HaiUtil {

    public static boolean isYaochuhai(int haiId) {
        return haiId == 0 || haiId == 8 ||
                haiId == 9 || haiId == 17 ||
                haiId == 18 || haiId == 26 ||
                (27 <= haiId && haiId < 34);
    }

    private HaiUtil() {
    }
}

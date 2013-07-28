package org.ymatsux.mjai.client;

public class MathUtil {

    public static double combinationAsDouble(int n, int r) {
        return factAsDouble(n) / (factAsDouble(r) * factAsDouble(n - r));
    }

    public static double factAsDouble(int n) {
        if (n == 0) {
            return 1.0;
        } else {
            return n * factAsDouble(n - 1);
        }
    }
}

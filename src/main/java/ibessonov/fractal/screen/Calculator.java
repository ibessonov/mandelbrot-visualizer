package ibessonov.fractal.screen;

import ibessonov.fractal.conf.Configuration;
import static java.lang.Math.*;

/**
 *
 * @author Ivan Bessonov
 */
public class Calculator {

    private static final int    SIZE = (int) Configuration.PIXELS_IN_UNIT;
    private static final int    ITERATIONS = 1 << 10;
    private static final int    GRADIENT = ITERATIONS >> 2;
    private static final double M_LN2 = Math.log(2);
    private static final double M_LN2_INV = 1.0 / M_LN2;

    public static int[] getPiece(long x, long y, int zoom, int[] result) {
        double z = 1.0 / (((long) SIZE) << zoom);
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                int color = getColorByPoint(
                        z * (x * SIZE + i),
                        z * (y * SIZE + j - 0.5));
                result[i * SIZE + j] = color;
            }
        }
        return result;
    }

    private static int getColorByPoint(double x, double y) {
        double rx = x - 0.25;
        double rC = 0.5 * (1 - cos(atan2(y, rx)));
        if ((rx * rx + y * y) < rC * rC) {
            return 0;
        }

        double cX = x,
               cY = y;
        for (int i = 0; i < ITERATIONS; i++) {
            if (x == 0 && y == 0) {
                break;
            }

            double xx = x * x;
            double yy = y * y;
            double r1 = xx + yy;

            double tx = xx - yy + cX;
            double ty = y * (x + x) + cY;

            double txx = tx * tx;
            double tyy = ty * ty;
            double r2 = txx + tyy;

            x = txx - tyy + cX;
            y = ty * (tx + tx) + cY;

            if (tx == 0 && ty == 0) {
                break;
            }

            if (r2 >= 4) {
                return r1 >= 4 ? getColor(i * 2, r1) : getColor(i * 2 + 1, r2);
            }
        }
        return 0;
    }

    private static int getColor(int i, double r) {
        double n = i + 1 - log(0.5 * log((double) r) * M_LN2_INV) * M_LN2_INV;
        double nh = 0.01 * n;
        double dh = nh - (int) nh;
        double it = ITERATIONS + 1 - n;
        double b = (it >= GRADIENT)
                  ? 1
                  : (it * it) / (GRADIENT * GRADIENT);
        return ((int) (dh * 0xFFFF) << 16) ^ ((int) (b * 0xFFFF) & 0xFFFF);
    }
}

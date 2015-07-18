package ibessonov.fractal.cache;

import ibessonov.fractal.conf.Configuration;
import java.awt.Image;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Ivan Bessonov
 */
public class PiecesCache {

    private static final class KeyDescriptor {
        public final long x, y;
        private final int hash;

        public KeyDescriptor(long x, long y) {
            this.x = x;
            this.y = y;
            this.hash = (int) (x ^ y ^ (x >>> 32) ^ (y >>> 32));
        }

        @Override
        @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
        public final boolean equals(Object obj) {
            final KeyDescriptor other = (KeyDescriptor) obj;
            return (x == other.x
                 && y == other.y);
        }

        @Override
        public final int hashCode() {
            return hash;
        }
    }

    private static final Map<KeyDescriptor, SoftReference<Image>>[] cache = new Map[Configuration.ZOOM_DEPTH + 1];

    static {
        for (int i = 0; i < cache.length; i++) {
            cache[i] = new ConcurrentHashMap<>();
        }
    }

    public static void store(int zoom, long x, long y, Image image) {
        KeyDescriptor descriptor = new KeyDescriptor(x, y);
        SoftReference<Image> sr = new SoftReference<>(image);
        cache[zoom].put(descriptor, sr);
    }

    public static Image get(int zoom, long x, long y) {
        SoftReference<Image> sr = cache[zoom].get(new KeyDescriptor(x, y));
        if (sr == null) {
            return null;
        }
        Image image = sr.get();
        return image;
    }
}

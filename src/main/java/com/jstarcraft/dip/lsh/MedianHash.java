package com.jstarcraft.dip.lsh;

import java.math.BigInteger;

import com.github.kilianB.ArrayUtil;
import com.jstarcraft.dip.color.ColorPixel;

/**
 * Calculate a hash value based on the median luminosity in an image.
 * 
 * <p>
 * Really good performance almost comparable to average hash. So far does a
 * better job matching images if watermarks are added but trades this off for a
 * little bit worse detection rating if handling rescaled images.
 * 
 * <p>
 * - Slower to compute
 * 
 * @author Kilian
 * @since 2.0.0
 */
public class MedianHash extends AverageHash {

    /**
     * @param bitResolution The bit resolution specifies the final length of the
     *                      generated hash. A higher resolution will increase
     *                      computation time and space requirement while being able
     *                      to track finer detail in the image. Be aware that a high
     *                      key is not always desired.
     *                      <p>
     * 
     *                      The median hash will produce a hash with at least the
     *                      number of bits defined by this argument. In turn this
     *                      also means that different bit resolutions may be mapped
     *                      to the same final key length.
     * 
     *                      <pre>
     *  64 = 8x8 = 65 bit key
     *  128 = 11.3 -&gt; 12 -&gt; 144 bit key
     *  256 = 16 x 16 = 256 bit key
     *                      </pre>
     */
    public MedianHash(int bitResolution) {
        super(bitResolution);
    }

    @Override
    protected BigInteger hash(ColorPixel pixel, HashBuilder hash) {
        int[] lum = pixel.getLuminanceVector();

        int[][] luminocity = pixel.getLuminanceMatrix();

        // Create hash
        return computeHash(hash, luminocity, ArrayUtil.median(lum));
    }

}

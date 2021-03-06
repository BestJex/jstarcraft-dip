package com.jstarcraft.dip.lsh;

import java.math.BigInteger;
import java.util.Objects;

import com.github.kilianB.ArrayUtil;
import com.jstarcraft.dip.color.ColorPixel;

/**
 * Calculate a hash value based on the average luminosity in an image.
 * 
 * @author Kilian
 * @since 1.0.0
 * @since 2.0.0 use luminosity instead of average pixel color
 */
public class AverageHash extends HashingAlgorithm {

    /**
     * @param bitResolution The bit resolution specifies the final length of the
     *                      generated hash. A higher resolution will increase
     *                      computation time and space requirement while being able
     *                      to track finer detail in the image. Be aware that a high
     *                      key is not always desired.
     *                      <p>
     * 
     *                      The average hash requires to re scale the base image
     *                      according to the required bit resolution. If the square
     *                      root of the bit resolution is not a natural number the
     *                      resolution will be rounded to the next whole number.
     *                      </p>
     * 
     *                      The average hash will produce a hash with at least the
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
    public AverageHash(int bitResolution) {
        super(bitResolution);

        // Allow for slightly non symmetry to get closer to the true bit resolution
        int dimension = (int) Math.round(Math.sqrt(bitResolution));

        // Lets allow for a +1 or -1 asymmetry and find the most fitting value
        int normalBound = (dimension * dimension);
        int higherBound = (dimension * (dimension + 1));

        this.height = dimension;
        this.width = dimension;
        if (normalBound < bitResolution || (normalBound - bitResolution) > (higherBound - bitResolution)) {
            this.width++;
        }
    }

    @Override
    protected BigInteger hash(ColorPixel pixel, HashBuilder hash) {
        int[][] luminance = pixel.getLuminanceMatrix();

        // Calculate the average color of the entire image
        double average = ArrayUtil.average(luminance);

        // Create hash
        return computeHash(hash, luminance, average);
    }

    protected BigInteger computeHash(HashBuilder hash, double[][] pixels, double compare) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (pixels[x][y] < compare) {
                    hash.prependZero();
                } else {
                    hash.prependOne();
                }
            }
        }
        return hash.toBigInteger();
    }

    protected BigInteger computeHash(HashBuilder hash, int[][] pixels, double compare) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (pixels[x][y] < compare) {
                    hash.prependZero();
                } else {
                    hash.prependOne();
                }
            }
        }
        return hash.toBigInteger();
    }

    @Override
    protected int precomputeAlgoId() {
        /*
         * String and int hashes stays consistent throughout different JVM invocations.
         * Algorithm changed between version 1.x.x and 2.x.x ensure algorithms are
         * flagged as incompatible. Dimension are what makes average hashes unique
         * therefore, even
         */
        return Objects.hash(getClass().getName(), height, width);
    }
}

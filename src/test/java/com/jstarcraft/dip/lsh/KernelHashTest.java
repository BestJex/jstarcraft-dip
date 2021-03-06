package com.jstarcraft.dip.lsh;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.jstarcraft.dip.lsh.KernelHash;
import com.jstarcraft.dip.lsh.HashingAlgorithm;
import com.jstarcraft.dip.lsh.kernel.Kernel;

/**
 * @author Kilian
 *
 */
class KernelHashTest {

    @Nested
    @DisplayName("Algorithm Id")
    class AlgorithmId {
        /**
         * The algorithms id shall stay consistent throughout different instances of the
         * jvm. While simple hashcodes do not guarantee this behavior hash codes created
         * from strings and integers are by contract consistent.
         */
        @Test
        @DisplayName("Consistent AlgorithmIds")
        public void consistency() {
            assertAll(() -> {
                assertEquals(1045402787, new KernelHash(14).algorithmId());
            }, () -> {
                assertEquals(1074955459, new KernelHash(25).algorithmId());
            });
        }

        @Test
        @DisplayName("Consistent AlgorithmIds v 2.0.0 collision")
        public void notVersionTwo() {
            assertAll(() -> {
                assertNotEquals(544576934, new KernelHash(14).algorithmId());
            }, () -> {
                assertNotEquals(545530246, new KernelHash(14).algorithmId());
            });
        }

        /**
         * Algorithm id isn't equal to the v2 hashes anymore
         */
        @Test
        @DisplayName("NotOldIds AlgorithmIds")
        public void consistencyOld() {
            assertAll(() -> {
                assertNotEquals(1264492988, new KernelHash(14).algorithmId());
            }, () -> {
                assertNotEquals(1264523740, new KernelHash(25).algorithmId());
            });
        }
    }

    /**
     * Adding a kernel produces a different hash
     */
    @Test
    public void addKernel() {
        HashingAlgorithm h0 = new KernelHash(32);
        HashingAlgorithm h1 = new KernelHash(32, Kernel.gaussianFilter(3, 3, 2));
        assertNotEquals(h0.algorithmId(), h1.algorithmId());
    }

    @Test
    public void addMultipleKernel() {
        HashingAlgorithm h0 = new KernelHash(32);
        HashingAlgorithm h1 = new KernelHash(32, Kernel.gaussianFilter(3, 3, 2));
        HashingAlgorithm h2 = new KernelHash(32, Kernel.gaussianFilter(3, 3, 2), Kernel.boxFilterNormalized(3, 3));
        assertNotEquals(h0.algorithmId(), h1.algorithmId());
        assertNotEquals(h0.algorithmId(), h2.algorithmId());
        assertNotEquals(h1.algorithmId(), h2.algorithmId());
    }

    // Base Hashing algorithm tests
    @Nested
    class AlgorithmBaseTests extends HashTestBase {

        @Override
        protected HashingAlgorithm getInstance(int bitResolution) {
            return new KernelHash(bitResolution);
        }

        @Override
        protected double differenceBallonHqHash() {
            return 77;
        }

        @Override
        protected double normDifferenceBallonHqHash() {
            return 77 / 132d;
        }
    }

    @Nested
    class AlgorithmBaseTestsWithFilter extends HashTestBase {
        @Override
        protected HashingAlgorithm getInstance(int bitResolution) {
            return new KernelHash(bitResolution, Kernel.gaussianFilter(3, 3, 2));
        }

        @Override
        protected double differenceBallonHqHash() {
            return 79;
        }

        @Override
        protected double normDifferenceBallonHqHash() {
            return 79 / 132d;
        }
    }

    @Nested
    class AlgorithmBaseTestsWithMultipleFilters extends HashTestBase {
        @Override
        protected HashingAlgorithm getInstance(int bitResolution) {
            return new KernelHash(bitResolution, Kernel.gaussianFilter(3, 3, 2), Kernel.gaussianFilter(5, 3, 2));
        }

        @Override
        protected double differenceBallonHqHash() {
            return 73;
        }

        @Override
        protected double normDifferenceBallonHqHash() {
            return 73 / 132d;
        }
    }

    @Test
    void testToString() {
        // Contains fields with kernels
        assertTrue(new KernelHash(32).toString().contains("filters="));
    }

}

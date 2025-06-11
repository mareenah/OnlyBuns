package com.example.onlybuns.performance;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class BloomFilterPerformanceTest {

    @Test
    void bloomFilterPerformanceTest() {
        int totalUsers = 10_000;
        BloomFilter<CharSequence> bloomFilter = BloomFilter.create(
                Funnels.stringFunnel(StandardCharsets.UTF_8),
                totalUsers,
                0.01 // 1% false positive rate
        );

        // Simulate adding many usernames to the filter
        for (int i = 0; i < totalUsers; i++) {
            bloomFilter.put("user" + i);
        }

        // Check false negatives (should be zero)
        for (int i = 0; i < totalUsers; i++) {
            assertTrue(bloomFilter.mightContain("user" + i), "Bloom filter missing: user" + i);
        }

        // Test for expected false positives
        int falsePositives = 0;
        int testRange = 1_000;

        for (int i = totalUsers; i < totalUsers + testRange; i++) {
            if (bloomFilter.mightContain("ghostUser" + i)) {
                falsePositives++;
            }
        }

        double rate = (falsePositives * 100.0) / testRange;

        System.out.println("False positives: " + falsePositives + "/" + testRange + " (~" + rate + "%)");

        // Optional: Assert upper bound if you want strict limits
        assertTrue(rate <= 2.0, "False positive rate too high");
    }
}

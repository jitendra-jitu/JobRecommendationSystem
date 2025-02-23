package com.techstart.jobportal.util;

import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class SimilarityCalculator {

    public double cosineSimilarity(Map<String, Double> vectorA, Map<String, Double> vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (String key : vectorA.keySet()) {
            if (vectorB.containsKey(key)) {
                dotProduct += vectorA.get(key) * vectorB.get(key);
            }
            normA += Math.pow(vectorA.get(key), 2);
        }

        for (String key : vectorB.keySet()) {
            normB += Math.pow(vectorB.get(key), 2);
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB) + 1e-10);
    }
}

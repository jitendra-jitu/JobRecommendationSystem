package com.techstart.jobportal.recommendation;

//package com.techstart.jobportal.recommendation;

import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class TFIDFVectorizer {

    public double computeCosineSimilarity(String text1, String text2) {
        // Tokenize
        List<String> tokens1 = tokenize(text1);
        List<String> tokens2 = tokenize(text2);

        // Compute TF-IDF vectors
        Map<String, Double> tfidf1 = computeTFIDF(tokens1);
        Map<String, Double> tfidf2 = computeTFIDF(tokens2);

        // Compute cosine similarity
        return cosineSimilarity(tfidf1, tfidf2);
    }

    private List<String> tokenize(String text) {
        return Arrays.stream(text.toLowerCase().split("\\W+"))
                .filter(token -> !token.isEmpty())
                .collect(Collectors.toList());
    }

    private Map<String, Double> computeTFIDF(List<String> tokens) {
        Map<String, Double> tfidf = new HashMap<>();
        int totalTokens = tokens.size();
        for (String token : tokens) {
            tfidf.put(token, (double) Collections.frequency(tokens, token) / totalTokens);
        }
        return tfidf;
    }

    private double cosineSimilarity(Map<String, Double> vec1, Map<String, Double> vec2) {
        Set<String> allWords = new HashSet<>(vec1.keySet());
        allWords.addAll(vec2.keySet());

        double dotProduct = 0.0, norm1 = 0.0, norm2 = 0.0;
        for (String word : allWords) {
            double v1 = vec1.getOrDefault(word, 0.0);
            double v2 = vec2.getOrDefault(word, 0.0);
            dotProduct += v1 * v2;
            norm1 += v1 * v1;
            norm2 += v2 * v2;
        }
        return norm1 == 0.0 || norm2 == 0.0 ? 0.0 : dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}


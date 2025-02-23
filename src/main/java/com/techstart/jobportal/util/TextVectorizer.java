package com.techstart.jobportal.util;

import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class TextVectorizer {

    public Map<String, Double> vectorize(String text) {
        String[] tokens = text.toLowerCase().split("\\W+");
        Map<String, Double> vector = new HashMap<>();
        for (String token : tokens) {
            vector.put(token, vector.getOrDefault(token, 0.0) + 1.0);
        }
        return vector;
    }
}

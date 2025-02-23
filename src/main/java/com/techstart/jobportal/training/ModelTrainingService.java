package com.techstart.jobportal.training;

import com.techstart.jobportal.model.UserInteraction;
import com.techstart.jobportal.repository.UserInteractionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModelTrainingService {

    @Autowired
    private UserInteractionRepository userInteractionRepository;

    @Autowired
    private RecommendationModel recommendationModel;

    @Async
    public void trainModel() {
        // Fetch all user interactions
        List<UserInteraction> interactions = userInteractionRepository.findAll();

        // Train the recommendation model
        recommendationModel.train(interactions);
    }
}
package com.techstart.jobportal.service;

import com.techstart.jobportal.dto.ResumeData;
import okhttp3.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

@Service
public class ResumeParsingService {

    @Value("${gemini.api.key}")
    private String GEMINI_API_KEY;

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";

    public ResumeData parseResume(File resumeFile) {
        try {
            validateFile(resumeFile);
            String extractedText = extractTextFromPDF(resumeFile);
            String prompt = createAnalysisPrompt(extractedText);
            String apiResponse = sendToGemini(prompt);
            return parseGeminiResponse(apiResponse);

        } catch (IOException e) {
            return errorResponse("PDF Error", "File processing failed: " + e.getMessage());
        } catch (JSONException e) {
            return errorResponse("API Error", "Invalid response format from AI service");
        } catch (Exception e) {
            return errorResponse("Processing Error", "Unexpected error: " + e.getMessage());
        }
    }

    private void validateFile(File file) throws IOException {
        if (!file.exists()) throw new IOException("File not found");
        if (file.length() == 0) throw new IOException("Empty file");
    }

    private String extractTextFromPDF(File file) throws IOException {
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            return stripper.getText(document);
        } catch (IOException e) {
            throw new IOException("Invalid PDF format: " + e.getMessage());
        }
    }

    private String createAnalysisPrompt(String resumeText) {
        return "Analyze this resume and return valid JSON with these fields: "
                + "fullName (string), email (string), skills (string array), "
                + "experience (array of {jobTitle: string, company: string, duration: string}), "
                + "education (array of {degree: string, institution: string, year: string}), "
                + "careerGoals (string). Resume text:\n" + resumeText;
    }

    private String sendToGemini(String prompt) throws IOException {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        JSONObject requestBody = new JSONObject()
                .put("contents", new JSONArray()
                        .put(new JSONObject()
                                .put("parts", new JSONArray()
                                        .put(new JSONObject()
                                                .put("text", prompt)
                                        )
                                )
                        )
                );

        Request request = new Request.Builder()
                .url(GEMINI_API_URL + "?key=" + GEMINI_API_KEY)
                .post(RequestBody.create(requestBody.toString(), JSON))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "";
                throw new IOException("API request failed: " + response.code() + " - " + errorBody);
            }
            return response.body().string();
        }
    }

    private ResumeData parseGeminiResponse(String responseJson) throws JSONException {
        JSONObject jsonResponse = new JSONObject(responseJson);
        JSONObject candidate = jsonResponse.getJSONArray("candidates")
                .getJSONObject(0);

        String contentText = candidate.getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text");

        // Handle potential markdown code blocks in response
        String cleanJson = contentText.replaceAll("```json", "").replaceAll("```", "").trim();
        JSONObject data = new JSONObject(cleanJson);

        return new ResumeData(
                data.optString("fullName", ""),
                data.optString("email", ""),
                parseStringArray(data.optJSONArray("skills")),
                parseExperience(data.optJSONArray("experience")),
                parseEducation(data.optJSONArray("education")),
                data.optString("careerGoals", "")
        );
    }

    private List<String> parseStringArray(JSONArray array) {
        if (array == null) return Collections.emptyList();
        List<String> result = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            result.add(array.getString(i));
        }
        return result;
    }

    private List<ResumeData.Experience> parseExperience(JSONArray array) {
        if (array == null) return Collections.emptyList();
        List<ResumeData.Experience> result = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject exp = array.getJSONObject(i);
            result.add(new ResumeData.Experience(
                    exp.optString("jobTitle", ""),
                    exp.optString("company", ""),
                    exp.optString("duration", "")
            ));
        }
        return result;
    }

    private List<ResumeData.Education> parseEducation(JSONArray array) {
        if (array == null) return Collections.emptyList();
        List<ResumeData.Education> result = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject edu = array.getJSONObject(i);
            result.add(new ResumeData.Education(
                    edu.optString("degree", ""),
                    edu.optString("institution", ""),
                    edu.optString("year", "")
            ));
        }
        return result;
    }

    private ResumeData errorResponse(String error, String message) {
        return new ResumeData("Error", error,
                Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), message);
    }
}
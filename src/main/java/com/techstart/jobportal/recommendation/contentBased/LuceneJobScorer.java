// LuceneJobScorer.java
package com.techstart.jobportal.recommendation.contentBased;

import com.techstart.jobportal.model.Job;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.RAMDirectory;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class LuceneJobScorer {
    public static Map<Job, Double> computeJobScoresWithLucene(Map<String, Double> userProfile, List<Job> jobs) {
        Map<Job, Double> jobScores = new HashMap<>();
        try (RAMDirectory directory = new RAMDirectory(); IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(new StandardAnalyzer()))) {
            for (Job job : jobs) {
                Document doc = new Document();
                doc.add(new StringField("jobId", String.valueOf(job.getId()), Field.Store.YES));
                doc.add(new TextField("content", getJobContent(job), Field.Store.NO));
                writer.addDocument(doc);
            }
            writer.commit();
            jobScores = searchLuceneIndex(directory, userProfile, jobs);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jobScores;
    }

    private static String getJobContent(Job job) {
        return String.join(" ", job.getTitle(), job.getDescription(), job.getRequiredSkills() == null ? "" : String.join(" ", job.getRequiredSkills()));
    }

    private static Map<Job, Double> searchLuceneIndex(RAMDirectory directory, Map<String, Double> userProfile, List<Job> jobs) throws IOException {
        Map<Job, Double> scores = new HashMap<>();
        try (DirectoryReader reader = DirectoryReader.open(directory)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
            for (Map.Entry<String, Double> entry : userProfile.entrySet()) {
                Query query = new BoostQuery(new TermQuery(new Term("content", entry.getKey())), entry.getValue().floatValue());
                queryBuilder.add(query, BooleanClause.Occur.SHOULD);
            }
            TopDocs results = searcher.search(queryBuilder.build(), jobs.size());
            for (ScoreDoc doc : results.scoreDocs) {
                Document document = searcher.doc(doc.doc);
                Job job = jobs.stream().filter(j -> String.valueOf(j.getId()).equals(document.get("jobId"))).findFirst().orElse(null);
                if (job != null) scores.put(job, (double) doc.score);
            }
        }
        return scores;
    }
}
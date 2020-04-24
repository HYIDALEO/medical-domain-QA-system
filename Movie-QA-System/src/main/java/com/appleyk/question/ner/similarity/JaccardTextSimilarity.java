package com.appleyk.question.ner.similarity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.appleyk.node.Sentence;



public class JaccardTextSimilarity extends AbstractTextSimilarity{
    @Override
    public List<SimilarResult> similarScore(Sentence sentence, List<Sentence> sentences) {
        List<SimilarResult> results = new ArrayList<>();
        for(Sentence sentence2 : sentences){
            double score = this.similarScore1(text2words(sentence.getText()), text2words(sentence2.getText()));
            if(results.size() < AbstractTextSimilarity.CANDIDATE_NUMBER){
                results.add(new SimilarResult(score, sentence2));
            }else{
                results.sort((o1, o2) -> Double.compare(o2.getScore(),o1.getScore()));
                SimilarResult last = results.get(AbstractTextSimilarity.CANDIDATE_NUMBER - 1);
                if(score > last.getScore()){
                    results.remove(last);
                    results.add(new SimilarResult(score, sentence2));
                }
            }
        }
        results.sort((o1, o2) -> Double.compare(o2.getScore(),o1.getScore()));
        return results;
    }
    
    private List<String> text2words(String text){
        List<String> words = new ArrayList<>();
        for (int i = 0; i < text.length(); i++) {
            String word = String.valueOf(text.charAt(i));
            words.add(word);
        }
        return words;
    }
    
    private double similarScore1(List<String> words1, List<String> words2){
        if (words1.isEmpty() && words2.isEmpty()) {
            return 1.0D;
        } else {
            HashSet<String> intersection = new HashSet<>();
            words1.forEach((word1) -> {
                if (words2.contains(word1)) {
                    intersection.add(word1);
                }
            });
            HashSet<String> union = new HashSet<>();
            words1.forEach((word) -> {
                if(!union.contains(word)){
                    union.add(word);
                }
            });
            words2.forEach((word) -> {
                if(!union.contains(word)){
                    union.add(word);
                }
            });
            return (double)intersection.size() / (double)union.size();
        }
    }
}

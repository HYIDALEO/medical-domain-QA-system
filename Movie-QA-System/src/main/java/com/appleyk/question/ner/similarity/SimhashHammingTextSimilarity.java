package com.appleyk.question.ner.similarity;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.appleyk.node.Sentence;



public class SimhashHammingTextSimilarity extends AbstractTextSimilarity{
	private final int HASH_BIT_COUNT = 256;
	
    @Override
    public List<SimilarResult> similarScore(Sentence sentence, List<Sentence> sentences) {
        List<SimilarResult> results = new ArrayList<>();
        for(Sentence sentence2 : sentences){
            double score = this.similarScore1(sentence.getText(), sentence2.getText());
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
    
    private Map<String, Float> text2words(String text){
        Map<String, Float> words = new HashMap<>();
        for (int i = 0; i < text.length(); i++) {
            String word = String.valueOf(text.charAt(i));
            if(!words.containsKey(word)){
                words.put(word, 0f);
            }
            words.put(word, words.get(word) + 1);
        }
        return words;
    }
    
    private BigInteger hash(String word) {
        if (word == null || word.length() == 0) {
            return new BigInteger("0");
        }
        char[] charArray = word.toCharArray();
        BigInteger x = BigInteger.valueOf(((long) charArray[0]) << 7);
        BigInteger m = new BigInteger("1000003");
        BigInteger mask = new BigInteger("2").pow(HASH_BIT_COUNT).subtract(new BigInteger("1"));
        long sum = 0;
        for (char c : charArray) {
            sum += c;
        }
        x = x.multiply(m).xor(BigInteger.valueOf(sum)).and(mask);
        x = x.xor(new BigInteger(String.valueOf(word.length())));
        if (x.equals(new BigInteger("-1"))) {
            x = new BigInteger("-2");
        }
        return x;
    }
    
    private String simHash(Map<String, Float> words){
        float[] hashBit = new float[this.HASH_BIT_COUNT];
        words.forEach((word, weight) ->{
            BigInteger hash = hash(word);
            for (int i = 0; i < this.HASH_BIT_COUNT; i++) {
                BigInteger bitMask = new BigInteger("1").shiftLeft(i);
                if (hash.and(bitMask).signum() != 0) {
                    hashBit[i] += weight;
                } else {
                    hashBit[i] -= weight;
                }
            }
        });
        StringBuilder fingerprint = new StringBuilder();
        for (int i = 0; i < this.HASH_BIT_COUNT; i++) {
            if (hashBit[i] >= 0) {
                fingerprint.append("1");
            }else{
                fingerprint.append("0");
            }
        }
        return fingerprint.toString();
    }
    
    private int hammingDistance(String simHash1, String simHash2) {
        if (simHash1.length() != simHash2.length()) {
            return -1;
        }
        int distance = 0;
        int len = simHash1.length();
        for (int i = 0; i < len; i++) {
            if (simHash1.charAt(i) != simHash2.charAt(i)) {
                distance++;
            }
        }
        return distance;
    }


    private double similarScore1(String text1, String text2) {
        if(text1 == null || text2 == null){
            return 0.0;
        }
        if(text1.equals("") && text2.equals("")){
            return 1.0;
        }
        if(text1.equals("") || text2.equals("")){
            return 0.0;
        }
        Map<String, Float> words1 = text2words(text1);
        Map<String, Float> words2 = text2words(text2);
        String simhash1 = simHash(words1);
        String simhash2 = simHash(words2);
        int hammingDistance = hammingDistance(simhash1, simhash2);
        if(hammingDistance == -1){
            return 0.0;
        }
        int maxDistance = simhash1.length();
        return (1 - hammingDistance / (double)maxDistance);
    }
}

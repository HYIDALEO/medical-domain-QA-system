package com.appleyk.question.ner.similarity;

import com.appleyk.node.Sentence;

public class SimilarResult {
    private double score;   //0.0-1.0 值越高说明相似度越高
    private Sentence sentence;

    public SimilarResult() {
        this.score = -1;
        this.sentence = null;
    }

    public SimilarResult(double score, Sentence sentence) {
        this.score = score;
        this.sentence = sentence;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Sentence getSentence() {
        return sentence;
    }

    public void setSentence(Sentence sentence) {
        this.sentence = sentence;
    }
}

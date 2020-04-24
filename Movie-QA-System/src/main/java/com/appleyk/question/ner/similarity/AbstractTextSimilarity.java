package com.appleyk.question.ner.similarity;

import java.util.List;

import com.appleyk.node.Sentence;



public abstract class AbstractTextSimilarity {
    public static final int CANDIDATE_NUMBER = 30;

    public abstract List<SimilarResult> similarScore(Sentence sentence, List<Sentence> sentences);

}

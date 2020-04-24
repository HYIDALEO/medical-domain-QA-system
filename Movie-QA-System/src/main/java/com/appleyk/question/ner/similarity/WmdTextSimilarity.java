package com.appleyk.question.ner.similarity;

import java.util.ArrayList;
import java.util.List;

import com.appleyk.InitElements.QAWordSegmenter;
import com.appleyk.InitElements.QAWordVectors;
import com.appleyk.node.Sentence;
import com.appleyk.question.ner.similarity.wmd.WordMovers;

public class WmdTextSimilarity  extends AbstractTextSimilarity{
    private WordMovers wordMovers;

    public WmdTextSimilarity(QAWordVectors wordVectors,
                             QAWordSegmenter segmenter) {
    	
        this.wordMovers = new WordMovers(wordVectors, segmenter);
        
    }
    
    @Override
    public List<SimilarResult> similarScore(Sentence sentence, List<Sentence> sentences){
    	List<SimilarResult> results = new ArrayList<>();
    	return results;
    }
}

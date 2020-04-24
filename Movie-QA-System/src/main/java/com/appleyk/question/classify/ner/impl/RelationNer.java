package com.appleyk.question.classify.ner.impl;

import java.util.List;

import com.appleyk.InitElements.QAWordSegmenter;
import com.appleyk.InitElements.QAWordVectors;
import com.appleyk.node.KbEntity;
import com.appleyk.node.RecognizedResult;
import com.appleyk.question.classify.ner.SimilarityNer;

public class RelationNer extends SimilarityNer{
    public RelationNer(QAWordVectors wordVectors, QAWordSegmenter segmenter) {
        super(0.9, wordVectors, segmenter);
    }

    @Override
    protected RecognizedResult handle(RecognizedResult recognizedResult) {
        recognizedResult.entity.name = null;
        return recognizedResult;
    }

	@Override
	protected RecognizedResult recognizeStringEne(String ene, List<KbEntity> entities) {
		// TODO Auto-generated method stub
		return null;
	}
}

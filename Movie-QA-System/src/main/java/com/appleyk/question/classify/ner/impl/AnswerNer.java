package com.appleyk.question.classify.ner.impl;

import java.util.ArrayList;
import java.util.List;

import com.appleyk.InitElements.QAWordSegmenter;
import com.appleyk.InitElements.QAWordVectors;
import com.appleyk.node.KbEntity;
import com.appleyk.node.RecognizedResult;
import com.appleyk.question.classify.ner.SimilarityNer;

public class AnswerNer extends SimilarityNer {

    public AnswerNer(QAWordVectors wordVectors, QAWordSegmenter segmenter) {
        super(0.9, wordVectors, segmenter);
        initEntitiesList();
    }
    
    
    
    public void initEntitiesList() {
        //药品，症状，疾病
        List<KbEntity> entities = new ArrayList<>();
  //      entities.add(new KbEntity("药物","药品"));
  //      entities.add(new KbEntity("药","药品"));
  //      entities.add(new KbEntity("症状","症状"));
  //      entities.add(new KbEntity("病","疾病"));
  //      entities.add(new KbEntity("疾病","疾病"));
        setEntities(entities);
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

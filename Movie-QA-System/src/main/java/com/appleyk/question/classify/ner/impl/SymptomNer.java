package com.appleyk.question.classify.ner.impl;

import java.util.ArrayList;
import java.util.List;

import com.appleyk.InitElements.Config;
import com.appleyk.InitElements.KnowledgeBase;
import com.appleyk.InitElements.QAWordSegmenter;
import com.appleyk.InitElements.QAWordVectors;
import com.appleyk.node.KbEntity;
import com.appleyk.node.KbResults;
import com.appleyk.node.RecognizedResult;
import com.appleyk.question.classify.ner.SimilarityNer;



public class SymptomNer extends SimilarityNer {
	
    public SymptomNer(QAWordVectors wordVectors, QAWordSegmenter segmenter) {
        super(0.5, wordVectors, segmenter);
        initEntities();
    }

    public final void initEntities() {
        List<KbEntity> sympthom = new ArrayList<>();
        String query = Config.KNOWLEDGE_BASE_PREFIX + "select ?x where { ?x rdf:type exampleo:症状 .}";
        KbResults results = KnowledgeBase.getInstance().executeQuery(query);
        while (results.hasNext()) {
            results.next().values().forEach(value -> sympthom.add(new KbEntity(value.substring(value.lastIndexOf("/") + 1), "症状")));
        }

        setEntities(sympthom);
    }

    @Override
    protected RecognizedResult handle(RecognizedResult recognizedResult) {
        return recognizedResult;
    }

	@Override
	protected RecognizedResult recognizeStringEne(String ene, List<KbEntity> entities) {
		// TODO Auto-generated method stub
		return null;
	}
}

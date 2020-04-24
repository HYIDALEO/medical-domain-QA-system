package com.appleyk.question.classify.ner.impl;

import java.util.ArrayList;
import java.util.List;

import com.appleyk.InitElements.Config;
import com.appleyk.InitElements.KnowledgeBase;
import com.appleyk.node.KbEntity;
import com.appleyk.node.KbResults;
import com.appleyk.node.RecognizedResult;
import com.appleyk.question.classify.ner.AccuracyNer;


public class EntityNer extends AccuracyNer{
    public EntityNer() {
        initEntitiesList();
    }
    public void initEntitiesList() {
        //药品，疾病
        List<KbEntity> entities = new ArrayList<>();
        String query = Config.KNOWLEDGE_BASE_PREFIX + "select ?x where { ?x rdf:type exampleo:药品.}";
        KbResults results = KnowledgeBase.getInstance().executeQuery(query);
        while(results.hasNext()){
            results.next().values().forEach(value -> entities.add(new KbEntity(value.substring(value.lastIndexOf("/") + 1), "药品")));
        }
        
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println(entities);
//        System.out.println("--------------------------------------------------------------------");
        query = Config.KNOWLEDGE_BASE_PREFIX + "select ?x where { ?x rdf:type exampleo:疾病.}";
        results = KnowledgeBase.getInstance().executeQuery(query);
        while(results.hasNext()){
            results.next().values().forEach(value -> entities.add(new KbEntity(value.substring(value.lastIndexOf("/") + 1), "疾病")));
        }
        setEntities(entities);
    }
    @Override
    protected RecognizedResult handle(RecognizedResult recognizedResult) {
        return recognizedResult;
    }
}

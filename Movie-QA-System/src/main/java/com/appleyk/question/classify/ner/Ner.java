package com.appleyk.question.classify.ner;

import java.util.ArrayList;
import java.util.List;

import com.appleyk.node.KbEntity;
import com.appleyk.node.Sentence;
import com.appleyk.node.RecognizedResult;;
public abstract class Ner {
    private double minScore;                        //期望的最低分数
    private List<KbEntity> entities = null;

    public Ner(double minScore) {
        this.minScore = minScore;
    }

    public void setEntities(List<KbEntity> entities) {
        this.entities = entities;
    }
    //SimilarityNer和AccuracyNer。recognize(sentence, entities);
    public RecognizedResult recognize(Sentence sentence){
        RecognizedResult result = recognize(sentence, entities);
        if(result != null && result.score >= this.minScore){
            return handle(result);
        }
        return null;
    }
    
    public RecognizedResult recognizeStringEne(String entityInQuestion) {
        RecognizedResult result = recognizeStringEne(entityInQuestion, entities);
        if(result != null && result.score >= this.minScore){
            return handle(result);
        }
        return null;
    }
    
    public String getOnt(String name) {
    	String result=null;
    	for(KbEntity kbEntity : entities) {
    		if(name.indexOf(kbEntity.name)!=-1) {
    			result= kbEntity.ontology;
    			
    		}
    	}
    	return result;
    }
    public List<KbEntity> findElt(String text){
    	List<KbEntity> result = new ArrayList();
    	for(KbEntity kbEntity : entities) {
    		if(text.indexOf(kbEntity.name)!=-1) {
    			result.add(kbEntity);
    		}
    	}
    	return result;
    }

    protected abstract RecognizedResult recognize(Sentence sentence, List<KbEntity> entities);

    protected abstract RecognizedResult recognizeStringEne(String ene, List<KbEntity> entities);
    
    protected abstract RecognizedResult handle(RecognizedResult recognizedResult);

    public double getMinScore() {
        return minScore;
    }
    
    public void showEle() {
    	System.out.println("minScore:"+this.minScore);
    	System.out.println("entities:");
    	for(int i=0;i<this.entities.size();i++) {
    		this.entities.get(i).showKbEntity();
    	}
    	System.out.println("end");
    	
    }
}

package com.appleyk.question.classify.ner;

import java.util.ArrayList;
import java.util.List;

import com.appleyk.node.KbEntity;
import com.appleyk.node.RecognizedResult;
import com.appleyk.node.Sentence;



public class Ners {
    public List<Ner> ners = new ArrayList<>();

    public Ners() {
    }

    public void addNer(Ner ner){
        this.ners.add(ner);
    }
    
    public void shoeNers() {
    	for( int i = 0 ; i < this.ners.size() ; i++) {
    		this.ners.get(i).showEle();
    	}
    }
    public String getOnt(String name) {
    	String result=null;
    	for(Ner recognizer : this.ners) {
    		result=recognizer.getOnt(name);
    	}
    	return result;
    }
    public List<KbEntity> findEnt(String text){
    	List<KbEntity> result = new ArrayList();
    	for(Ner recognizer : this.ners) {
    		result.addAll(recognizer.findElt(text));
    	}
    	return result;
    }
    public List<RecognizedResult> recognize(Sentence sentence){
        List<RecognizedResult> recognizedResults = new ArrayList<>();
        List<String> words = sentence.getWords();
        boolean[] flags = new boolean[words.size()];
        int count = 0;
        while(count < words.size()){
            List<String> temp = new ArrayList<>();
            //如下的识别最多只能找到两个实体。flag为真代表这个对应的词是已经发现的实体
            for (int i = 0; i < words.size(); i++) {
                if(!flags[i]){
                	//如果flag不存在
                    temp.add(words.get(i));
                }else{
                	//如果flag==true
                    if(i != 0 && !flags[i - 1]){
                    	//如果前一个flag不存在
                        break;
                    }
                }
            }
            
            if(!temp.isEmpty()){
                StringBuilder sb = new StringBuilder();
                temp.forEach(sb::append);
                Sentence newSentence = new Sentence(sb.toString(), temp);
                //下面注释这两句输出很多
           //     System.out.println("Ners   recognize:sb.toString()");
           //     System.out.println(sb.toString());
                
                //因为每一次匹配对应一个实体的搜索，所以是words.size次循环
                RecognizedResult result = recognize1(newSentence);
                
                
                
                
                
                if(result != null){
                	System.out.println("Ners   recognize:result");
                    result.showRR();
                    for (int i = 0; i < words.size(); i++) {
                        //很可能有问题(原注释)
                        if(result.sentence.getWords().contains(words.get(i))){
                        	
                            flags[i] = true;
                        }
                    }
                    //识别出的实体
                    recognizedResults.add(result);
                }
            }
            count++;
        }
        sentence.isMatch = flags;
        return recognizedResults;
    }
    
    public void setEntits(List<KbEntity> entitys){
        this.ners.forEach(ner -> ner.setEntities(entitys));
    }
    
    
    public List<RecognizedResult> recognizeList(List<String> questionCacheSubsi){
    	List<RecognizedResult> resultList = new ArrayList<RecognizedResult>();
    	RecognizedResult finalResult = new RecognizedResult();
        finalResult.score = -1;
        for(String entityInQuestion : questionCacheSubsi) {
	        for(Ner recognizer : this.ners) {
	        	//recognizer对应的实体和sentence进行操作，找到sentence中的实体，此处this。ners存储的是实体
	        	//对于每一个实体，进行一次匹配，找到一个实体就返回
	        	RecognizedResult result = recognizer.recognizeStringEne(entityInQuestion);
	            if(result != null && result.score > finalResult.score){
	                finalResult = result;
	                if(result.score == 1.0){
	                	resultList.add(finalResult);
	                    break;
	                }
	            }
	        }
        }
    	return resultList;
    			
    }
    
    private RecognizedResult recognize1(Sentence sentence){
    	RecognizedResult finalResult = new RecognizedResult();
        finalResult.score = -1;
        for(Ner recognizer : this.ners){
        	//recognizer对应的实体和sentence进行操作，找到sentence中的实体，此处this。ners存储的是实体
        	//对于每一个实体，进行一次匹配，找到一个实体就返回
        	RecognizedResult result = recognizer.recognize(sentence);
            if(result != null && result.score > finalResult.score){
                finalResult = result;
                if(result.score == 1.0){
                    break;
                }
            }
        }
        if(finalResult.score == -1){
            return null;
        }
        return finalResult;
    }
}

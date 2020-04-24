package com.appleyk.node;



public class RecognizedResult {

    public double score;                //识别的分数
    public KbEntity entity;
    public Sentence sentence;
    
    public void showRR() {
    	System.out.println("score:"+this.score);
    	System.out.println("sentence.text:"+this.sentence.text);
    	System.out.println("sentence.words:");
    	for(String ele : this.sentence.words) {
    		System.out.print(ele+" ,");
    	}
    	System.out.println("");
    	System.out.println("entity:"+this.entity.name);
    }

}

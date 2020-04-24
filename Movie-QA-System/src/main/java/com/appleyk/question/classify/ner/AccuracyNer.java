package com.appleyk.question.classify.ner;

import java.util.ArrayList;
import java.util.List;

import com.appleyk.node.KbEntity;
import com.appleyk.node.Sentence;
import com.appleyk.node.RecognizedResult;

public abstract class AccuracyNer extends Ner {
    public AccuracyNer() {
        super(1.0);
    }
    
    //直接在句子中搜寻实体，如果找到了分数标1，找到一个实体就生成结果并返回。
    @Override
    public RecognizedResult recognize(Sentence sentence, List<KbEntity> entities) {
        for(KbEntity kbEntity : entities){
            for(String word : sentence.getWords()){
                if(word.equals(kbEntity.name)){
                    RecognizedResult result = new RecognizedResult();
                    result.score = 1.0;
                    result.sentence = new Sentence();
                    result.sentence.text = word;
                    result.sentence.words = new ArrayList<>();
                    result.sentence.words.add(word);
                    result.entity = kbEntity;
                    return result;
                }
            }
        }
        return null;
    }
    @Override
    public RecognizedResult recognizeStringEne(String ene, List<KbEntity> entities) {
        for(KbEntity kbEntity : entities){
            
                if(ene.equals(kbEntity.name)){
                    RecognizedResult result = new RecognizedResult();
                    result.score = 1.0;
/*                    result.sentence = new Sentence();
                    result.sentence.text = ene;
                    result.sentence.words = new ArrayList<>();
                    result.sentence.words.add(ene);
*/                    result.entity = kbEntity;
                    return result;
                }
            
        }
    	return null;
    }
}

package com.appleyk.question.classify.ner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.appleyk.InitElements.QAWordSegmenter;
import com.appleyk.InitElements.QAWordVectors;
import com.appleyk.node.KbEntity;
import com.appleyk.node.Sentence;
import com.appleyk.question.ner.similarity.AbstractTextSimilarity;
import com.appleyk.question.ner.similarity.JaccardTextSimilarity;
import com.appleyk.question.ner.similarity.SimhashHammingTextSimilarity;
import com.appleyk.question.ner.similarity.SimilarResult;
import com.appleyk.question.ner.similarity.WmdTextSimilarity;
import com.appleyk.node.RecognizedResult;

//unfinished

public abstract class SimilarityNer extends Ner{
    private SimhashHammingTextSimilarity simhashHammingTextSimilarity = new SimhashHammingTextSimilarity();
    private WmdTextSimilarity wmdTextSimilarity;
    private JaccardTextSimilarity jaccardTextSimilarity = new JaccardTextSimilarity();
    private final double WMD_FACTOR = 0.5;
    private final double HANMING_FACTOR = 1;
    private final double JACCARD_FACTOR = 0.5;

    public SimilarityNer(double minScore, QAWordVectors wordVectors,
            QAWordSegmenter segmenter) {
        super(minScore);
        this.wmdTextSimilarity = new WmdTextSimilarity(wordVectors, segmenter);
    }

    @Override
    public RecognizedResult recognize(Sentence sentence, List<KbEntity> entities) {
        RecognizedResult finalResult = new RecognizedResult();
        finalResult.score = -1;
        //三维数组
        List<List<List<String>>> sentencess = new ArrayList<>();
        //句子分词后的每一个词对应一个二维数组
        for (int i = 0; i < sentence.getWords().size(); i++) {
            sentencess.add(new ArrayList<>());
        }
        //二维数组是(句子分词结果的长度-i)*(i到j)
        
        
        //最低维是从i到j的所有词，构成一维
        //上一维是(sentence.getWords().size()-i+1)*(i到j)
        //最高维是0到sentence.getWords().size()
        //也就是说，每一个二维都表示，少了前i个词后，在i+1到末尾的各个缝隙进行分割，分割点为j
        //对于从i到j，再进行分割，获得一个句子中的所有有序子集，也就是每一个一维都表示，少了后面总长度减去j个词
        //这就是sentencess的内容
        for (int i = 0; i <= sentence.getWords().size(); i++) {
            for (int j = i + 1; j <= sentence.getWords().size(); j++) {
                List<String> tempSentence = new ArrayList<>();
                //从i到j的所有词，构成一维
                for (int k = i; k < j; k++) {
                    tempSentence.add(sentence.getWords().get(k));
                }
                sentencess.get(sentence.getWords().size() - tempSentence.size()).add(tempSentence);
            }
        }
        
        for (List<List<String>> sentences : sentencess) {
        	
            if (finalResult.score == -1) {//如果没找到
                for (List<String> tempSentence : sentences) {
                    Sentence st = new Sentence(tempSentence);
                    st.fill();
                    //计算st与，各种关系的匹配程度
                    RecognizedResult result = recognize2(st, entities);
                    if (result != null && result.score >= finalResult.score && result.score > getMinScore()) {
                        finalResult = result;
                        finalResult.sentence = st;
                    }
                }
            } else {
            	//如果找到了一些结果
                boolean isWorthContinue = false;
                for (List<String> tempSentence : sentences) {
                    Sentence st = new Sentence(tempSentence);
                    st.fill();
                    RecognizedResult result = recognize2(st, entities);
                    //找到的并不一定最合适
                    if (result != null && result.score > getMinScore()) {
                        if (result.entity.name.equals(finalResult.entity.name)
                                && result.entity.ontology.equals(finalResult.entity.ontology)
                                && result.score >= finalResult.score) {
                            finalResult = result;
                            finalResult.sentence = st;
                            isWorthContinue = true;
                        }
                    }
                }
                if (!isWorthContinue) {
                    break;
                }
            }
        }
        if (finalResult.score != -1) {
            return finalResult;
        }
        return null;
    }
    
    private RecognizedResult recognize2(Sentence sentence, List<KbEntity> entities) {
        List<Sentence> texts = new ArrayList<>();
        Map<String, KbEntity> entityMap = new HashMap<>();
        
        entities.forEach(entity -> {
            texts.add(new Sentence(entity.name));
            entityMap.put(entity.name, new KbEntity(entity.name, entity.ontology));
        });
        List<SimilarResult> results1 = this.simhashHammingTextSimilarity.similarScore(sentence, texts).
                stream().filter(result -> result.getScore() > getMinScore()).collect(Collectors.toList());
        if (results1.isEmpty()) {
            return null;
        }
        List<SimilarResult> results2 = this.wmdTextSimilarity.similarScore(sentence, texts).
                stream().filter(result -> result.getScore() > getMinScore()).collect(Collectors.toList());
        if (results2.isEmpty()) {
            return null;
        }
        List<SimilarResult> results3 = this.jaccardTextSimilarity.similarScore(sentence, texts).
                stream().filter(result -> result.getScore() > getMinScore()).collect(Collectors.toList());
        if (results3.isEmpty()) {
            return null;
        }

        Map<String, SimilarResult> resultsMap1 = new HashMap<>();
        Map<String, SimilarResult> resultsMap2 = new HashMap<>();
        Map<String, SimilarResult> resultsMap3 = new HashMap<>();
        System.out.println("SimilarityNer.recognize2() simhashHammingTextSimilarity,result1");
        for(SimilarResult ele : results1) {
        	System.out.println("text:");
        	System.out.println(ele.getSentence().getText());
        	System.out.println("score:");
        	System.out.println(ele.getScore());
        }
        results1.forEach(similarResult -> resultsMap1.put(similarResult.getSentence().getText(), similarResult));
        results2.forEach(similarResult -> resultsMap2.put(similarResult.getSentence().getText(), similarResult));
        results3.forEach(similarResult -> resultsMap3.put(similarResult.getSentence().getText(), similarResult));

        RecognizedResult result = new RecognizedResult();

        for (int i = 0; i < AbstractTextSimilarity.CANDIDATE_NUMBER && i < results1.size(); i++) {
            SimilarResult similarResult1 = results1.get(i);
            SimilarResult similarResult2 = resultsMap2.get(similarResult1.getSentence().getText());
            SimilarResult similarResult3 = resultsMap3.get(similarResult1.getSentence().getText());
            if (similarResult2 != null && similarResult3 != null) {
                double score = this.HANMING_FACTOR * similarResult1.getScore()
                        + this.WMD_FACTOR * similarResult2.getScore()
                        + this.JACCARD_FACTOR * similarResult3.getScore();
                if (score > result.score) {
                    result.score = score;
                    result.entity = entityMap.get(similarResult1.getSentence().getText());
                }
            }
        }
        for (int i = 0; i < AbstractTextSimilarity.CANDIDATE_NUMBER && i < results2.size(); i++) {
            SimilarResult similarResult2 = results2.get(i);
            SimilarResult similarResult1 = resultsMap1.get(similarResult2.getSentence().getText());
            SimilarResult similarResult3 = resultsMap3.get(similarResult2.getSentence().getText());
            if (similarResult1 != null && similarResult3 != null) {
                double score = this.HANMING_FACTOR * similarResult1.getScore()
                        + this.WMD_FACTOR * similarResult2.getScore()
                        + this.JACCARD_FACTOR * similarResult3.getScore();
                if (score > result.score) {
                    result.score = score;
                    result.entity = entityMap.get(similarResult1.getSentence().getText());
                }
            }
        }
        for (int i = 0; i < AbstractTextSimilarity.CANDIDATE_NUMBER && i < results3.size(); i++) {
            SimilarResult similarResult3 = results3.get(i);
            SimilarResult similarResult1 = resultsMap1.get(similarResult3.getSentence().getText());
            SimilarResult similarResult2 = resultsMap2.get(similarResult3.getSentence().getText());
            if (similarResult2 != null && similarResult1 != null) {
                double score = this.HANMING_FACTOR * similarResult1.getScore()
                        + this.WMD_FACTOR * similarResult2.getScore()
                        + this.JACCARD_FACTOR * similarResult3.getScore();
                if (score > result.score) {
                    result.score = score;
                    result.entity = entityMap.get(similarResult1.getSentence().getText());
                }
            }
        }

        if (result.score != -1) {
            return result;
        }
        return null;
    }

}

package com.appleyk.InitElements;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.Word;

import com.appleyk.InitElements.QAWordVectors;
//unfinished
public class QAWordSegmenter {
    private final QAWordVectors wordVectors;
    private static final Logger LOGGER = Logger.getLogger(QAWordSegmenter.class.getName());

    private Map<String, List<String>> segmentCache = new HashMap<>();
    private Map<String, Long> timeCache = new HashMap<>();
    
    private static QAWordSegmenter instance = new QAWordSegmenter(QAWordVectors.getInstance());

    public static QAWordSegmenter getInstance() {
        return instance;
    }
    public void loadCache(String filepath) {
        LOGGER.log(Level.INFO, "file path = {0}", filepath);
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath), "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                String sentence = line.split(":")[0];
                String[] words = line.split(":")[1].split(" ");
                //Arrays.asList,将数组转化为list
                this.segmentCache.put(sentence, Arrays.asList(words));
                updateCacheTime(sentence);
            }
            LOGGER.log(Level.INFO, "共加载{0}个分词缓冲", this.segmentCache.size());
            br.close();
    //        System.out.println("-----------------------------------------------------------------");
    //        System.out.println(this.segmentCache);
    //        System.out.println("-----------------------------------------------------------------");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public QAWordSegmenter(QAWordVectors wordVectors) {
        this.wordVectors = wordVectors;
    }
    
    private void updateCacheTime(String sentence) {
        this.timeCache.put(sentence, System.currentTimeMillis());
    }
    public List<String> segment(String sentence, boolean noStopwords, boolean usingWord2Vec) {
        return seg(sentence, noStopwords, usingWord2Vec);
    }
    private List<String> seg(String sentence, boolean noStopwords, boolean usingWord2Vec) {
        if (this.segmentCache.containsKey(sentence)) {
            this.updateCacheTime(sentence);
            return this.segmentCache.get(sentence);
        }

        List<Word> words = noStopwords ? WordSegmenter.seg(sentence) : WordSegmenter.segWithStopWords(sentence);
        List<String> tokens = new ArrayList<>();
        //首先判断每个token是否都有词向量值，如果没有则将token分割
        //当token长度为1时，必定存在词向量
        //当token长度为2时，分割成两个长度为1的token
        //当token长度为3时，两次切割判断，一次在12中间，一次在23中间切割
        //当token长度为4时，中间切割
        //当token长度大于5时，全部切割成长度为1的token
        //以上切割结果若失败了，则全部切割为长度为1的token
        
        
        //这里问句词序是对的
        System.out.println("QAWordSegmenter seg:words");
        for (Word word : words) {
        	System.out.println(word.getText());
        }
        System.out.println("End QAWordSegmenter seg:words");
        for (Word word : words) {
            if (!usingWord2Vec || this.wordVectors.hasWord(word.getText())) {
                tokens.add(word.getText());
            } else {
                tokens.addAll(segment1(word.getText()));
            }
        }
        this.updateCacheTime(sentence);
        this.segmentCache.put(sentence, tokens);
        return tokens;
    }
    private List<String> segment1(String token) {
        List<String> tokens = new ArrayList<>();
        switch (token.length()) {
            case 2:
                tokens.add(token.substring(0, 1));
                tokens.add(token.substring(1, 2));
                break;
            case 3:
                //12切一刀
                String token1 = token.substring(0, 1);
                String token23 = token.substring(1, 3);
                if (this.wordVectors.hasWord(token23)) {
                    tokens.add(token1);
                    tokens.add(token23);
                } else {
                    //23切一刀
                    String token12 = token.substring(0, 2);
                    String token3 = token.substring(2, 3);
                    
                    if (this.wordVectors.hasWord(token12)) {
                        tokens.add(token12);
                        tokens.add(token3);
                    } else {
                        tokens.addAll(segment1(token12));
                        tokens.add(token3);
                    }
                }
                break;
            case 4:
                //中间切一刀
                String token12 = token.substring(0, 2);
                String token34 = token.substring(2, 4);
                if (this.wordVectors.hasWord(token12) && this.wordVectors.hasWord(token34)) {
                    tokens.add(token12);
                    tokens.add(token34);
                } else {
                    token23 = token.substring(1, 3);
                    if (this.wordVectors.hasWord(token23)) {
                        tokens.add(token.substring(0, 1));
                        tokens.add(token23);
                        tokens.add(token.substring(3, 4));
                    } else {
                        tokens.add(token.substring(0, 1));
                        tokens.add(token.substring(1, 2));
                        tokens.add(token.substring(2, 3));
                        tokens.add(token.substring(3, 4));
                    }
                }
                break;
            default:
                for (int i = 0; i < token.length(); i++) {
                    tokens.add(token.substring(i, i + 1));
                }
        }
        return tokens;
    }
}

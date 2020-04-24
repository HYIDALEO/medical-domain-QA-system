package com.appleyk.InitElements;

import com.appleyk.InitElements.QAWordVector;
import com.appleyk.Util.BaiDuUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;



public class QAWordVectors {
	private Map<String, QAWordVector> cache = new HashMap<>();
    private static final Logger LOGGER = Logger.getLogger(QAWordVectors.class.getName());
    private static QAWordVectors instance = new QAWordVectors();

    public static QAWordVectors getInstance() {
        return instance;
    }
    
    public QAWordVector getQAWordVector(String word) {
    	return this.cache.get(word);
    }
    
    public void loadCache(String filepath) {
    	LOGGER.log(Level.INFO, "file path = {0}", filepath);
        BufferedReader br;
        try {
        	br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath), "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                String word = line.split(":")[0];
             //   System.out.print("word:"+word);
           //     System.out.print("[");
                QAWordVector vector = null;
                if (line.split(":").length != 1) {
                    vector = new QAWordVector();
                    String[] values = line.split(":")[1].split(" ");
                    vector.value = new double[values.length];
                    for (int i = 0; i < values.length; i++) {
                        double d = Double.parseDouble(values[i]);
                        vector.value[i] = d;
         //               System.out.print(d+",");
                    }
                }
       //         System.out.println("]");
                this.cache.put(word, vector);
            }
        	br.close();
            LOGGER.log(Level.INFO, "共载入{0}个词向量", this.cache.size());
//            System.out.println("-----------------------------------------------------------------");
//            System.out.println("word"+""+this.cache);
//            System.out.println("-----------------------------------------------------------------");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public boolean hasWord(String word) {
        return getWordVector(word) != null;
    }
    public QAWordVector getWordVector(String word) {
        if (this.cache.containsKey(word)) {
            return this.cache.get(word);
        }
        LOGGER.log(Level.INFO, "未命中缓存 word = {0}", word);
        QAWordVector vector = null;
        String json = BaiDuUtils.word2vec(word);
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(json).getAsJsonObject();
        if (jsonObject.get("vec") != null) {
            vector = new QAWordVector();
            JsonArray vec = jsonObject.get("vec").getAsJsonArray();
            int size = vec.size();
            vector.value = new double[size];
            for (int i = 0; i < size; i++) {
                vector.value[i] = vec.get(i).getAsDouble();
            }
        }
        this.cache.put(word, vector);
        return vector;
    }
}

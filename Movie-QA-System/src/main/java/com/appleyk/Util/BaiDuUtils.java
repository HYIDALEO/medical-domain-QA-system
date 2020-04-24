package com.appleyk.Util;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.appleyk.InitElements.QAWordVector;
import com.baidu.aip.nlp.AipNlp;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class BaiDuUtils {
    private static final String APP_ID = "10751301";
    private static final String API_KEY = "o5Q8A2pXdl96GK7UA6vrcvNH";
    private static final String SECRET_KEY = "pkCmB4eVqIBRDyj4zhy2HkiA0UaF7piE";
    private static final AipNlp CLIENT;
    private static final Logger LOGGER = Logger.getLogger(BaiDuUtils.class.getName());

    static {
        CLIENT = new AipNlp(APP_ID, API_KEY, SECRET_KEY);
    }

    private static String toGBK(String string) {
        try {
            return new String(string.getBytes("GBK"));
        } catch (UnsupportedEncodingException e) {
        }
        return string;
    }

    public static String dependencyParse(String sentence) {
        LOGGER.log(Level.INFO, "input = {0}", sentence);
        HashMap<String, Object> options = new HashMap<>();
        options.put("mode", 1);

        // 依存句法分析
        JSONObject res = CLIENT.depParser(sentence, options);
        LOGGER.log(Level.INFO, "output = {0}", res.toString());
        return res.toString();
    }

    public static String word2vec(String word) {
        LOGGER.log(Level.INFO, "word = {0}", word);
        // 传入可选参数调用接口
        HashMap<String, Object> options = new HashMap<>();
        // 依存句法分析
        JSONObject res = CLIENT.wordEmbedding((word), options);
        LOGGER.log(Level.INFO, "output = {0}", res.toString());
        return res.toString(2);
    }

    public static String ner(String sentence) {
        LOGGER.log(Level.INFO, "input = {0}", sentence);
        // 传入可选参数调用接口
        HashMap<String, Object> options = new HashMap<>();

        // 词法分析（定制版）
        JSONObject res = CLIENT.lexerCustom(sentence, options);
        LOGGER.log(Level.INFO, "output = {0}", res.toString());
        return res.toString();
    }

    public static void main(String[] args) {
        QAWordVector vector = new QAWordVector();
        String json = BaiDuUtils.word2vec("片");
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(json).getAsJsonObject();
        StringBuilder sb = new StringBuilder();
        if (jsonObject.get("vec") != null) {
            vector = new QAWordVector();
            JsonArray vec = jsonObject.get("vec").getAsJsonArray();
            int size = vec.size();
            for (int i = 0; i < size; i++) {
                sb.append(vec.get(i).getAsDouble());
                sb.append(",");
                if (i == 300 || i == 600) {
                    System.out.println(sb.toString());
                    sb = new StringBuilder();
                }
            }
        }
        System.out.println(sb.toString());

    }
}

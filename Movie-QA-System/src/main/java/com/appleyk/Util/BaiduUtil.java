package com.appleyk.Util;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.baidu.aip.nlp.AipNlp;

public class BaiduUtil {
    private static final String APP_ID = "10751301";
    private static final String API_KEY = "o5Q8A2pXdl96GK7UA6vrcvNH";
    private static final String SECRET_KEY = "pkCmB4eVqIBRDyj4zhy2HkiA0UaF7piE";
    private static final AipNlp CLIENT;
    private static final Logger LOGGER = Logger.getLogger(BaiDuUtils.class.getName());
    
    static {
        CLIENT = new AipNlp(APP_ID, API_KEY, SECRET_KEY);
    }
    public static String dependencyParse(String sentence) {
 //       LOGGER.log(Level.INFO, "input = {0}", sentence);
        HashMap<String, Object> options = new HashMap<>();
        options.put("mode", 1);

        // 依存句法分析 词语的句法结构信息（如“主谓”、“动宾”、“定中”等结构关系），并用树状结构来表示整句的结构（如“主谓宾”、“定状补”等）
        JSONObject res = CLIENT.depParser(sentence, options);
  //      LOGGER.log(Level.INFO, "output = {0}", res.toString());
        System.out.println(res.toString());
        return res.toString();
    }
}

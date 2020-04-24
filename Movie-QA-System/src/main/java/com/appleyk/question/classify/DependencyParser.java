package com.appleyk.question.classify;

import com.appleyk.Util.BaiduUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DependencyParser {
    private static DependencyParser instance = new DependencyParser();

    public static SyntaxTree parseSentece(String sentence) {
        return instance.parse(sentence);
    }
    
    public static SyntaxTree parseTree(String sentence) {
        return instance.parse(sentence);
    }
    
    public static SyntaxTree parseTreeDetail(String question) {
    	SyntaxTree tree = new SyntaxTree(question);
    	JsonParser parser = new JsonParser();
    	
        //id 	int 	词的ID
        //postag 	string 	词性，请参照下方词性（postag)取值范围
        //head 	int 	词的父节点ID
        //deprel 	string 	词与父节点的依存关系，请参照下方依存关系标识
        //词性 	含义 	词性 	含义 	词性 	含义 	词性 	含义
        //Ag 	形语素 	g 	语素 	ns 	地名 	u 	助词
        //a 	形容词 	h 	前接成分 	nt 	机构团体 	vg 	动语素
        //ad 	副形词 	i 	成语 	nz 	其他专名 	v 	动词
        //an 	名形词 	j 	简称略语 	o 	拟声词 	vd 	副动词
        //b 	区别词 	k 	后接成分 	p 	介词 	vn 	名动词
        //c 	连词 	 l 	习用语 	q 	量词 	w 	标点符号
        //dg 	副语素 	m 	数词 	r 	代词 	x 	非语素字
        //d 	副词 	Ng 	名语素 	s 	处所词 	y 	语气词
        //e 	叹词 	n 	名词 	tg 	时语素 	z 	状态词
        //f 	方位词 	nr 	人名 	t 	时间词 	un 	未知词
        
        //{"log_id":8214424394686623745,"text":"感冒吃什么药<br\/>",
        //"items":
        //[{"head":2,"deprel":"CS","postag":"v","id":1,"word":"感冒"},
        //{"head":0,"deprel":"HED","postag":"v","id":2,"word":"吃"},
        //{"head":4,"deprel":"ATT","postag":"r","id":3,"word":"什么"},
        //{"head":2,"deprel":"VOB","postag":"n","id":4,"word":"药"},
        //{"head":6,"deprel":"WP","postag":"w","id":5,"word":"<"},
        //{"head":2,"deprel":"IC","postag":"n","id":6,"word":"br"},
        //{"head":6,"deprel":"WP","postag":"w","id":7,"word":"\/"},
        //{"head":6,"deprel":"WP","postag":"w","id":8,"word":">"}]}
        //		吃
        //	   感冒     药             br 
        //		什么         < \/ >
    	JsonObject json = parser.parse(BaiduUtil.dependencyParse(question)).getAsJsonObject();
    	
    	JsonArray items = json.get("items").getAsJsonArray();
        int itemLength = items.size();
        String[] ws = new String[itemLength];
        for (int i = 0; i < itemLength; i++) {
            JsonObject item = items.get(i).getAsJsonObject();
            String word = item.get("word").getAsString();
            int head = item.get("head").getAsInt();
            //1,CS,感冒
            tree.addNode(i + 1, item.get("deprel").getAsString(), word);
            ws[i] = word;
        }
        
        for (int i = 0; i < itemLength; i++) {
            JsonObject item = items.get(i).getAsJsonObject();
            //根据信息构建自己的语义树
            tree.addRelationshipForWhole(item.get("head").getAsInt(), i + 1);
        }
    	
    	return tree;
    }

    public SyntaxTree parse(String sentence) {
        SyntaxTree tree = new SyntaxTree(sentence);
        //parser.字符串形成json
        JsonParser parser = new JsonParser();
        
        
        
        //id 	int 	词的ID
        //postag 	string 	词性，请参照下方词性（postag)取值范围
        //head 	int 	词的父节点ID
        //deprel 	string 	词与父节点的依存关系，请参照下方依存关系标识
        //词性 	含义 	词性 	含义 	词性 	含义 	词性 	含义
        //Ag 	形语素 	g 	语素 	ns 	地名 	u 	助词
        //a 	形容词 	h 	前接成分 	nt 	机构团体 	vg 	动语素
        //ad 	副形词 	i 	成语 	nz 	其他专名 	v 	动词
        //an 	名形词 	j 	简称略语 	o 	拟声词 	vd 	副动词
        //b 	区别词 	k 	后接成分 	p 	介词 	vn 	名动词
        //c 	连词 	 l 	习用语 	q 	量词 	w 	标点符号
        //dg 	副语素 	m 	数词 	r 	代词 	x 	非语素字
        //d 	副词 	Ng 	名语素 	s 	处所词 	y 	语气词
        //e 	叹词 	n 	名词 	tg 	时语素 	z 	状态词
        //f 	方位词 	nr 	人名 	t 	时间词 	un 	未知词
        
        //{"log_id":8214424394686623745,"text":"感冒吃什么药<br\/>",
        //"items":
        //[{"head":2,"deprel":"CS","postag":"v","id":1,"word":"感冒"},
        //{"head":0,"deprel":"HED","postag":"v","id":2,"word":"吃"},
        //{"head":4,"deprel":"ATT","postag":"r","id":3,"word":"什么"},
        //{"head":2,"deprel":"VOB","postag":"n","id":4,"word":"药"},
        //{"head":6,"deprel":"WP","postag":"w","id":5,"word":"<"},
        //{"head":2,"deprel":"IC","postag":"n","id":6,"word":"br"},
        //{"head":6,"deprel":"WP","postag":"w","id":7,"word":"\/"},
        //{"head":6,"deprel":"WP","postag":"w","id":8,"word":">"}]}
        //		吃
        //	   感冒     药             br 
        //		什么         < \/ >
        
        JsonObject json = parser.parse(BaiduUtil.dependencyParse(sentence)).getAsJsonObject();
        
        JsonArray items = json.get("items").getAsJsonArray();
        int itemLength = items.size();
        String[] ws = new String[itemLength];
        
        for (int i = 0; i < itemLength; i++) {
            JsonObject item = items.get(i).getAsJsonObject();
            String word = item.get("word").getAsString();
            //1,CS,感冒
            tree.addNode(i + 1, item.get("deprel").getAsString(), word);
            ws[i] = word;
        }
        
        for (int i = 0; i < itemLength; i++) {
            JsonObject item = items.get(i).getAsJsonObject();
            //根据信息构建自己的语义树
            tree.addRelationship(item.get("head").getAsInt(), i + 1);
        }
        
        tree.cutAll();
        return tree;
    }
    
}

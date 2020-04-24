package com.appleyk.InitElements;


public class Config {

    //********************************* 知识库配置 *********************************
    public static final String KNOWLEDGE_BASE_FILE_PATH = "G:\\毕设\\数据\\kb.ttl";
    public static final String KNOWLEDGE_BASE_PREFIX = "PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
            "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>" +
            "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>" +
            "PREFIX exampleo: <http://www.example.com/ontology/#>" +
            "PREFIX example: <http://www.example.com/> ";
    //********************************* 知识库配置 *********************************
    //********************************* 缓存配置 *********************************
    public static final String WORD2VEC_CACHE_FILE_PATH = Config.class.getResource("/cache/word2vecCache.txt").getPath();
    //********************************* 缓存配置 *********************************

    //********************************* 问题分析配置 *********************************
    public static final int NER_THREAD_NUM = 10;    //语义相似度计算中线程数量
    public static final int WORD2VEC_DIMENSION = 1024;
    //********************************* 问题分析配置 *********************************
    
    //********************************* 查询图构建配置 *********************************
    public static final int ALLOW_QUERY_STEP_NUMBER = 2;
    public static final int RELATION_SETP_Limit = 2;
    //********************************* 查询图构建配置 *********************************
}

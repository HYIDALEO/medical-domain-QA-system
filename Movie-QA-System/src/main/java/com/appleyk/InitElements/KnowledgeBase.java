package com.appleyk.InitElements;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.ModelFactory;

import com.appleyk.node.KbResults;
import com.google.common.base.Preconditions;


public class KnowledgeBase {
    private OntModel model = ModelFactory.createOntologyModel();
    private static final Logger LOGGER = Logger.getLogger(KnowledgeBase.class.getName());
    private List<String> classOntology = new ArrayList<>();
    public void initOntology() {
        String sql = Config.KNOWLEDGE_BASE_PREFIX + "SELECT ?x WHERE{ ?x rdf:type owl:Class .}";
        KbResults results = executeQuery(sql);
        //this.classOntology:[药品, 部位, 科室, 症状, 疾病]
        while (results.hasNext()) {
            results.next().values().forEach(value -> {
                this.classOntology.add(value.substring(value.lastIndexOf("#") + 1));
            });
        }
        

        //this.classOntology:[药品, 部位, 科室, 症状, 疾病, 症状相关药品, 部位相关疾病, 科室相关疾病, 疾病相关症状, 药品相关症状, 疾病相关药品, 疾病相关科室, 药品相关疾病, 症状相关疾病, 疾病相关部位]
        sql = Config.KNOWLEDGE_BASE_PREFIX + "SELECT ?x WHERE{ ?x rdf:type owl:ObjectProperty .}";
        results = executeQuery(sql);
        while (results.hasNext()) {
            results.next().values().forEach(value -> {
                this.classOntology.add(value.substring(value.lastIndexOf("#") + 1));
            });
        }
        

    }
    public KbResults executeQuery(String querySql) {
    	//Guava学习笔记：Preconditions优雅的检验参数
    	
        Preconditions.checkNotNull(querySql);
        
        Preconditions.checkArgument(!querySql.equals(""));
        
        ResultSet rs = QueryExecutionFactory.create(QueryFactory.create(querySql), model).execSelect();
        KbResults results = new KbResults();
        while (rs.hasNext()) {
        	
            QuerySolution qs = rs.nextSolution();
            
            Iterator<String> it = qs.varNames();
            results.newReuslt();
            while (it.hasNext()) {
                String key = it.next();
                results.add(key, qs.get(key).toString());
            }
        }
        return results;
    }
	public void init(String filePath) {
        LOGGER.log(Level.INFO, "filepath = {0}", filePath);
        model.read(filePath);
        initOntology();
    }
    private static KnowledgeBase instance = new KnowledgeBase();
    
    public static String handleResultWithOntology(String ontology) {
        return ontology.substring(ontology.lastIndexOf("#") + 1);
    }

    public static String handleResult(String result) {
        return result.substring(result.lastIndexOf("/") + 1);
    }
    public static KnowledgeBase getInstance() {
        return instance;
    }
}

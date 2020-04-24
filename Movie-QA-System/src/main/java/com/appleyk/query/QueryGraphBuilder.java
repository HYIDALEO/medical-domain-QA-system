package com.appleyk.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.appleyk.InitElements.Config;
import com.appleyk.InitElements.KnowledgeBase;
import com.appleyk.node.KbEntity;
import com.appleyk.node.KbResults;



public class QueryGraphBuilder {
    /**
     * 边界，用于扩展图
     * enode和snode
     */
    public static List<KbEntity> boundary = new ArrayList<>();

    /**
     * 扩展查询图
     * 使得图里拥有的与其连接的所有的结点都在图上
     * @param graph
     */
    public static void expand(QueryGraph graph) {
    	
        List<KbEntity> boundaryTemp = new ArrayList<>();
        boundary.forEach(kbEntity -> {
            //go out
            String sql = Config.KNOWLEDGE_BASE_PREFIX
                    + "SELECT ?a ?b ?c WHERE{ example:" + kbEntity.name + " ?a ?b . "
                    + "?b rdf:type ?c ."
                    + "?c rdf:type owl:Class .}";
            KbResults results = KnowledgeBase.getInstance().executeQuery(sql);
            while (results.hasNext()) {
                Map<String, String> result = results.next();
                KbEntity enode = new KbEntity(KnowledgeBase.handleResult(result.get("b")), KnowledgeBase.handleResultWithOntology(result.get("c")));
                if (!graph.hasNode(enode)) {
                    boundaryTemp.add(enode);
                }
                graph.putEdge(kbEntity, enode, KnowledgeBase.handleResultWithOntology(result.get("a")));
            }
            
            //come back
            sql = Config.KNOWLEDGE_BASE_PREFIX
                    + "SELECT ?a ?b ?c WHERE{ ?a ?b example:" + kbEntity.name + " . "
                    + "?a rdf:type ?c ."
                    + "?c rdf:type owl:Class .}";
            results = KnowledgeBase.getInstance().executeQuery(sql);
            while (results.hasNext()) {
                Map<String, String> result = results.next();
                KbEntity snode = new KbEntity(KnowledgeBase.handleResult(result.get("a")),
                        KnowledgeBase.handleResultWithOntology(result.get("c")));
                if (!graph.hasNode(snode)) {
                    boundaryTemp.add(snode);
                }
                graph.putEdge(snode, kbEntity, KnowledgeBase.handleResultWithOntology(result.get("b")));
            }
        });
        boundary = boundaryTemp;
    }

    public static void expand(QueryGraph graph, int step) {
        for (int i = 0; i < step; i++) {
            expand(graph);
        }
    }

    /**
     * 查询图构建模块 根据已知条件以知识库为主构建图形
     *
     * @param knownConditions
     * @return
     */
    public static QueryGraph build(List<KbEntity> knownConditions) {
        boundary.clear();
        final QueryGraph graph = new QueryGraph();
        knownConditions.forEach((kbEntity) -> {
        	
        	//两次查询是因为要得到双向图
            //go out
        	//搜索出与 kbEntity.name 有关的所有三元组的信息
            String sql = Config.KNOWLEDGE_BASE_PREFIX
                    + "SELECT ?a ?b ?c WHERE{ example:" + kbEntity.name + " ?a ?b . "
                    + "?b rdf:type ?c ."
                    + "?c rdf:type owl:Class .}";
            KbResults results = KnowledgeBase.getInstance().executeQuery(sql);
 //           System.out.println("QueryGraphBuilder	build:kbEntity");
 //           System.out.println(kbEntity.name);
 //           System.out.println("QueryGraphBuilder	build:results");
            while (results.hasNext()) {
            	
            	
                Map<String, String> result = results.next();
/*                System.out.println("QueryGraphBuilder	build:results upper part");
                System.out.println("KnowledgeBase.handleResult(result.get(\"b\"))");
                System.out.println(KnowledgeBase.handleResult(result.get("b")));
                System.out.println("result.get(\"b\")");
                System.out.println(result.get("b"));
                System.out.println("KnowledgeBase.handleResultWithOntology(result.get(\"c\"))");
                System.out.println(KnowledgeBase.handleResult(result.get("c")));
                System.out.println("result.get(\"c\")");
                System.out.println(result.get("c"));
                System.out.println("KnowledgeBase.handleResultWithOntology(result.get(\"a\"))");
                System.out.println(KnowledgeBase.handleResult(result.get("a")));
                System.out.println("result.get(\"a\")");
                System.out.println(result.get("a"));
  */              
                
                //b是   阵发性睡眠性血红蛋白尿
                //c是   疾病
                //a是   药品相关疾病
                KbEntity enode = new KbEntity(KnowledgeBase.handleResult(result.get("b")),
                        KnowledgeBase.handleResultWithOntology(result.get("c")));
                boundary.add(enode);
                graph.putEdge(kbEntity, enode, KnowledgeBase.handleResultWithOntology(result.get("a")));
            }
            
            
            
            
            
            //come back
            sql = Config.KNOWLEDGE_BASE_PREFIX
                    + "SELECT ?a ?b ?c WHERE{ ?a ?b example:" + kbEntity.name + " . "
                    + "?a rdf:type ?c ."
                    + "?c rdf:type owl:Class .}";
            

            
            results = KnowledgeBase.getInstance().executeQuery(sql);
            while (results.hasNext()) {
                Map<String, String> result = results.next();
                
 //               System.out.println("QueryGraphBuilder	build:results bottom part");
 //               System.out.println("KnowledgeBase.handleResult(result.get(\"a\"))");
 //               System.out.println(KnowledgeBase.handleResult(result.get("a")));
 //               System.out.println("result.get(\"a\")");
 //               System.out.println(result.get("a"));
 //               System.out.println("KnowledgeBase.handleResultWithOntology(result.get(\"c\"))");
 //               System.out.println(KnowledgeBase.handleResult(result.get("c")));
 //               System.out.println("result.get(\"c\")");
  //              System.out.println(result.get("c"));
  //              System.out.println("KnowledgeBase.handleResultWithOntology(result.get(\"b\"))");
  //              System.out.println(KnowledgeBase.handleResult(result.get("b")));
  //              System.out.println("result.get(\"b\")");
  //              System.out.println(result.get("b"));
                KbEntity snode = new KbEntity(KnowledgeBase.handleResult(result.get("a")),
                        KnowledgeBase.handleResultWithOntology(result.get("c")));
                boundary.add(snode);
                graph.putEdge(snode, kbEntity, KnowledgeBase.handleResultWithOntology(result.get("b")));
            }
            
            
            
            
        });
        expand(graph, Config.ALLOW_QUERY_STEP_NUMBER - 1);
        return graph;
    }

}

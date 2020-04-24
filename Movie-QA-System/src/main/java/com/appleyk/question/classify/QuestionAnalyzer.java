package com.appleyk.question.classify;







import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.arrow.vector.util.Text.TextSerializer;

import com.appleyk.question.classify.ner.Ner;
import com.appleyk.question.classify.ner.Ners;
import com.appleyk.query.Constraint;
import com.appleyk.query.QueryGraph;
import com.appleyk.query.QueryGraph.Edge;
import com.appleyk.query.QueryGraphBuilder;
import com.appleyk.QException;
import com.appleyk.RetCode;
import com.appleyk.InitElements.QAWordSegmenter;
import com.appleyk.answer.AnswerType;
import com.appleyk.answer.Graph.ShortestDistance;
import com.appleyk.node.KbEntity;
import com.appleyk.node.RecognizedResult;
import com.appleyk.node.Sentence;
import com.appleyk.operation.QuickSort;
import com.appleyk.question.classify.QuestionType;
import libsvm.svm;
import libsvm.svm_node;

public class QuestionAnalyzer {
    private final Ners entityNers = new Ners();
    private final Ners relationNers = new Ners();
    private final QuestionClassifier classifier;
    //与日志相关
    private static final Logger LOGGER = Logger.getLogger(QuestionAnalyzer.class.getName());
    public QuestionAnalyzer(QuestionClassifier classifier) {
        this.classifier = classifier;
    }
    
    
    public void addEntityNer(Ner ner) {
        this.entityNers.addNer(ner);
    }
    public void addRelationNer(Ner ner) {
        this.relationNers.addNer(ner);
    }
    //在answerNer进行了注释
    @SuppressWarnings("deprecation")
	public String analysis_DP(String question) throws QException, InterruptedException, IOException{
    	String analysis_result=null;
    	QuestionType questionType = classifier.classify(question);
    	QuickSort qs=new QuickSort();
    	
    	if (null != questionType) {
    		if (QuestionType.EXPLAIN == questionType) {
    			throw new QException(RetCode.QUESTION_TYPE_NOT_SUPPOT);
    		}

    		
    		
    		
            //问句中的实体列表
    		List<String> questionCacheSubsi = new ArrayList<String>();
    		//问句中的实体位置
    		List<Integer> questionCacheStart = new ArrayList<Integer>();
    		//将问句分词
    		List<String> result_qu = new ArrayList<String>();
    //		entityNers.shoeNers();
     
     
    //		question
    		
			for(int i=0;i<question.length();i++) {
				result_qu.add(String.valueOf(question.charAt(i)));
			}
			System.out.println(result_qu);
			//问句中的实体
			List<KbEntity> KbEntityList = entityNers.findEnt(question);
			//查找出的实体中，结果可能会有覆盖，比如：补肾丸和健脑补肾丸，因此要去重
			for(int i=0;i<KbEntityList.size();i++) {
				for(int j=0;j<KbEntityList.size();j++) {
					if(KbEntityList.get(i).name.indexOf(KbEntityList.get(j).name)!=-1) {
						//去掉j
						if(i<j) {
							KbEntityList.remove(j);
							j--;
						}else if(i>j){
							KbEntityList.remove(j);
							i--;
							j--;
						}
					}
				}
				
				
			}
			
			for(int i=0;i<KbEntityList.size();i++) {
				questionCacheSubsi.add(KbEntityList.get(i).name);
			}
	//		questionCacheSubsi.addAll(entityNers.findEnt(text));
			for(int i=0;i<questionCacheSubsi.size();i++) {
				questionCacheStart.add(question.indexOf(questionCacheSubsi.get(i)));
			}
			
			for(int i=0;i<questionCacheSubsi.size();i++) {
				//对于每一个分词
				for(int j=0;j<result_qu.size();j++) {
					if(j==questionCacheStart.get(i)) {
						
						int len = questionCacheSubsi.get(i).length();
						
						for(int inner_count=0;inner_count<len;inner_count++) {
							//因为删除完自动向前移位
							result_qu.remove(j);
						}
						String entity = questionCacheSubsi.get(i);
						
				//		String onto = entityNers.getOnt(entity);
						len = KbEntityList.get(i).ontology.length();
						
						//代替的字符串从末尾到开头的顺序加入问题，因为加入了数组元素后移
						for(int inner_count=0;inner_count<len;inner_count++) {
							result_qu.add(questionCacheStart.get(i),String.valueOf(KbEntityList.get(i).ontology.charAt(len-1-inner_count)));
						}
				//		result_qu.add(index, element);
					}else {
					//	result_qu.add(String.valueOf(text.charAt(j)));
					}
				}
			}			
			
			System.out.println(questionCacheSubsi);
			System.out.println(questionCacheStart);
			System.out.println(result_qu);

    		StringBuffer sb_result = new StringBuffer();
    		for(int i=0;i<result_qu.size();i++) {
    			sb_result.append(result_qu.get(i));
    		}
    		String qustion_onto = sb_result.toString();
    		
    		
    		
    		StringBuffer input_sb_result = new StringBuffer();
    		for(int i=0;i<result_qu.size();i++) {
    			if(i+1<result_qu.size()) {
	    			if(result_qu.get(i).equals("疾")&&result_qu.get(i+1).equals("病")) {
	    				input_sb_result.append("disease");
	    				i++;
	    			}else if(result_qu.get(i).equals("药")&&result_qu.get(i+1).equals("品")) {
	    				input_sb_result.append("drug");
	    				i++;
	    			}else if(result_qu.get(i).equals("症")&&result_qu.get(i+1).equals("状")) {
	    				input_sb_result.append("symptom");
	    				i++;
	    			}else {
	    				input_sb_result.append(result_qu.get(i));
	    			}
    			}else {
    				input_sb_result.append(result_qu.get(i));
    			}
    			
    		}
    		String inputQuestion = input_sb_result.toString();
    		System.out.println(inputQuestion);
    		
    		
    		SyntaxTree tree = DependencyParser.parseTreeDetail(qustion_onto);
    		tree.showTree();
    		//并行进行排序
    		
			//排序，使得顺序按照开始的位置从小到大
			int[] list= new int[questionCacheSubsi.size()];
			String[] list_S= new String[questionCacheSubsi.size()];
			
			for(int i=0;i<questionCacheSubsi.size();i++) {
				list[i]=questionCacheStart.get(i);
				list_S[i]=questionCacheSubsi.get(i);
			}
			
			qs._quickSort(list, list_S, 0,questionCacheSubsi.size()-1);
			
			for(int i=0;i<questionCacheSubsi.size();i++) {
				questionCacheStart.set(i, list[i]);
				questionCacheSubsi.set(i, list_S[i]);
			}
    		
    		
    		
    		
    		
    		
    		for(int i=0;i<questionCacheSubsi.size();i++) {
    			tree.changeValue(questionCacheStart.get(i),questionCacheSubsi.get(i));
    		}
    		tree.showTree();
    		
    		//question 传到python
    		
    		//返回answer 
    		
    		
    		//questionCacheSubsi，问句中的实体
    		
    		
    		List<RecognizedResult> recognizedResults = this.entityNers.recognizeList(questionCacheSubsi);
        	final List<KbEntity> knownConditions = new ArrayList<>();
            final List<KbEntity> answerConditions = new ArrayList<>();
    		for(RecognizedResult recognizedResult: recognizedResults) {
                if (recognizedResult.entity.name != null) {
                    knownConditions.add(recognizedResult.entity);
                } else {
                    answerConditions.add(recognizedResult.entity);
                }
    		}
    		
    		//查询图构建
    		final QueryGraph graph = new QueryGraph();
    		
            

            
    		knownConditions.forEach(knownCondition -> {
                List<KbEntity> entitys = new ArrayList<>();
                entitys.add(knownCondition);
                LOGGER.log(Level.INFO, "识别出name = {0}, ontoloty = {1}的实体", new String[]{knownCondition.name, knownCondition.ontology});
                graph.addGraph(QueryGraphBuilder.build(entitys));
            });
    		
            knownConditions.forEach(knownCondition -> {
                graph.computeShortestDistance(knownCondition);
            });
    		
    		LOGGER.log(Level.INFO, "查询图最短距离计算结束");
    		int numOfOnto = 0;
    		List<String> ontoList = new ArrayList<>();
    		List<Integer> ontoNum = new ArrayList<>();
    		knownConditions.forEach(knownCondition -> {
    			if(!ontoList.contains(knownCondition.ontology)) {
    				ontoList.add(knownCondition.ontology);
    				ontoNum.add(1);
    			}else {
    				ontoNum.set(ontoList.indexOf(knownCondition.ontology), ontoNum.get(ontoList.indexOf(knownCondition.ontology))+1);
    			}
    		});
    		
    		System.out.println(ontoList);
    		System.out.println(ontoNum);
    		
    		if(answerConditions.isEmpty()) {
    			
    			if(questionType == QuestionType.BOOL||ontoList.size()==2) {
        			if(knownConditions.size()>0) {
        				if(ontoList.size()>0) {
        					List<QueryGraph.Edge> edge_list = new ArrayList<>();
                        	for (KbEntity entity : knownConditions) {
                            	//可以到达 entity的所有的边
                        		List<QueryGraph.Edge> edges = graph.myfindInEdges(entity);
                        		
                                //前后节点的关系，如疾病相关症状
                            //    Set<String> inEdges = inEdgeMap.keySet();
                    
                                if (edges != null && !edges.isEmpty()) {
                                	for(QueryGraph.Edge edge : edges) {
                                		edge_list.add(edge);
                                	}
                                	


                                }
                        	}
                        	Stack<KbEntity> staffs = new Stack<KbEntity>();
                        	
                        	staffs.add(knownConditions.get(0));
                        	
                        	knownConditions.remove(0);
                        	
                        	
                        	
                        	
                        	
                        	KbEntity temp = staffs.pop();
                        	int find = 0;
                        		for(QueryGraph.Edge edge : edge_list) {
                        			if(edge.snode.name.equals(temp.name)) {
                        				
                        				staffs.add(edge.enode);
                        				for(KbEntity knownCondition : knownConditions) {
                        					if(knownCondition.name.equals(edge.enode.name)) {
                        						knownConditions.remove(knownCondition);
                        						find =1;
                        						break;
                        					}
                        				}
                        				
                        			}
                        			if(find == 1) {
                        				break;
                        			}
                        		}
                        	
                        	if(find==1) {
                        		analysis_result = "可以";
                        	}else {
                        		analysis_result = "不可以";
                        	}
                        	
                        	
                        	
        				}
        			}
    			}else {
    				
    				String receive = graph.getPythonIstmFromAnaConda(inputQuestion);
                    
                    List<String> RecOntoList = new ArrayList<>();
                    List<Integer> RecOntoNum = new ArrayList<>();
                    List<Integer> RecOntoIndex = new ArrayList<>();
                    List<String> RecAnswerist = new ArrayList<>();
                    
            		//将问句分词
            		List<String> Reresult_qu = new ArrayList<String>();

            		
        			for(int i=0;i<receive.length();i++) {
        				Reresult_qu.add(String.valueOf(receive.charAt(i)));
        			}
        			System.out.println(Reresult_qu);
            		for(int i=0;i<Reresult_qu.size();i++) {
            			if(i+6<Reresult_qu.size()) {
        	    			if(Reresult_qu.get(i).equals("d")&&Reresult_qu.get(i+6).equals("e")) {
        	    				
        	    				RecOntoList.add("疾病");
        	    				RecOntoIndex.add(i);
        	    				i+=6;
        	    			}else if(Reresult_qu.get(i).equals("s")&&Reresult_qu.get(i+1).equals("m")) {
        	    				RecOntoList.add("症状");
        	    				RecOntoIndex.add(i);
        	    				i+=6;
        	    			}else if(Reresult_qu.get(i).equals("d")&&Reresult_qu.get(i+3).equals("g")) {
            					RecOntoList.add("药品");
        	    				RecOntoIndex.add(i);
        	    				i+=3;
        	    			}
            			}else if(i+3<Reresult_qu.size()){
            				if(Reresult_qu.get(i).equals("d")&&Reresult_qu.get(i+3).equals("g")) {
            					RecOntoList.add("药品");
        	    				RecOntoIndex.add(i);
        	    				i+=3;
        	    			}
            			}
            			
            		}
            		
            		System.out.println("RecOntoList");
            		System.out.println(RecOntoList);
            		System.out.println("RecOntoIndex");
            		System.out.println(RecOntoIndex);
            	
                    
        			if(knownConditions.size()>0) {
        				if(ontoList.size()>0) {
        					
                        	for (KbEntity entity : knownConditions) {
                            	//可以到达 entity的所有的边
                        		List<QueryGraph.Edge> edges = graph.myfindInEdges(entity);
                                //前后节点的关系，如疾病相关症状
                            //    Set<String> inEdges = inEdgeMap.keySet();
                    
                                if (edges != null && !edges.isEmpty()) {
                                	System.out.println(RecOntoList.size());
                                	for(String onto: RecOntoList) {
                                		
                                		for(QueryGraph.Edge edge:edges) {
                                			if(edge.snode.ontology.equals(onto)) {
                                				if(!RecAnswerist.contains(edge.snode.name)) {
                                    				System.out.println(edge.snode.ontology);
                                    				System.out.println(onto);
                                    				RecAnswerist.add(edge.snode.name);
                                    				break;
                                				}

                                			}
                                			
                                		}
                                		
                                	}

                                }
                                int index_list =0;
                                int index = RecOntoIndex.get(index_list);
                        		StringBuffer sb_result_receive = new StringBuffer();
                        		for(int i=0;i<result_qu.size();i++) {
                        			sb_result.append(result_qu.get(i));
                        		}
                                for(int i=0;i<receive.length();i++) {
                                	if(i == index) {
                                		
                                		for(int ii=0;ii<RecAnswerist.get(index_list).length();ii++) {
                                			sb_result_receive.append(String.valueOf(RecAnswerist.get(index_list).charAt(ii)));
                                		}
                                		if(RecOntoList.get(index_list).equals("疾病")) {
                                			i+=7;
                                		}else if(RecOntoList.get(index_list).equals("药品")) {
                                			i+=4;
                                		}else if(RecOntoList.get(index_list).equals("症状")) {
                                			i+=7;
                                		}
                                		
                                		i--;
                                		if(index_list+1<RecOntoIndex.size()) {
                                			index_list++;
                                    		index = RecOntoIndex.get(index_list);
                                		}
                                		
                                	}else if(receive.charAt(i)!=' '){
                                		sb_result_receive.append(String.valueOf(receive.charAt(i)));
                                	}
                    			}
                                analysis_result = sb_result_receive.toString();
                                System.out.println(sb_result_receive.toString());
                                
                                for (String answer:RecAnswerist) {
                                	System.out.println(answer);
                                }
                        	}
        				}
        			}
    			}

    		}
    		

    	}
    	return analysis_result;
    }




    

    
    public List<KbEntity> createRelations(Set<String> entities) {
        List<KbEntity> result = new ArrayList<>();
        entities.forEach(entity -> {
            switch (entity) {
                case "药品相关疾病":
                    result.add(new KbEntity("治疗", "药品相关疾病"));
                    break;
                case "疾病相关药品":
                    result.add(new KbEntity("吃", "疾病相关药品"));
                    result.add(new KbEntity("使用", "疾病相关药品"));
                    break;
                case "症状相关疾病":
                	result.add(new KbEntity("吃", "症状相关疾病"));
                	break;
                case "疾病相关症状":
                	result.add(new KbEntity("吃", "疾病相关症状"));
                	break;
                case "症状相关药品":
                	result.add(new KbEntity("吃", "症状相关药品"));
                	break;
                case "药品相关症状":
                	result.add(new KbEntity("吃", "药品相关症状"));
                	break;
                case "科室相关疾病":
                	result.add(new KbEntity("吃", "科室相关疾病"));
                	break;
                case "疾病相关科室":
                	result.add(new KbEntity("吃", "疾病相关科室"));
                	break;
            }
        });
        return result;
    }
    

    
    public void showNer() {
    	System.out.println("------------------------------------------------------------------------------------");
    	this.relationNers.shoeNers();
    	System.out.println("---------------------------------------end entities---------------------------------------------");
 //   	System.out.println("------------------------------------------------------------------------------------");
   // 	this.relationNers.shoeNers();
    //	System.out.println("---------------------------------------end relations---------------------------------------------");
    }
    

    
}

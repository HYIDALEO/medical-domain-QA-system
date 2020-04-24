package com.appleyk.query;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.appleyk.answer.Graph.ShortestDistance;
import com.appleyk.node.KbEntity;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class QueryGraph extends TopologyGraph<KbEntity, String> {
	

    public static class Edge{
        public KbEntity snode, enode;
        public String edge;
    }

    private final Table<KbEntity, KbEntity, Integer> shortestDistances = HashBasedTable.create();
    private final Table<KbEntity, KbEntity, List<KbEntity>> shortestPaths = HashBasedTable.create();

    public void addGraph(QueryGraph other) {
        this.graph.putAll(other.graph);
        this.isolatedNodes.addAll(other.isolatedNodes);
    }

    //根据本体找节点
    public List<KbEntity> findNodesByOntology(String ontology) {
        List<KbEntity> entitys = new ArrayList<>();
        getNodes().forEach(node -> {
            if (node.ontology.equals(ontology)) {
                entitys.add(node);
            }
        });
        return entitys;
    }

    //根据名字找节点
    public KbEntity findNodesByName(String name) {
        List<KbEntity> entitys = new ArrayList<>();
        for (KbEntity entity : getNodes()) {
            if (entity.name.equals(name)) {
                return entity;
            }
        }
        return null;
    }

    //查找与某个节点相连的所有边
    public Map<String,Edge> findToEdges(KbEntity snode) {
        Map<String,Edge> edges = new HashMap<>();
        //node做起点
        graph.row(snode).forEach((key, value) -> {
            if (!value.isEmpty()) {
                Edge edge = new Edge();
                edge.snode = snode;
                edge.enode = key;
                edge.edge = value.iterator().next();
                edges.put(edge.edge,edge);
            }
        });
        return edges;
    }

    public List<Edge> myfindInEdges(KbEntity node) {
        List<Edge> edges = new ArrayList<>();
        graph.rowKeySet().forEach(snode -> graph.row(snode).forEach((KbEntity enode, Set<String> es) -> {
            if (enode.equals(node)) {
                if (es != null && !es.isEmpty()) {
                    Edge edge = new Edge();
                    edge.snode = snode;
                    edge.enode = enode;
                    edges.add(edge);
                }
            }
        }));
        return edges;
    }
    
    
    public Map<String,Edge> findInEdges(KbEntity node) {
        Map<String,Edge> edges = new HashMap<>();
        graph.rowKeySet().forEach(snode -> graph.row(snode).forEach((KbEntity enode, Set<String> es) -> {
            if (enode.equals(node)) {
                if (es != null && !es.isEmpty()) {
                    Edge edge = new Edge();
                    edge.snode = snode;
                    edge.enode = enode;
                    edge.edge = es.iterator().next();
                    edges.put(edge.edge,edge);
                }
            }
        }));
        return edges;
    }
    
    public String getPythonlstmFromAnaConda(String inputQuestion) throws IOException {
		String exe = "C:\\Users\\admin\\AppData\\Local\\Programs\\Python\\Python35\\python.exe";
	//	String command = "G:/毕设/lstm/myrunMod.py";
		String command = "myrunMod.py";
        String[] cmdArr = new String[] {exe,command, inputQuestion};
        System.out.println(System.getProperty("user.dir"));
		Process process = Runtime.getRuntime().exec("python "+command+" " +inputQuestion);
		
		
		InputStream is = process.getInputStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		
		System.out.println("start");
		String line = in.readLine();
		process.destroy();
		in.close();
    	return line;
    }

    public Table<KbEntity, KbEntity, Set<String>> findEdges(String nodeName) {

        return null;
    }

    //根据指定条件查找最短路径
    //现有如下限制条件
    //1.最短路径中必须包含某条边
    //2.最短路径中某个节点必须与某条边相连 
    public ShortestDistance findShortestDistance(List<KbEntity> snodes, KbEntity enode, List<Constraint> constraints) {
        final ShortestDistance shortestDistance = new ShortestDistance();
        if (constraints == null || constraints.isEmpty()) {
            //不存在约束条件的情况下，推理默认方向为已知条件推答案变量
        	//对所有的snode做加法，但是目前只有一个snode，不影响。
            snodes.forEach(snode -> {
                ShortestDistance distance = findShortestDistance(snode, enode);
                shortestDistance.distance += distance.distance;
                shortestDistance.unreachable_count += distance.unreachable_count;
                shortestDistance.nodes.addAll(distance.nodes);
            });
        } else {
            //约束条件只可能与起点或者终点相连接
            //在存在约束条件的情况下，推理方向由约束条件决定
            snodes.forEach(snode -> constraints.forEach(constraint -> {
                //约束条件与已知条件相关联
                ShortestDistance distance1;
                //看束缚的首或尾与哪个结点相连
                
                //constraint = snode1 -edge-> enode1
                //比如，snode与束缚的snode都是药品
                if (snode.ontology.equals(constraint.snode.ontology)) {
                    //snode == snode1 snode1 -edge-> enode1
                    //snode == snode1 -edge-> enode1 ->enode
                    //已知条件->答案变量
                    distance1 = findShortestDistance(snode, enode);
                } else if (snode.ontology.equals(constraint.enode.ontology)) {
                    //snode == enode1 snode1 -edge-> enode1
                    //enode -> snode1 -edge-> enode1 == snode
                    //答案变量->已知条件
                    distance1 = findShortestDistance(enode, snode);
                } else if (enode.ontology.equals(constraint.snode.ontology)) {
                    //enode == snode1 snode1 -edge-> enode1
                    //enode == snode1 -edge-> enode1 -> snode
                    distance1 = findShortestDistance(enode, snode);
                } else if(enode.ontology.equals(constraint.enode.ontology)){
                    //enode == enode1  snode1 -edge-> enode1
                    //snode -> node1 -edge-> enode1 == enode
                    distance1 = findShortestDistance(snode, enode);
                }else{
                    distance1 = new ShortestDistance();
                    distance1.unreachable_count = 1;
                }
                shortestDistance.distance += distance1.distance;
                shortestDistance.unreachable_count += distance1.unreachable_count;
                shortestDistance.nodes.addAll(distance1.nodes);
            }));
        }
        return shortestDistance;
    }

    public ShortestDistance findShortestDistance(KbEntity snode, KbEntity enode) {
        ShortestDistance distance = new ShortestDistance();
        //如果不存在，计算并添加
        if(!this.shortestDistances.containsRow(snode)){
            computeShortestDistance(snode);
        }
        
        if (this.shortestDistances.contains(snode, enode)) {//如果可达，添加所有路径
            distance.distance = this.shortestDistances.get(snode, enode);
            distance.nodes.addAll(this.shortestPaths.get(snode, enode));
        } else {//如果不可达，
            distance.unreachable_count++;
        }
        return distance;
    }

    public void computeShortestDistance(KbEntity snode) {
        dijkstra(snode);
    }
    

    
    public List<String> getTheEntitiesInRe(List<String> RecOntoList,List<KbEntity> knownConditions) {
    	
    	List<HashMap<KbEntity,HashMap<KbEntity,Integer>>> result_emd = new ArrayList<HashMap<KbEntity,HashMap<KbEntity,Integer>>>();

    	for(KbEntity knownCondition : knownConditions){

        	HashMap<KbEntity,HashMap<KbEntity,Integer>> emd_tem = new HashMap<KbEntity,HashMap<KbEntity,Integer>>();
        	Set<KbEntity> nodes = getNodes();
        	Set<KbEntity> nodeFound = new HashSet<>();
        	
        	
        	
        	Set<KbEntity> ano_nodes = new HashSet<KbEntity>();
        	
        	for(KbEntity snode : nodes) {
        		ano_nodes.add(snode);
        	}
        	
        	for(KbEntity snode : nodes) {
            	for(KbEntity enode : ano_nodes) {
            		HashMap<KbEntity,Integer> temp = new HashMap<KbEntity,Integer>();
            		temp.put(enode, Integer.MAX_VALUE);
            		emd_tem.put(snode,new HashMap<KbEntity,Integer>(temp));
            	}
        	}
        	
            for(KbEntity snode : nodes) {
            	for(KbEntity enode : ano_nodes) {
            		
            			
                			System.out.println(snode.name);
                			System.out.println(enode.name);
                			System.out.println(emd_tem.get(snode).get(enode));
            			

            		
            	}
            }

			
        	
            
            for(KbEntity snode : nodes) {
            	for(KbEntity enode : ano_nodes) {
            		if (!snode.equals(enode)) {
                        if (isConnected(snode, enode)) {
                        	HashMap<KbEntity,Integer> temp_inner = emd_tem.get(snode);
                        	temp_inner.replace(enode, 1);
                        	emd_tem.replace(snode, temp_inner);
                        	
                        	HashMap<KbEntity,Integer> temp_inner_2 = emd_tem.get(snode);
                        	temp_inner_2.replace(snode, 1);
                        	emd_tem.replace(enode, temp_inner_2);
                        }
                    }
            	}
            }
            



        	

        	
    		nodes.remove(knownCondition);
    		nodeFound.add(knownCondition);
    		boolean mark = true;
        	while(mark) {
            	KbEntity temp=null;
            	Integer dis = Integer.MAX_VALUE;
            	for(KbEntity node : nodes) {
            		if(!node.equals(knownCondition)) {
                		if(emd_tem.get(knownCondition).get(node)<dis) {
                			temp = node;
                			dis = emd_tem.get(knownCondition).get(node);
                		}
            		}
            	}
            	Integer pre_dis = emd_tem.get(knownCondition).get(temp);
            	for(KbEntity node : nodes) {
            		if(emd_tem.get(temp).get(node)+pre_dis<emd_tem.get(knownCondition).get(node)) {
            			emd_tem.get(knownCondition).put(node, dis+pre_dis);
            			emd_tem.get(node).put(knownCondition, dis+pre_dis);
            		}
            	}
            	
        		nodes.remove(temp);
        		nodeFound.add(temp);
            	if(nodes.isEmpty()) {
            		 mark = false;
            	}else {
            		int have = 0;
            		for(KbEntity node : nodes) {
            			if(emd_tem.get(knownCondition).get(node)!=Integer.MAX_VALUE) {
            				have  = 1;
            				break;
            			}
            		}
            		if(have ==0) {
            			mark = false;
            		}
            	}
        	}    	
    		
        	result_emd.add(emd_tem);
    	}
    	
    	return null;
    }
    
    public boolean judge(KbEntity knownCondition,Set<KbEntity> nodes) {
    	if(nodes.isEmpty()) {
    		return false;
    	}else {
    		for(KbEntity node : nodes) {
    			
    		}
    	}
    	return false;
    }
    
    public void calculateDis(KbEntity knownCondition) {

    }

    public void dijkstra(KbEntity snode) {
        class Inner {

            KbEntity preNode;
            int minDist;
            boolean isFinded;
        }

        final Map<KbEntity, Inner> inners = new HashMap<>();

        //初始化
        Set<KbEntity> nodes = getNodes();
        
        nodes.forEach(node -> {
            if (!node.equals(snode)) {
                Inner inner = new Inner();
                inner.isFinded = false;
                inner.preNode = null;
                if (isConnected(snode, node)) {
                    inner.minDist = 1;
                } else {
                    inner.minDist = Integer.MAX_VALUE;
                }
                
                inners.put(node, inner);
            }
        });

        
        int num = nodeSize();
        for (int i = 0; i < num; i++) {
            //求最近距离顶点
            KbEntity minNode = null;
            int minDist = Integer.MAX_VALUE;
            //一次找到距离最短的点minNode
            for (KbEntity n : nodes) {
                if (!n.equals(snode)) {
                    Inner inner = inners.get(n);
                    if (!inner.isFinded && inner.minDist < minDist) {
                        minDist = inner.minDist;
                        minNode = n;
                    }
                }
            }
            
            //根据最近距离顶点修正其他所有节点
            //如果有长度为2的路径，下面的程序就会找到它
            if (null != minNode) {
                Inner inner = inners.get(minNode);
                inner.isFinded = true;

                for (KbEntity n : nodes) {
                    if (!n.equals(snode) && !n.equals(minNode)) {
                        Inner otherInner = inners.get(n);
                        if (!otherInner.isFinded && isConnected(minNode, n)) {
                            if ((minDist + 1) < otherInner.minDist) {
                                otherInner.preNode = minNode;
                                otherInner.minDist = minDist + 1;
                            }
                        }
                    }
                }
                
            }
        }
        //装下所有可能是最短路径点的路径与距离
        nodes.forEach(node -> {
            if (!node.equals(snode)) {
                Inner inner = inners.get(node);
                if (Integer.MAX_VALUE != inner.minDist) {

                    this.shortestDistances.put(snode, node, inner.minDist);
                    Stack<KbEntity> stack = new Stack<>();
                    KbEntity enode = node;
                    while (null != enode) {
                        stack.push(enode);
                        enode = inner.preNode;
                        inner = inners.get(enode);
                    }
                    List<KbEntity> path = new ArrayList<>();
                    path.add(snode);
                    while (!stack.isEmpty()) {
                        path.add(stack.pop());
                    }
                    this.shortestPaths.put(snode, node, path);
                }
            }
        });
    }
    
    public String getPythonIstmFromAnaConda(String inputQuestion) throws IOException {
    	String result = null;
    	if(inputQuestion.indexOf("disease")!=-1) {
    		result = "吃点  drug 。";
    	}else if(inputQuestion.indexOf("symptom")!=-1){
    		if(inputQuestion.indexOf("怎么办")!=-1) {
    			result = "每天  drug 。";
    		}else if(inputQuestion.indexOf("怎么回事")!=-1) {
    			result = "disease 。";
    		}else{
    			result = "disease 。";
    		}
    	}else if(inputQuestion.indexOf("drug")!=-1){
    		result = "disease 。";
    	}
    	return result;
    }

    public void printShortPath(KbEntity snode, KbEntity enode) {
        if (this.shortestPaths.contains(snode, enode)) {
            for (KbEntity node : this.shortestPaths.get(snode, enode)) {
                System.out.println(node.name);
            }
        }
    }

}

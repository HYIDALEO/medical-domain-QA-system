package com.appleyk.question.classify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.appleyk.InitElements.QAWordSegmenter;
import com.google.common.base.Preconditions;



public class SyntaxTree {
    private TreeMap<Integer, Node> nodes = new TreeMap<>();
    private String text;
    private int size;

    public int size() {
        return this.nodes.size() - 1;
    }
    
    public void changeValue(int start,String value) {
    	int count=0;
    	for(Node node : this.nodes.values()) {
    		if(count==start) {
    			node.setWord(value);
    			break;
    		}
    		
    		count+=node.getWord().length();
    	}
    }

    public String getText() {
        return this.text;
    }
    
    public void showTree() {
    	System.out.println("SyntaxTree	showTree:");
    	for(Node node : this.nodes.values()) {
    		System.out.print("{index:"+node.getIndex());
    		System.out.print(",parent:"+node.getParent());
    		System.out.println(",word:"+node.getWord()+"}");
    	}
    }

    public void markMatched(String nodeString) {
        for (Node node : this.nodes.values()) {
            if (node.getWord().equals(nodeString)) {
                node.setIsMatch(true);
                break;
            }
        }
    }

    //这个方法可能需要根据不断测试不断改进
    //目前暂定要求两节点必须相邻，且这两节点其中一个节点的父类是另一个节点，或者两个节点的父类为同一节点
    //碰到其他情况可能会出现问题，碰到再说
    public String merge(String node1String, String node2String) {
        Node node1 = null;
        Node node2 = null;
        for (Node node : this.nodes.values()) {
            if (node.word.equals(node1String) && node1 == null) {
                node1 = node;
            } else if (node.word.equals(node2String) && node2 == null) {
                node2 = node;
            }
            if (node1 != null && node2 != null) {
                break;
            }
        }
        //说明这两节点已经是一个节点了
        if (node1 == null && node2 == null) {
            return null;
        }
        return merge(node1, node2).word;
    }

    //节点融合
    private Node merge(Node node1, Node node2) {
        StringBuilder sb = new StringBuilder();
        sb.append(node1.word);
        sb.append(node2.word);

        node1.setWord(sb.toString());
        if (node1.getParent() == null && node2.getParent() != null) {
            node1.setParent(node2.getParent());
        } else if (node1.getParent() != null && node2.getParent() == null) {
            node1.setParent(node1.getParent());
        } else if (node1.getParent().getIndex() == node2.getIndex()) {
            node1.setParent(node2.getParent());
        } else if (node2.getParent().getIndex() == node1.getIndex()) {
            //不动
        } else if (node1.getParent().getIndex() == node2.getParent().getIndex()) {
            //不动
        } else {
            Node parent1 = node1.getParent();
            Node parent2 = node2.getParent();
            while (parent1 != null && parent2 != null && parent1 != parent2) {
                parent1 = parent1.getParent();
                parent2 = parent2.getParent();
            }
            if (node1.getParent() == null && node2.getParent() != null) {
                node1.setParent(node2.getParent());
            } else if (node1.getParent() != null && node2.getParent() == null) {
                node1.setParent(node1.getParent());
            } else {
                node1.setParent(node1.getParent());
            }
        }
        if (node1.getParent() == node1 || node1.getParent() == node2) {
            node1.setParent(null);
        }
        this.nodes.remove(node2.getIndex());
        return node1;
    }

    public void cutAll() {
        Map<Node, List<String>> needCut = new HashMap<>();
        List<Node> needRemove = new ArrayList<>();
        this.nodes.values().forEach(node -> {
        	//对每一个词进行分词
            List<String> words = QAWordSegmenter.getInstance().segment(node.getWord(), true, true);
            //如果需要分词，将词加入list，如果这个词无意义，加入移除list
            if (words.size() > 1) {
                needCut.put(node, words);
            } else if (words.isEmpty()) {
                needRemove.add(node);
            }
        });
        needRemove.forEach(node -> {
            nodes.remove(node.getIndex());
            nodes.values().forEach(n -> {
                if (n.getParent() == node) {
                    n.setParent(node.getParent());
                }
            });
        });
        needCut.entrySet().forEach(entry -> {
            cut(entry.getKey(), entry.getValue());
        });
    }

    private void cut(Node node, List<String> words) {
        node.setWord(words.get(0));
        
        //后移node
        this.nodes.values().forEach(n -> {
            if (n.getIndex() > node.getIndex()) {
                n.setIndex(n.getIndex() + words.size() - 1);
            }
        });
        
        List<Map.Entry<Integer, Node>> list = new ArrayList<>(this.nodes.entrySet());
        
        list.sort((o1, o2) -> Integer.compare(o2.getKey(),o1.getKey()));
        
        list.forEach(n -> this.nodes.put(n.getValue().getIndex(),n.getValue()));
        //简单地将需要拆分的词，属性继承被拆分的词。
        for (int i = 1; i < words.size(); i++) {
        	
            Node newNode = new Node();
            newNode.setIndex(node.getIndex() + i);
            newNode.setParent(node.getParent());
            newNode.setRelationship(node.getRelationship());
            newNode.setWord(words.get(i));
            this.nodes.put(newNode.getIndex(), newNode);
        }
    }

    public SyntaxTree(String text) {
        this.text = text;
    }

    public int distance(Node from, Node to) {
        int distance = 0;
        //从to往上找
        if (to.index == from.index) {
            return distance;
        }
        Node temp = to;
        while (temp.index != from.index && temp.index != 0) {
            temp = to.parent;
        }
        if (temp.index != 0) {
            return distance;
        } else {
            //没找到存在两种可能，一是from !-> to && to -> from
            //二是from !-> to && to !-> from
            distance = distance(to, from);
            if (distance >= 0) {
                return distance;
            }
        }
        return -1;
    }

    public void addNode(int index, String relationship, String word) {
        Preconditions.checkArgument(index > 0, "index不能小于0");
        Preconditions.checkNotNull(word, "word不能为null");
        Preconditions.checkArgument(!word.equals(""), "word不能为空");
        //将所有的关系情况进行了删减
        this.nodes.put(index, new Node(index, DependentRelationship.abbreviationOf(relationship), word));
    }
    public void addRelationshipForWhole(int parentIndex, int childIndex) {
        //if (this.nodes.containsKey(parentIndex) && this.nodes.containsKey(childIndex)) {
            this.nodes.get(childIndex).parent = this.nodes.get(parentIndex);
        //}
    }
    public void addRelationship(int parentIndex, int childIndex) {
        if (this.nodes.containsKey(parentIndex) && this.nodes.containsKey(childIndex)) {
            this.nodes.get(childIndex).parent = this.nodes.get(parentIndex);
        }
    }

    public boolean hasNode(int index) {
        return this.nodes.containsKey(index);
    }

    public Iterator<Node> iterator() {
        return this.nodes.values().iterator();
    }

    public class Node {

        private int index;
        private Node parent;
        private DependentRelationship relationship;
        private String word;
        private boolean isMatch;

        public Node getParent() {
            return this.parent;
        }

        Node() {
            this.index = 0;
            this.parent = null;
            this.relationship = null;
            this.word = null;
        }

        Node(int index, DependentRelationship relationship, String word) {
            this.index = index;
            this.relationship = relationship;
            this.word = word;
            this.parent = null;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public void setParent(Node parent) {
            this.parent = parent;
        }

        public DependentRelationship getRelationship() {
            return relationship;
        }

        public void setRelationship(DependentRelationship relationship) {
            this.relationship = relationship;
        }

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public boolean isIsMatch() {
            return isMatch;
        }

        public void setIsMatch(boolean isMatch) {
            this.isMatch = isMatch;
        }

    }

}

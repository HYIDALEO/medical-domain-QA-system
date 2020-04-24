package com.appleyk.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KbResults {
    private List<Map<String,String>> listMap = new ArrayList<>();
    private int index = 0;
    
    public void newReuslt(){
        this.listMap.add(new HashMap<>());
    }
    
    public void add(String key, String value){
        this.listMap.get(this.listMap.size() - 1).put(key, value);
    }
    
    public Map<String, String> next(){
        return this.listMap.get(index++);
    }
    
    public boolean hasNext(){
        return this.index < this.listMap.size() && this.listMap.size() > 0;
    }
}

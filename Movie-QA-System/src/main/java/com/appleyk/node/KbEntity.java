package com.appleyk.node;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


public class KbEntity {
    public String name;
    public String ontology;
    public Set<String> goout = new HashSet<>();
    public Set<String> comeback = new HashSet<>();

    public KbEntity() {
    }

    public KbEntity(String name, String ontology) {
        this.name = name;
        this.ontology = ontology;
    }
    
    public void showKbEntity() {
    	
    	try {
			FileWriter file = new FileWriter("G:\\info.txt",true);
			file.write("ontology:"+this.ontology+",name"+this.name+"\r\n");
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	System.out.println("ontology:"+this.ontology+",name"+this.name);
    	
    }

    public KbEntity(String ontology){
        this.name = ontology;
        this.ontology = ontology;
    }
    
    public boolean isOntology(){
        return this.name.equals(this.ontology);
    }
    
//    public void expand(){
//        String sql = Config.KNOWLEDGE_BASE_PREFIX + "SELECT ?y WHERE { example:"+name+" ?y ?z .}";
//        for(Triad triad : KnowledgeBase.getInstance().executeQuery(sql)){
//            goout.add(triad.values[0].substring(triad.values[0].lastIndexOf("#") + 1));
//        }
//        sql = Config.KNOWLEDGE_BASE_PREFIX + "SELECT ?y WHERE {  ?x ?y example:"+name+" .}";
//        for(Triad triad : KnowledgeBase.getInstance().executeQuery(sql)){
//            comeback.add(triad.values[0].substring(triad.values[0].lastIndexOf("#") + 1));
//        }
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOntology() {
        return ontology;
    }

    public void setOntology(String ontology) {
        this.ontology = ontology;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KbEntity kbEntity = (KbEntity) o;
        return Objects.equals(name, kbEntity.name) &&
                Objects.equals(ontology, kbEntity.ontology);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, ontology);
    }
}

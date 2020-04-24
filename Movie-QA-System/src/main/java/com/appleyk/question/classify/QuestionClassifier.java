package com.appleyk.question.classify;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.appleyk.classfy.Pretreatment;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;

public class QuestionClassifier {
    private static final Logger LOGGER = Logger.getLogger(QuestionClassifier.class.getName());
    private svm_model model;
    private Pretreatment pretreatment;
    
    public void loadModel(String filePath) {
        LOGGER.log(Level.INFO, "load model = {0}", filePath);
        try {
            model = svm.svm_load_model(filePath);
        } catch (IOException ex) {
            Logger.getLogger(QuestionClassifier.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
    }
    public void setPretreatment(Pretreatment pretreatment) {
        this.pretreatment = pretreatment;
    }
    
    public QuestionType classify(String question) {
    	System.out.println(question);
    	List<svm_node> nodes = Pretreatment.getPretreatment().pretreat(question);
    //	List<svm_node> nodes = this.pretreatment.pretreat(question);
        svm_node[] snodes = new svm_node[nodes.size()];
        nodes.toArray(snodes);
        QuestionType type = QuestionType.numberOf((int) svm.svm_predict(this.model, snodes));
        return type;
    }
}

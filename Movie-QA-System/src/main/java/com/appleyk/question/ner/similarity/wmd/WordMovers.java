package com.appleyk.question.ner.similarity.wmd;

import java.lang.reflect.Method;

import com.appleyk.InitElements.QAWordSegmenter;
import com.appleyk.InitElements.QAWordVectors;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.lang.reflect.InvocationTargetException;
//unfinished
public class WordMovers {
	
	private QAWordSegmenter wordSegmenter;
	private QAWordVectors wordVectors;
	private Object emdObj;
	private Method emdMethod;
	
    public WordMovers(QAWordVectors wordVectors,QAWordSegmenter segmenter) {
        this.wordVectors = wordVectors;
        this.wordSegmenter = segmenter;

//        //反射得到emd_hat
//        try {
////            Class cls = Class.forName("com.appleyk.emd.emd_hat");
////            emdObj = cls.newInstance();
////            for(Method method : cls.getMethods()){
////                if("dist_gd_metric".equals(method.getName())){
////                    emdMethod = method;
////                }
////            }
////            System.out.println("----------------------------------------------------------------------");
////            System.out.println(emdMethod);
////            System.out.println("----------------------------------------------------------------------");
//        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
//            e.printStackTrace();
//        }

    }
}

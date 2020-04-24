package com.appleyk.answer;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.appleyk.question.classify.QuestionType;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class AnswerTemplates {
    private final Table<QuestionType, AnswerType, Map<PossibilityType, List<String>>> templates = HashBasedTable.create();
    private static final Random RANDOM = new Random(System.currentTimeMillis());
    

    
    public static void addTemplate(QuestionType questionType, AnswerType answerType, PossibilityType possibilityType, String template) {
        List<String> list = getInstance().templates.get(questionType, answerType).get(possibilityType);
        if (!list.contains(template)) {
            list.add(template);
        }
    }
    
    private static List<String> getTemplates(QuestionType questionType, AnswerType answerType, PossibilityType possibilityType) {
    	return getInstance().templates.get(questionType, answerType).get(possibilityType);
    }
    
    public static String create(QuestionType questionType, AnswerType answerType, PossibilityType possibilityType, String[] args) {
        List<String> templates = getTemplates(questionType, answerType, possibilityType);
        String template = templates.get(RANDOM.nextInt(templates.size()));
        String result = MessageFormat.format(template, (Object[]) args);
        int index = result.indexOf(",{");
        if (index != -1) {
            return result.substring(0, index);
        }
        return result;
    }
    

    private static final AnswerTemplates INSTANCE=new AnswerTemplates();
    private AnswerTemplates() {
        for (QuestionType questionType : QuestionType.values()) {
            for (AnswerType answerType : AnswerType.values()) {
                this.templates.put(questionType, answerType, new HashMap<>());
                for (PossibilityType possibilityType : PossibilityType.values()) {
      //          	System.out.println("questionType"+questionType);
        //        	System.out.println("answerType"+answerType);
          //      	System.out.println("possibilityType"+possibilityType);
                    this.templates.get(questionType, answerType).put(possibilityType, new ArrayList<>());
                }
            }
        }

    }
    

    
    private static AnswerTemplates getInstance() {
        return INSTANCE;
    }
}

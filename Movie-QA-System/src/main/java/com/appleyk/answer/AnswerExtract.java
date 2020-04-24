package com.appleyk.answer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import com.appleyk.answer.Graph.ShortestDistance;
import com.appleyk.node.KbEntity;
import com.appleyk.question.classify.CandidateAnswerChain;
import com.appleyk.question.classify.QuestionType;



public class AnswerExtract {

    /**
     * 答案抽取模块 输入：候选答案链 输出：自然语言形式答案句
     *
     * 问句根据问句类型可分：事实类、是否类、列表类、数值类、计数类
     *
     * 答案变量分为：药品、症状、疾病
     *
     * 事实类：找到答案实体，抽取候选答案链中可能性最大的答案实体
     *
     * 是否类：判断候选答案链中可能性最大的那一条链的路径是否全部可达
     *
     * 列表类：找到答案实体，抽取候选答案链中前面同一种可能性的所有答案实体
     *
     * 数值类：找到答案实体，比较候选答案链中前面同一种可能性的所有答案实体
     *
     * 计数类：找到答案实体，计算候选答案链中前面同一种可能性的所有答案实体的个数
     *
     * 在本系统中，可能性所有已知实体到答案实体之间的距离表示
     *
     */
    private final Random random = new Random(System.currentTimeMillis());

    private static final Logger LOGGER = Logger.getLogger(AnswerExtract.class.getName());

    public String extract(List<CandidateAnswerChain> chains, QuestionType questionType, AnswerType answerType) {
        Collections.sort(chains);
        switch (questionType) {
            case FACTOR:{
                ShortestDistance distance = chains.get(0).getPossibility();
                PossibilityType possibilityType;
                if (distance.unreachable_count == 0) {
                    possibilityType = PossibilityType.SURE;
                } else if (distance.distance == 0) {
                    possibilityType = PossibilityType.MAY;
                } else {
                    possibilityType = PossibilityType.CERTAINLY_NOT;
                }
                return AnswerTemplates.create(questionType, answerType, possibilityType, new String[]{chains.get(0).getAnswer().name});
            }
            case BOOL: {
                ShortestDistance distance = chains.get(0).getPossibility();
                PossibilityType possibilityType;
                if (distance.unreachable_count == 0) {
                    possibilityType = PossibilityType.SURE;
                } else if (distance.distance != 0) {
                    possibilityType = PossibilityType.MAY;
                } else {
                    possibilityType = PossibilityType.CERTAINLY_NOT;
                }
                return AnswerTemplates.create(questionType, AnswerType.DISEASE, possibilityType,null);
            }
            case LIST: {
                List<String> answers = new ArrayList<>();
                ShortestDistance distance = chains.get(0).getPossibility();
                answers.add(chains.get(0).getAnswer().name);
                int size = chains.size();
                for (int i = 1; i < size; i++) {
                    if (chains.get(i).getPossibility().compareTo(distance) == 0) {
                        answers.add(chains.get(i).getAnswer().name);
                    } else {
                        break;
                    }
                }
                if (!answers.isEmpty()) {
                    String[] as = new String[answers.size()];
                    answers.toArray(as);
                    PossibilityType possibilityType;
                    if (distance.unreachable_count == 0) {
                        possibilityType = PossibilityType.SURE;
                    } else if (distance.distance == 0) {
                        possibilityType = PossibilityType.MAY;
                    } else {
                        possibilityType = PossibilityType.CERTAINLY_NOT;
                    }
                    return AnswerTemplates.create(questionType, answerType, possibilityType, as);
                } else {
                    return AnswerTemplates.create(questionType, answerType, PossibilityType.CERTAINLY_NOT, null);
                }
            }
            case NUMBER_MAX: {
                List<CandidateAnswerChain> cs = new ArrayList<>();
                for (CandidateAnswerChain chain : chains) {
                    if (chain.getPossibility().unreachable_count == 0) {
                        cs.add(chain);
                    } else {
                        break;
                    }
                }
                if (!cs.isEmpty()) {
                    double max = 0.0;
                    CandidateAnswerChain maxC = null;
                    for (CandidateAnswerChain c : cs) {
                        for (KbEntity entity : c.getEntities()) {
                            try {
                                double current = Double.parseDouble(entity.getName());
                                if (current > max) {
                                    max = current;
                                    maxC = c;
                                }
                            } catch (NumberFormatException e) {
                            }
                        }
                    }
                    if (null != maxC) {
                        return AnswerTemplates.create(questionType, answerType, PossibilityType.SURE, new String[]{maxC.getAnswer().name});
                    }
                }
                return AnswerTemplates.create(questionType, answerType, PossibilityType.CERTAINLY_NOT, null);
            }
            case NUMBER_MIN: {
                List<CandidateAnswerChain> cs = new ArrayList<>();
                for (CandidateAnswerChain chain : chains) {
                    if (chain.getPossibility().unreachable_count == 0) {
                        cs.add(chain);
                    } else {
                        break;
                    }
                }
                if (!cs.isEmpty()) {
                    double min = Double.MIN_VALUE;
                    CandidateAnswerChain minC = null;
                    for (CandidateAnswerChain c : cs) {
                        for (KbEntity entity : c.getEntities()) {
                            try {
                                double current = Double.parseDouble(entity.getName());
                                if (current < min) {
                                    min = current;
                                    minC = c;
                                }
                            } catch (NumberFormatException e) {
                            }
                        }
                    }
                    if (null != minC) {
                        return AnswerTemplates.create(questionType, answerType, PossibilityType.SURE, new String[]{minC.getAnswer().name});
                    }
                }
                return AnswerTemplates.create(questionType, answerType, PossibilityType.CERTAINLY_NOT, null);
            }
            case COUNT: {
                List<String> answers = new ArrayList<>();
                for (CandidateAnswerChain chain : chains) {
                    if (chain.getPossibility().unreachable_count == 0) {
                        answers.add(chain.getAnswer().getName());
                    } else {
                        break;
                    }
                }
                if (!answers.isEmpty()) {
                    return AnswerTemplates.create(questionType, answerType, PossibilityType.SURE, new String[]{answers.size()+""});
                } else {
                    return AnswerTemplates.create(questionType, answerType, PossibilityType.CERTAINLY_NOT, null);
                }
            }
        }
        return null;
    }
}

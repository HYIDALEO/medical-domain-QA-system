package com.appleyk.question.classify;

import java.util.Set;

import com.appleyk.answer.Graph.ShortestDistance;
import com.appleyk.node.KbEntity;



public class CandidateAnswerChain implements Comparable<CandidateAnswerChain>{
    private ShortestDistance possibility;
    private KbEntity answer;
    private Set<KbEntity> entities;

    public ShortestDistance getPossibility() {
        return possibility;
    }

    public KbEntity getAnswer() {
        return answer;
    }

    public Set<KbEntity> getEntities() {
        return entities;
    }

    public void setPossibility(ShortestDistance possibility) {
        this.possibility = possibility;
    }

    public void setAnswer(KbEntity answer) {
        this.answer = answer;
    }

    public void setEntities(Set<KbEntity> entities) {
        this.entities = entities;
    }

    @Override
    public int compareTo(CandidateAnswerChain o) {
        return this.possibility.compareTo(o.possibility);
    }
}

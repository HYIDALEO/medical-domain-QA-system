package com.appleyk.answer;



public enum AnswerType {

    DRUG("药品"),
    DISEASE("疾病"),
    SYMPTOM("症状");

    private String name;

    private AnswerType(String name) {
        this.name = name;
    }

    public static AnswerType nameOf(String name) {
        for (AnswerType type : values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        return null;
    }
}
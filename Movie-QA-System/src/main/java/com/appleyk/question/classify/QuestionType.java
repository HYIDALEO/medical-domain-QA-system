package com.appleyk.question.classify;


public enum QuestionType {

    FACTOR(1),
    BOOL(2),
    NUMBER_MAX(3),
    NUMBER_MIN(4),
    LIST(5),
    COUNT(6),
    EXPLAIN(7);

    private int number;

    private QuestionType(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public static QuestionType numberOf(int number) {
        for (QuestionType value : values()) {
            if (value.getNumber() == number) {
                return value;
            }
        }
        return null;
    }

}
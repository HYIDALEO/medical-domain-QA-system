package com.appleyk.question.classify;


public enum DependentRelationship {

    ATT("ATT",1),
    QUN("QUN",2),
    COO("COO",3),
    APP("APP",4),
    ADJ("ADJ",5),
    VOB("VOB",6),
    POB("POB",7),
    SBV("SBV",8),
    SIM("SIM",9),
    TMP("TMP",10),
    OTHER("其他",11);

    String abbreviation;
    int num;

    public static DependentRelationship abbreviationOf(String abbreviation){
        for (DependentRelationship relationship :
                DependentRelationship.values()) {
            if (relationship.abbreviation.equals(abbreviation)) {
                return relationship;
            }
        }
        return OTHER;
    }

    public static DependentRelationship numOf(int num){
        for (DependentRelationship relationship :
                DependentRelationship.values()) {
            if (relationship.num == num) {
                return relationship;
            }
        }
        return OTHER;
    }

    DependentRelationship(String abbreviation, int num) {
        this.abbreviation = abbreviation;
        this.num = num;
    }
}
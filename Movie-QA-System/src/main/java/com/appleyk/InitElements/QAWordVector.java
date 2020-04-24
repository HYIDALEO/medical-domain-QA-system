package com.appleyk.InitElements;


public class QAWordVector {
    public double[] value;

    public double distance2(QAWordVector vector) {
        return cosineDistance(this.value, vector.value);
    }
    
    public double[] getVector() {
    	return this.value;
    }

    private double cosineDistance(double[] value1, double[] value2) {
        double fm1 = 0;
        double fm2 = 0;
        double fz = 0;
        for (int i = 0; i < value1.length; i++) {
            fz += value1[i] * value2[i];
            fm1 += value1[i] * value1[i];
            fm2 += value2[i] * value2[i];
        }
        double result = 1.0 - fz / (Math.sqrt(fm1) * (Math.sqrt(fm2)));
        return result > 0 ? result : 0;
    }
}

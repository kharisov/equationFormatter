package org.stepik.is2018.kharisovds;

import java.util.ArrayList;
import java.util.Comparator;

class Term {
    private Sign sign = Sign.POSITIVE;
    private int coef = 1;
    private ArrayList<Factor> factors = new ArrayList<>();

    Term() {
    }

    Term(Factor factor) {
        factors.add(factor);
    }

    Term(ArrayList<Factor> factors) {
        this.factors = factors;
    }

    void setSign(Sign sign) {
        this.sign = sign;
    }

    void addFactor(Factor factor) {
        factors.add(factor);
    }

    void setFactors(ArrayList<Factor> factors) {
        this.factors = factors;
    }

    Sign getSign() {
        return sign;
    }

    void setCoef(int coef) {
        this.coef = coef;
    }

    int getCoef() {
        return coef;
    }

    ArrayList<Factor> getFactors() {
        return factors;
    }

    void sortFactors(Comparator<Factor> comparator) {
        factors.sort(comparator);
    }
}

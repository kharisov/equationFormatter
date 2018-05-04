package org.stepik.is2018.kharisovds;

import java.util.ArrayList;

class ComplexFactor extends Factor {
    private ArrayList<Term> terms = new ArrayList<>();

    ComplexFactor(ArrayList<Term> terms) {
        this.terms = terms;
    }

    ArrayList<Term> getTerms() {
        return terms;
    }

    void setTerms(ArrayList<Term> terms) {
        this.terms = terms;
    }
}

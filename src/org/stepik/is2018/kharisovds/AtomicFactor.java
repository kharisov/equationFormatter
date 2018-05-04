package org.stepik.is2018.kharisovds;

class AtomicFactor extends Factor {
    private String var;

    AtomicFactor(String var) {
        this.var = var;
    }

    String getVar() {
        return var;
    }
}

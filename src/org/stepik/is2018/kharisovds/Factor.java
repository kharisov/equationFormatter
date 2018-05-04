package org.stepik.is2018.kharisovds;

abstract class Factor {
    private int exponent = 1;

    void setExponent(int exponent) {
        this.exponent = exponent;
    }

    int getExponent() {
        return exponent;
    }
}

package org.stepik.is2018.kharisovds;

class Lexeme {
    private LexemeType type;
    private String lexemeText;

    Lexeme(LexemeType type, String lexemeText) {
        this.type = type;
        this.lexemeText = lexemeText;
    }

    LexemeType getType() {
        return type;
    }

    String getLexemeText() {
        return lexemeText;
    }
}

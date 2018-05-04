package org.stepik.is2018.kharisovds;

import java.io.IOException;
import java.io.Reader;

class Lexer {
    private Reader reader;
    private int currentChar;

    Lexer(Reader reader) throws IOException {
        this.reader = reader;
        readChar();
    }

    private void readChar() throws IOException {
        currentChar = reader.read();
        while (Character.isWhitespace((char)currentChar))
            currentChar = reader.read();
    }

    Lexeme getLexeme() throws IOException, WrongSyntaxException {
        Lexeme lexeme;
        if (currentChar == '+') {
            lexeme = new Lexeme(LexemeType.PLUS, "+");
            readChar();
        } else if (currentChar == '-') {
            lexeme = new Lexeme(LexemeType.MINUS, "-");
            readChar();
        } else if (currentChar == '^') {
            lexeme = new Lexeme(LexemeType.CARET, "^");
            readChar();
        } else if (currentChar == '(') {
            lexeme = new Lexeme(LexemeType.LEFT_PAREN, "(");
            readChar();
        } else if (currentChar == ')') {
            lexeme = new Lexeme(LexemeType.RIGHT_PAREN, ")");
            readChar();
        } else if (currentChar >= '0' && currentChar <= '9') {
            StringBuilder builder = new StringBuilder();
            builder.append((char)currentChar);
            currentChar = reader.read();
            while (currentChar >= '0' && currentChar <= '9') {
                builder.append((char)currentChar);
                currentChar = reader.read();
            }
            if (Character.isWhitespace((char)currentChar))
                readChar();
            if (currentChar >= '0' && currentChar <= '9')
                throw new WrongSyntaxException("Two numbers separated by whitespace are forbidden");
            lexeme = new Lexeme(LexemeType.NUMBER, builder.toString());
        } else if (currentChar >= 'a' && currentChar <= 'z') {
            lexeme = new Lexeme(LexemeType.LATIN_LOWER_CHAR, Character.toString((char)currentChar));
            currentChar = reader.read();
            if (Character.isWhitespace((char)currentChar))
                readChar();
        } else if (currentChar == -1) {
            lexeme = new Lexeme(LexemeType.EOF, null);
        } else {
            throw new WrongSyntaxException("Wrong char: " + (char)currentChar);
        }
        return lexeme;
    }
}
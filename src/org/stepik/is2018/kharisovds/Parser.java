package org.stepik.is2018.kharisovds;

import java.io.IOException;
import java.util.ArrayList;

class Parser {
    private Lexer lexer;
    private Lexeme currentLexeme;

    Parser(Lexer lexer) throws IOException, WrongSyntaxException {
        this.lexer = lexer;
        currentLexeme = lexer.getLexeme();
    }

    ArrayList<Term> parse() throws IOException, WrongSyntaxException {
        ArrayList<Term> canonicalForm = parseEquation();
        if (currentLexeme.getType() == LexemeType.EOF)
            return canonicalForm;
        else
            throw new WrongSyntaxException("Wrong syntax: no EOF");
    }

    private ArrayList<Term> parseEquation() throws IOException, WrongSyntaxException {
        ArrayList<Term> parsedEquation = new ArrayList<>();
        Sign sign = Sign.POSITIVE;
        if (currentLexeme.getType() == LexemeType.MINUS) {
            currentLexeme = lexer.getLexeme();
            sign = Sign.NEGATIVE;
        }
        Term term = parseTerm();
        term.setSign(sign);
        parsedEquation.add(term);
        while (currentLexeme.getType() == LexemeType.PLUS || currentLexeme.getType() == LexemeType.MINUS) {
            if (currentLexeme.getType() == LexemeType.PLUS)
                sign = Sign.POSITIVE;
            else
                sign = Sign.NEGATIVE;
            currentLexeme = lexer.getLexeme();
            term = parseTerm();
            term.setSign(sign);
            parsedEquation.add(term);
        }
        return parsedEquation;
    }

    private Term parseTerm() throws IOException, WrongSyntaxException {
        Factor factor = parseFactor();
        Term term = new Term(factor);
        while (currentLexeme.getType() == LexemeType.NUMBER || currentLexeme.getType() == LexemeType.LATIN_LOWER_CHAR
                || currentLexeme.getType() == LexemeType.LEFT_PAREN) {
            factor = parseFactor();
            term.addFactor(factor);
        }
        return term;
    }

    private Factor parseFactor() throws IOException, NumberFormatException, WrongSyntaxException {
        Factor factor = parseBase();
        if (currentLexeme.getType() == LexemeType.CARET) {
            currentLexeme = lexer.getLexeme();
            factor.setExponent(parseExponent());
            if (factor instanceof CoefFactor)
                ((CoefFactor) factor).setCoef((int) Math.pow(((CoefFactor) factor).getCoef(), factor.getExponent()));
        }
        return factor;
    }

    private Factor parseBase() throws IOException, WrongSyntaxException {
        Factor factor;
        if (currentLexeme.getType() == LexemeType.NUMBER) {
            try {
                factor = new CoefFactor(Integer.parseInt(currentLexeme.getLexemeText()));
            } catch (NumberFormatException n) {
                throw new WrongSyntaxException("Wrong coefficient: " + currentLexeme.getLexemeText());
            }
            currentLexeme = lexer.getLexeme();
        } else if (currentLexeme.getType() == LexemeType.LATIN_LOWER_CHAR) {
            factor = new AtomicFactor(currentLexeme.getLexemeText());
            currentLexeme = lexer.getLexeme();
        } else if (currentLexeme.getType() == LexemeType.LEFT_PAREN) {
            currentLexeme = lexer.getLexeme();
            factor = new ComplexFactor(parseEquation());
            if (currentLexeme.getType() == LexemeType.RIGHT_PAREN) {
                currentLexeme = lexer.getLexeme();
            } else
                throw new WrongSyntaxException("No closing parenthesis");
        } else {
            throw new WrongSyntaxException("Wrong symbol in factor");
        }
        return factor;
    }

    private int parseExponent() throws IOException, WrongSyntaxException {
        try {
            int exponent = Integer.parseInt(currentLexeme.getLexemeText());
            currentLexeme = lexer.getLexeme();
            return exponent;
        } catch (NumberFormatException n) {
            throw new WrongSyntaxException("Wrong exponent: " + currentLexeme.getLexemeText());
        }
    }
}
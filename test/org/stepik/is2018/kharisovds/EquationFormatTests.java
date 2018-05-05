package org.stepik.is2018.kharisovds;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import java.io.StringReader;

class EquationFormatTests {

    @Test //basic examples from task description
    void basicTest() {
        try {
            assertEquals("a", EquationFormat.canonicalForm(new StringReader("a")));
            assertEquals("-ab", EquationFormat.canonicalForm(new StringReader("-ab")));
            assertEquals("-6ab", EquationFormat.canonicalForm(new StringReader("-2a3b")));
            assertEquals("a^2+b^2-c^2", EquationFormat.canonicalForm(new StringReader("a^2+b^2-c^2")));
            assertEquals("abd^2+2abde^2+2abdf^9+abe^4+2abe^2f^9+" +
                            "abf^18+acd^2+2acde^2+2acdf^9+ace^4+2ace^2f^9+acf^18",
                    EquationFormat.canonicalForm(new StringReader("a(b+c)(  d+ e    ^2+(f^9))^2")));
            assertEquals("23e+23f", EquationFormat.canonicalForm(new StringReader("(e+f)23")));

            assertThrows(WrongSyntaxException.class, () -> EquationFormat.canonicalForm(new StringReader("(a+b+c")));
            assertThrows(WrongSyntaxException.class, () -> EquationFormat.canonicalForm(new StringReader("+a")));
            assertThrows(WrongSyntaxException.class, () -> EquationFormat.canonicalForm(new StringReader("(a+b)^(2+2)")));
            assertThrows(WrongSyntaxException.class, () -> EquationFormat.canonicalForm(new StringReader("abcD")));
            assertThrows(WrongSyntaxException.class, () -> EquationFormat.canonicalForm(new StringReader("(a+b+)")));
        } catch (Exception e) {
            System.err.println("Exception in unexpected place during test execution");
            e.printStackTrace();
        }
    }

    @Test
    void numberTest() {
        try {
            assertEquals("a+4", EquationFormat.canonicalForm(new StringReader("2 + a + 2")));
            assertEquals("4", EquationFormat.canonicalForm(new StringReader("(2)2")));
            assertEquals("b", EquationFormat.canonicalForm(new StringReader("0a + 1b + 0^2")));
            assertEquals("a+b", EquationFormat.canonicalForm(new StringReader("a^0(a+b)")));
            assertEquals("a+ab", EquationFormat.canonicalForm(new StringReader("a(a^0+b)")));
            assertEquals("72", EquationFormat.canonicalForm(new StringReader("2^3+4^3")));

            assertThrows(WrongSyntaxException.class, () -> EquationFormat.canonicalForm(new StringReader("2 2")));
            assertThrows(WrongSyntaxException.class, () -> EquationFormat.canonicalForm(new StringReader("a^2 2")));
            assertThrows(WrongSyntaxException.class, () -> EquationFormat.canonicalForm(new StringReader("2.1a")));
        } catch (Exception e) {
            System.err.println("Exception in unexpected place during test execution");
            e.printStackTrace();
        }
    }

    @Test
    void exponentTest() {
        try {
            assertEquals("a^3", EquationFormat.canonicalForm(new StringReader("aaa")));
            assertEquals("a^2+2ab+b^2", EquationFormat.canonicalForm(new StringReader("(a+b)^2")));
            assertEquals("a^2-2ab+b^2", EquationFormat.canonicalForm(new StringReader("(a-b)^2")));
            assertEquals("a^3+3a^2b+3ab^2+b^3", EquationFormat.canonicalForm(new StringReader("(a+b)^3")));
            assertEquals("a^3-3a^2b+3ab^2-b^3", EquationFormat.canonicalForm(new StringReader("(a-b)^3")));
            assertEquals("a^32", EquationFormat.canonicalForm(new StringReader("a^16a^16")));
            assertEquals("ac", EquationFormat.canonicalForm(new StringReader("ab^0c")));
            assertEquals("b+1", EquationFormat.canonicalForm(new StringReader("b + 0^0")));
            assertEquals("b+1", EquationFormat.canonicalForm(new StringReader("b + a^0")));
            assertEquals("b-1", EquationFormat.canonicalForm(new StringReader("b - 0^0")));
            assertEquals("b-1", EquationFormat.canonicalForm(new StringReader("b - a^0")));

            assertThrows(WrongSyntaxException.class, () -> EquationFormat.canonicalForm(new StringReader("a^(-1)")));
            assertThrows(WrongSyntaxException.class, () -> EquationFormat.canonicalForm(new StringReader("a^b")));
            assertThrows(ExponentTooHighException.class, () -> EquationFormat.canonicalForm(new StringReader("a^16b^17")));
        } catch (Exception e) {
            System.err.println("Exception in unexpected place during test execution");
            e.printStackTrace();
        }
    }

    @Test
    void parenthesesTest() {
        try {
            assertEquals("a^2+2ab^2+b^4", EquationFormat.canonicalForm(new StringReader("((a)+(b)^2)^2")));

            assertThrows(WrongSyntaxException.class, () -> EquationFormat.canonicalForm(new StringReader("(a")));
        } catch (Exception e) {
            System.err.println("Exception in unexpected place during test execution");
            e.printStackTrace();
        }
    }

    @Test
    void monomialTest() {
        try {
            assertEquals("abc", EquationFormat.canonicalForm(new StringReader("bca")));
            assertEquals("2abc", EquationFormat.canonicalForm(new StringReader("bca2")));
            assertEquals("2ab", EquationFormat.canonicalForm(new StringReader("ab + ab")));
            assertEquals("0", EquationFormat.canonicalForm(new StringReader("ab - ab")));
            assertEquals("-ab", EquationFormat.canonicalForm(new StringReader("ab - a2b")));
            assertEquals("a+b", EquationFormat.canonicalForm(new StringReader("b + a")));
            assertEquals("ab+ac", EquationFormat.canonicalForm(new StringReader("ac + ab")));
            assertEquals("ab^2+ab", EquationFormat.canonicalForm(new StringReader("ab + abb")));
            assertEquals("ab+abc", EquationFormat.canonicalForm(new StringReader("abc + ab")));
            assertEquals("ab", EquationFormat.canonicalForm(new StringReader("ab + ac - ac")));

            assertThrows(WrongSyntaxException.class, () -> EquationFormat.canonicalForm(new StringReader("a+-b")));
            assertThrows(WrongSyntaxException.class, () -> EquationFormat.canonicalForm(new StringReader("+a")));
            assertThrows(WrongSyntaxException.class, () -> EquationFormat.canonicalForm(new StringReader("a+B")));
        } catch (Exception e) {
            System.err.println("Exception in unexpected place during test execution");
            e.printStackTrace();
        }
    }

    @Test
    void whitespaceTest() {
        try {
            assertEquals("a^2+2ab+b^2", EquationFormat.canonicalForm(new StringReader("(    a +\nb  )\n^\n2\n")));
        } catch (Exception e) {
            System.err.println("Exception in unexpected place during test execution");
            e.printStackTrace();
        }
    }

    @Test //tricky tests gotten kindly given by Julia Zhuk
    void trickyTest() {
        try {
            assertEquals("-2a", EquationFormat.canonicalForm(new StringReader("a(-2)")));
            assertEquals("2a", EquationFormat.canonicalForm(new StringReader("a(-2)(-1)")));
            assertEquals("4a", EquationFormat.canonicalForm(new StringReader("a(-2)^2")));
            assertEquals("-4a", EquationFormat.canonicalForm(new StringReader("(-a)(-2)^2")));
            assertEquals("-8a", EquationFormat.canonicalForm(new StringReader("a(-2)^3")));
            assertEquals("a", EquationFormat.canonicalForm(new StringReader("a(-2)^0")));
            assertEquals("1", EquationFormat.canonicalForm(new StringReader("(-2)^0")));
            assertEquals("1", EquationFormat.canonicalForm(new StringReader("(a+b+d+c+t)^0")));

            assertEquals("a^32", EquationFormat.canonicalForm(new StringReader("(((((a^2)^2)^2)^2)^2)")));
            assertThrows(ExponentTooHighException.class, () -> EquationFormat.canonicalForm(new StringReader("a^16b^17")));
            assertThrows(ExponentTooHighException.class, () -> EquationFormat.canonicalForm(new StringReader("aaaabbbbnnnnhhhhkkkkiiiiiiiiiiiiiii")));
            assertThrows(ExponentTooHighException.class, () -> EquationFormat.canonicalForm(new StringReader("(a^14+c)(a^20+d)")));
            assertThrows(ExponentTooHighException.class, () -> EquationFormat.canonicalForm(new StringReader("((a^2))^19")));
            assertThrows(ExponentTooHighException.class, () -> EquationFormat.canonicalForm(new StringReader("(((((a^2)^2)^2)^2)^2)^2")));

            assertEquals("ab", EquationFormat.canonicalForm(new StringReader("ba")));
            assertEquals("abcdefghijklmnopqrstuvwxyz", EquationFormat.canonicalForm(new StringReader("zyxwvutsrqponmlkjihgfebcda")));
            assertEquals("a+b+c+d+e+f+g+h+i+j+k+l+m+n+o+p+q+r+s+t+u+v+w+x+y+z", EquationFormat.canonicalForm(new StringReader("z+y+x+w+v+u+t+s+r+q+p+o+n+m+l+k+j+i+h+g+f+e+b+c+d+a")));
            assertEquals("a^5+a^4+a^3+a^2+a", EquationFormat.canonicalForm(new StringReader("a+aa+aaa+aaaa+aaaaa")));
            assertEquals("a^15z+a^5f+a^4e+a^3d+a^2c+ab", EquationFormat.canonicalForm(new StringReader("ab+a^2c+a^3d+a^4e+a^5f+a^15z")));
            assertEquals("a^11+z^32", EquationFormat.canonicalForm(new StringReader("z^32+a^11")));
        } catch (Exception e) {
            System.err.println("Exception in unexpected place during test execution");
            e.printStackTrace();
        }
    }
}

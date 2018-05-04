package org.stepik.is2018.kharisovds;

import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {
        try (InputStreamReader in = new InputStreamReader(System.in)) {
            System.out.print(EquationFormat.canonicalForm(in));
        } catch (WrongSyntaxException w) {
            System.out.print("ERROR: INVALID");
        } catch (ExponentTooHighException e) {
            System.out.print("ERROR: TOO BIG");
        }
        catch (IOException i) {
            i.printStackTrace();
        }
    }
}

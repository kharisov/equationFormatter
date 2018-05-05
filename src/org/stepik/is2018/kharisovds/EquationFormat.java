package org.stepik.is2018.kharisovds;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

abstract class EquationFormat {
    static final int MAX_EXPONENT = 32;

    private static Factor findFactor(Factor factor, ArrayList<Factor> list) {
        for (Factor f : list) {
            if (factor instanceof CoefFactor && f instanceof CoefFactor) {
                return f;
            }
            if (factor instanceof AtomicFactor && f instanceof AtomicFactor) {
                if (((AtomicFactor) factor).getVar().equals(((AtomicFactor) f).getVar()))
                    return f;
            }
        }
        return null;
    }

    private static Term multTerms(Term t1, Term t2) {
        ArrayList<Factor> resultFactors = new ArrayList<>();
        for (Factor f : t1.getFactors()) {
            if (f instanceof CoefFactor) {
                CoefFactor cf = new CoefFactor(((CoefFactor) f).getCoef());
                resultFactors.add(cf);
            } else if (f instanceof AtomicFactor) {
                AtomicFactor af = new AtomicFactor(((AtomicFactor) f).getVar());
                af.setExponent(f.getExponent());
                resultFactors.add(af);
            }
        }
        for (Factor f : t2.getFactors()) {
            Factor searchResult = findFactor(f, resultFactors);
            if (searchResult != null) {
                if (searchResult instanceof CoefFactor)
                    ((CoefFactor) searchResult).setCoef(((CoefFactor) searchResult).getCoef() * ((CoefFactor) f).getCoef());
                else if (searchResult instanceof AtomicFactor)
                    searchResult.setExponent(searchResult.getExponent() + f.getExponent());
            } else {
                resultFactors.add(f);
            }
        }
        Term result = new Term(resultFactors);
        result.setSign(multSigns(t1.getSign(), t2.getSign()));
        result.setCoef(t1.getCoef() * t2.getCoef());
        return result;
    }

    private static ComplexFactor complexFactorPow(ComplexFactor factor, int pow) {
        ArrayList<Term> terms = simplifyTerms(factor.getTerms());
        ArrayList<Term> result = new ArrayList<>(terms);
        for (int i = 0; i < pow - 1; i++) {
            ArrayList<Term> tmp = new ArrayList<>();
            for (Term r : result) {
                for (Term t : terms) {
                    tmp.add(multTerms(r, t));
                }
            }
            result = tmp;
        }
        if (pow == 0) {
            result.clear();
            result.add(new Term());
        }
        return new ComplexFactor(result);
    }

    private static Sign multSigns(Sign s1, Sign s2) {
        if (s1 == s2)
            return Sign.POSITIVE;
        else
            return Sign.NEGATIVE;
    }

    private static ArrayList<Term> openBracketsAndReduce(ArrayList<Factor> factors) {
        ArrayList<Term> result = new ArrayList<>();
        for (Factor f : factors) {
            if (f instanceof CoefFactor) {
                if (result.isEmpty()) {
                    Term t = new Term();
                    t.setCoef(((CoefFactor) f).getCoef());
                    result.add(t);
                } else {
                    for (Term t : result) {
                        t.setCoef(t.getCoef() * ((CoefFactor) f).getCoef());
                    }
                }
            } else if (f instanceof AtomicFactor) {
                Term t = new Term(f);
                if (result.isEmpty()) {
                    if (f.getExponent() != 0)
                        result.add(t);
                    else
                        result.add(new Term());
                } else {
                    if (f.getExponent() == 0)
                        continue;
                    for (Term r : result) {
                        Term tmpTerm = multTerms(t, r);
                        r.setFactors(tmpTerm.getFactors());
                        r.setSign(tmpTerm.getSign());
                        r.setCoef(tmpTerm.getCoef());
                    }
                }
            } else if (f instanceof ComplexFactor) {
                if (result.isEmpty()) {
                    result.addAll(((ComplexFactor) f).getTerms());
                } else {
                    ArrayList<Term> tmp = new ArrayList<>();
                    for (Term r : result) {
                        for (Term t : ((ComplexFactor) f).getTerms()) {
                            Term tmpTerm = multTerms(r, t);
                            tmp.add(tmpTerm);
                        }
                    }
                    result = tmp;
                }
            }
        }
        return result;
    }

    private static ArrayList<Term> simplifyTerms(ArrayList<Term> terms) {
        ArrayList<Term> result = new ArrayList<>();
        for (Term t : terms) {
            for (Factor f : t.getFactors()) {
                if (f instanceof ComplexFactor) {
                    ((ComplexFactor) f).setTerms(complexFactorPow((ComplexFactor) f, f.getExponent()).getTerms());
                }
            }
            ArrayList<Term> opened = openBracketsAndReduce(t.getFactors());
            for (Term o : opened)
                o.setSign(multSigns(o.getSign(), t.getSign()));
            result.addAll(opened);
        }
        return result;
    }

    //terms should be sorted
    private static void sumSimilars(ArrayList<Term> terms) {
        TermComparator comparator = new TermComparator();
        Iterator<Term> it = terms.iterator();
        Term accumulator = null;
        while (it.hasNext()) {
            Term t = it.next();
            if (accumulator == null) {
                accumulator = t;
            } else {
                if (comparator.compare(accumulator, t) == 0) {
                    if (accumulator.getSign() == t.getSign()) {
                        accumulator.setCoef(accumulator.getCoef() + t.getCoef());
                    } else {
                        int coef;
                        if (accumulator.getSign() == Sign.POSITIVE && t.getSign() == Sign.NEGATIVE)
                            coef = accumulator.getCoef() - t.getCoef();
                        else
                            coef = t.getCoef() - accumulator.getCoef();
                        if (coef >= 0)
                            accumulator.setSign(Sign.POSITIVE);
                        else
                            accumulator.setSign(Sign.NEGATIVE);
                        accumulator.setCoef(Math.abs(coef));
                    }
                    it.remove();
                } else {
                    accumulator = t;
                }
            }
        }
    }

    private static String termsToString(ArrayList<Term> terms) throws ExponentTooHighException {
        StringBuilder builder = new StringBuilder();
        for (Term t : terms) {
            int sumExponent = 0;
            if (t.getCoef() == 0)
                continue;
            if (t.getSign() == Sign.NEGATIVE)
                builder.append("-");
            else if (builder.length() != 0) {
                builder.append("+");
            }
            if (t.getCoef() != 1)
                builder.append(t.getCoef());
            for (Factor f : t.getFactors()) {
                if (f.getExponent() == 0)
                    continue;
                builder.append(((AtomicFactor) f).getVar());
                if (f.getExponent() != 1)
                    builder.append("^").append(f.getExponent());
                sumExponent += f.getExponent();
            }
            if (t.getCoef() == 1 && sumExponent == 0)
                builder.append(1);
            if (sumExponent > MAX_EXPONENT)
                throw new ExponentTooHighException("Exponent too high: " + sumExponent);
        }
        if (builder.length() == 0)
            builder.append(0);
        return builder.toString();
    }

    static String canonicalForm(Reader in) throws IOException, WrongSyntaxException, ExponentTooHighException {
        Parser parser = new Parser(new Lexer(in));
        ArrayList<Term> terms = parser.parse();
        terms = simplifyTerms(terms);
        for (Term t : terms)
            t.sortFactors(new FactorCamparator());
        terms.sort(new TermComparator());
        sumSimilars(terms);
        return termsToString(terms);
    }

    //only compare atomic factors, cause we use it when every other type of factors is simplified
    static class FactorCamparator implements Comparator<Factor> {
        @Override
        public int compare(Factor o1, Factor o2) {
            if (o1 instanceof AtomicFactor && o2 instanceof AtomicFactor) {
                return ((AtomicFactor) o1).getVar().compareTo(((AtomicFactor) o2).getVar());
            } else {
                return 0;
            }
        }
    }

    //only compare atomic factors, cause we use it when every other type of factors is simplified
    static class TermComparator implements Comparator<Term> {
        @Override
        public int compare(Term o1, Term o2) {
            Iterator<Factor> it1 = o1.getFactors().iterator();
            Iterator<Factor> it2 = o2.getFactors().iterator();
            while (it1.hasNext() && it2.hasNext()) {
                AtomicFactor f1 = (AtomicFactor) it1.next();
                AtomicFactor f2 = (AtomicFactor) it2.next();
                if (f1.getVar().compareTo(f2.getVar()) != 0) {
                    return f1.getVar().compareTo(f2.getVar());
                } else {
                    if (Integer.compare(f1.getExponent(), f2.getExponent()) != 0) {
                        return -Integer.compare(f1.getExponent(), f2.getExponent());
                    }
                }
            }
            if (o1.getFactors().size() == 0 && o2.getFactors().size() != 0)
                return 1;
            else if (o1.getFactors().size() != 0 && o2.getFactors().size() == 0)
                return -1;
            else
                return Integer.compare(o1.getFactors().size(), o2.getFactors().size());
        }
    }
}

package uk.ac.imperial.isst.metric.cnf;

/**
 * @author uelis - https://github.com/uelis/FSV/tree/master/SAT/Java/cnf
 * @modified Martin Barrere <m.barrere@imperial.ac.uk>
 */

interface FormulaVisitor<A> {

  A visitVar(FormulaVar fm);

  A visitNeg(FormulaNeg fm);

  A visitOr(FormulaOr fm);

  A visitAnd(FormulaAnd fm);
}

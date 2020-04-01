package uk.ac.imperial.isst.metric.cnf;

/**
 * @author uelis - https://github.com/uelis/FSV/tree/master/SAT/Java/cnf
 * @modified Martin Barrere <m.barrere@imperial.ac.uk>
 */

public abstract class Formula {

  // package-private
  Formula() {}

  public abstract <A> A accept(FormulaVisitor<A> visitor);
  abstract public String toString();
}

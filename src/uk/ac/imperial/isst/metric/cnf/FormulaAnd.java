package uk.ac.imperial.isst.metric.cnf;

/**
 * @author uelis - https://github.com/uelis/FSV/tree/master/SAT/Java/cnf
 * @modified Martin Barrere <m.barrere@imperial.ac.uk>
 */

import java.util.List;

final class FormulaAnd extends Formula {

  final List<Formula> fms;

  public FormulaAnd(List<Formula> fms) {
    this.fms = fms;
  }

  public <A> A accept(FormulaVisitor<A> visitor) {
    return visitor.visitAnd(this);
  }

  @Override
  public String toString() {
    StringBuffer s = new StringBuffer();
    s.append("and(");
    String sep = "";
    for(Formula f : fms) {
      s.append(sep);
      s.append(f.toString());
      sep = ", ";
    }
    s.append(")");
    return s.toString();
  }


  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final FormulaAnd other = (FormulaAnd) obj;
    if (this.fms != other.fms && (this.fms == null || !this.fms.equals(other.fms))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 73 * hash + (this.fms != null ? this.fms.hashCode() : 0);
    return hash;
  }
}


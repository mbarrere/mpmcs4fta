package uk.ac.imperial.isst.metric.cnf;

/**
 * @author uelis - https://github.com/uelis/FSV/tree/master/SAT/Java/cnf
 * @modified Martin Barrere <m.barrere@imperial.ac.uk>
 */

import java.util.List;

final class FormulaOr extends Formula {

  final List<Formula> fms;

  public FormulaOr(List<Formula> fms) {
    this.fms = fms;
  }

  public <A> A accept(FormulaVisitor<A> visitor) {
    return visitor.visitOr(this);
  }

  @Override
  public String toString() {
    StringBuffer s = new StringBuffer();
    s.append("or(");
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
    final FormulaOr other = (FormulaOr) obj;
    if (this.fms != other.fms && (this.fms == null || !this.fms.equals(other.fms))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 97 * hash + (this.fms != null ? this.fms.hashCode() : 0);
    return hash;
  }
}


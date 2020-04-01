package uk.ac.imperial.isst.metric.cnf;

/**
 * @author uelis - https://github.com/uelis/FSV/tree/master/SAT/Java/cnf
 * @modified Martin Barrere <m.barrere@imperial.ac.uk>
 */

final class FormulaVar extends Formula {

  final Object name;

  public FormulaVar(Object name) {
    this.name = name;
  }

  public <A> A accept(FormulaVisitor<A> visitor) {
    return visitor.visitVar(this);
  }

  @Override
  public String toString() {
    return name.toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final FormulaVar other = (FormulaVar) obj;
    if (this.name != other.name && (this.name == null || !this.name.equals(other.name))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 41 * hash + (this.name != null ? this.name.hashCode() : 0);
    return hash;
  }
}

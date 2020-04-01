package uk.ac.imperial.isst.metric.cnf;

/**
 * @author uelis - https://github.com/uelis/FSV/tree/master/SAT/Java/cnf
 * @modified Martin Barrere <m.barrere@imperial.ac.uk>
 */

final class FormulaNeg extends Formula {

  final Formula fm;

  public FormulaNeg(Formula fm) {
    this.fm = fm;
  }

  public <A> A accept(FormulaVisitor<A> visitor) {
    return visitor.visitNeg(this);
  }

  @Override
  public String toString() {
    return "neg(" + fm + ")";
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final FormulaNeg other = (FormulaNeg) obj;
    if (this.fm != other.fm && (this.fm == null || !this.fm.equals(other.fm))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 37 * hash + (this.fm != null ? this.fm.hashCode() : 0);
    return hash;
  }
}



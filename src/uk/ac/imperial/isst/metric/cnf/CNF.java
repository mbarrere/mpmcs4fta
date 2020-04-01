package uk.ac.imperial.isst.metric.cnf;

/**
 * @author uelis - https://github.com/uelis/FSV/tree/master/SAT/Java/cnf
 * @modified Martin Barrere <m.barrere@imperial.ac.uk>
 */
	
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

public class CNF {

  private CNF() {
	  
  }

  public static Formula var(Object name) {
    if (name == null) {
      throw new NullPointerException("Formula's name cannot be null");
    }
    return new FormulaVar(name);
  }

  public static Formula tt() {
    return and();
  }

  public static Formula ff() {
    return or();
  }

  public static Formula neg(Formula f) {
    return new FormulaNeg(f);
  }

  public static Formula and(Formula f1, Formula f2) {
    List<Formula> fms = new LinkedList<>();
    fms.add(f1);
    fms.add(f2);
    return new FormulaAnd(fms);
  }

  public static Formula and(List<Formula> fms) {
    return new FormulaAnd(fms);
  }

  public static Formula and(Formula... fms) {
    return new FormulaAnd(Arrays.asList(fms));
  }

  public static Formula or(Formula f1, Formula f2) {
    List<Formula> fms = new LinkedList<>();
    fms.add(f1);
    fms.add(f2);
    return new FormulaOr(fms);
  }

  public static Formula or(List<Formula> fms) {
    return new FormulaOr(fms);
  }

  public static Formula or(Formula... fms) {
    return new FormulaOr(Arrays.asList(fms));
  }

  public static Formula imp(Formula fm1, Formula fm2) {
    return or(neg(fm1), fm2);
  }

  public static Formula iff(Formula fm1, Formula fm2) {
    return and(imp(fm1, fm2), imp(fm2, fm1));
  }

  public static Formula xor(Formula fm1, Formula fm2) {
    return or(and(fm1, neg(fm2)), and(neg(fm1), fm2));
  }

  public static Map<Integer, Object> writeDIMACS(OutputStream os, Formula f) throws IOException {
    TseitinVisitor tseitinVisitor = new TseitinVisitor();
    Integer x = f.accept(tseitinVisitor);
    tseitinVisitor.writeResultDIMACS(os, x);
    return tseitinVisitor.getVarNameMap();
  }

  public static Map<Object, Boolean> satisfiable(Formula f) throws TimeoutException {
    TseitinVisitor tseitinVisitor = new TseitinVisitor();
    Integer x = f.accept(tseitinVisitor);
    Set<Set<Integer>> clauses = tseitinVisitor.getClauses();

    ISolver solver = SolverFactory.newDefault();

    solver.setExpectedNumberOfClauses(clauses.size());
    try {
      solver.addClause(new VecInt(new int[]{x}));
      for (Set<Integer> c : clauses) {
        int[] carr = new int[c.size()];
        int i = 0;
        for (Integer y : c) {
          carr[i] = y;
          i++;
        }
        solver.addClause(new VecInt(carr));
      }
    } catch (ContradictionException ex) {
      return null; // unsat
    }

    IProblem problem = solver;
    if (problem.isSatisfiable()) {
      int[] model = problem.model();
      
      //System.out.println("CNF.model: " + model);
      for (int i = 0; i < model.length; i++) { System.out.print("* " + model[i] + " "); }
      
      Map<Object, Boolean> eta = new HashMap<>();
      for (Integer y : model) {
        Object var = tseitinVisitor.getVar(Math.abs(y));
        if (var != null) {
          eta.put(tseitinVisitor.getVar(Math.abs(y)), y > 0);
        }
      }
      return eta;
    } else {
      return null;
    }
  }
}

/**
 * 
 */
package uk.ac.imperial.isst.metric.cnf;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.sat4j.specs.TimeoutException;

/**
 * @author Martin Barrere <m.barrere@imperial.ac.uk>
 *
 */
public class CNFTest {

	/**
	 * 
	 */
	public static void main(String[] args) {
		try {
			test1();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	@SuppressWarnings("unused")
	public static void test1() throws TimeoutException, IOException {
		/*
		 ( c1 & 
			( d & 
				( 
					( ( a & s ) & ( b & s ) ) 
					| 
					( ( b & s ) & ( c & s ) ) 
				) 
			) 
		 )
		*/
		Formula s = CNF.var("s");
		Formula a = CNF.var("a");
		Formula b = CNF.var("b");
		Formula c = CNF.var("c");
		Formula d = CNF.var("d");
		Formula c1 = CNF.var("c1");
		
		Formula saAnd = CNF.and(s,a);
		Formula sbAnd = CNF.and(s,b);
		Formula scAnd = CNF.and(s,c);
		
		Formula saAndsbAnd = CNF.and(saAnd, sbAnd);
		Formula sbAndscAnd = CNF.and(sbAnd, scAnd);
		
		Formula or = CNF.or(saAndsbAnd, sbAndscAnd);
		
		Formula dAnd = CNF.and(d, or);
		
		Formula c1And = CNF.and(c1, dAnd);
		
		Formula objective = CNF.neg(c1And);
		
		Map<Object, Boolean> sat = CNF.satisfiable(objective);
		
		for (Entry<Object, Boolean> e : sat.entrySet()) {
			System.out.println("Object: " + e.getKey() + "; value: " + e.getValue());
		}
		
		Map<Integer, Object> dimacs = CNF.writeDIMACS(System.out, objective);
		
		for (Entry<Integer, Object> e : dimacs.entrySet()) {
			System.out.println("Integer: " + e.getKey() + "; object: " + e.getValue());
		}
		
		TseitinVisitor tseitinVisitor = new TseitinVisitor();
	    Integer x = objective.accept(tseitinVisitor);
	    Set<Set<Integer>> clauses = tseitinVisitor.getClauses();
	    
	    Iterator<Set<Integer>> iter = clauses.iterator();
	    while (iter.hasNext()) {
	    	Set<Integer> clause = iter.next();
			System.out.println("Clause: " + clause);
		}
	}

}

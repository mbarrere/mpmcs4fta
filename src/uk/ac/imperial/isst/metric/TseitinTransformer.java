package uk.ac.imperial.isst.metric;

import java.util.Map;

import uk.ac.imperial.isst.metric.cnf.Formula;
import uk.ac.imperial.isst.metric.cnf.TseitinStructure;
import uk.ac.imperial.isst.metric.cnf.TseitinVisitor;

/**
 * @author Martin Barrere <m.barrere@imperial.ac.uk>
 *
 */
public class TseitinTransformer {

	public static TseitinStructure transformGraphWithTseitin (ProblemSpecification problemSpec, Map<String,Object> stats) throws Exception{
		try {
			Formula formula = problemSpec.getLogicalFormula();			
			
			if (CpsMetricAnalyser.ASSIGNMENT_DEBUG) {
				System.out.println("\nLogical formula: \n" + formula);
			}

			formula = problemSpec.negateFormula(formula);
			if (CpsMetricAnalyser.ASSIGNMENT_DEBUG) {
				System.out.println("\nObjective: \n" + formula);
			}

			if (CpsMetricAnalyser.DEBUG) {				
				System.out.println("\n\nPerforming Tseitin transformation...");
			}

			long start = System.currentTimeMillis();
			// Tseitin TRANSFORMATION
			TseitinVisitor tseitinVisitor = new TseitinVisitor();
			Integer x = formula.accept(tseitinVisitor);
			long tseitinTime = System.currentTimeMillis() - start;

			stats.put("tseitin.time.ms", tseitinTime);
			stats.put("tseitin.time.sec", (tseitinTime/1000));

			if (CpsMetricAnalyser.DEBUG) {
				System.out.println("Tseitin transformation time: " + tseitinTime + " ms (" + (tseitinTime/1000) + " seconds).");
			}			
			
			TseitinStructure ts = new TseitinStructure(tseitinVisitor, x, formula);
			ts.setup(problemSpec, null, null);
			return ts;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}

	}
}

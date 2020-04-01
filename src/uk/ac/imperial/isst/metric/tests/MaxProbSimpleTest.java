package uk.ac.imperial.isst.metric.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import uk.ac.imperial.isst.metric.CpsMetricAnalyser;
import uk.ac.imperial.isst.metric.ProblemSolution;
import uk.ac.imperial.isst.metric.ProblemSpecification;
import uk.ac.imperial.isst.metric.config.ToolConfig;
import uk.ac.imperial.isst.metric.util.GraphUtils;
import uk.ac.imperial.isst.metric.util.JSONReader;

/**
 * @author Martin Barrere <m.barrere@imperial.ac.uk>
 *
 */

public class MaxProbSimpleTest {
	private static ToolConfig config = null;
	
	@BeforeClass 
	public static void setUpClass() {		
		config = new ToolConfig(); 
		config.loadDefaultSetings();  
		config.tryLoadDefaultFromFile(CpsMetricAnalyser.CONFIG_FILE);
		//config.printProperties();				
		CpsMetricAnalyser.setupTool(config);	
		CpsMetricAnalyser.OUTPUT_SOL = false;
		CpsMetricAnalyser.OUTPUT_WCNF = false;
		CpsMetricAnalyser.OUTPUT_TXT = false;
		CpsMetricAnalyser.DEBUG = false;
		CpsMetricAnalyser.FULL_DEBUG = false;
		
		CpsMetricAnalyser.USE_MAX_SAT = true;
		CpsMetricAnalyser.USE_OPTIM = false;
		CpsMetricAnalyser.WEIGHTS_AS_PROBABILITIES = true;
		System.out.println("TEST class setup DONE!\n");
    }
	
	@AfterClass 
    public static void tearDownClass() {
		System.out.println("\nALL TESTS FINISHED");
    }
	
	@Before
    public void setUp() {
		System.out.println("TEST started");
		
	}
 
    @After
    public void tearDown() throws IOException {
    	System.out.println("TEST finished");
    }
    
    	
	@Test
	public void testExample1() {
		try {
			String filename = "examples/example1.json";							
			ProblemSpecification problem = new JSONReader().loadProblemSpecification(filename);		
			ProblemSolution sol = CpsMetricAnalyser.solveWithTseitinAndDisplaySolution(problem, config);
			
			assertEquals(Double.valueOf(0.04), Double.valueOf(GraphUtils.round(sol.getCut().getCost().doubleValue(),2)));
			//sol.getCut().display();
		} catch (Exception e) {			
			e.printStackTrace();
			fail(e.getMessage());
		}		
	}
	
	
	@Test
	public void testExample1b() {
		try {
			String filename = "examples/example1b.json";
								
			ProblemSpecification problem = new JSONReader().loadProblemSpecification(filename);		
			ProblemSolution sol = CpsMetricAnalyser.solveWithTseitinAndDisplaySolution(problem, config);
			
			assertEquals(Double.valueOf(0.05), Double.valueOf(GraphUtils.round(sol.getCut().getCost().doubleValue(),2)));
			//sol.getCut().display();
		} catch (Exception e) {			
			e.printStackTrace();
			fail(e.getMessage());
		}		
	}	
	
	@Test
	public void testAircraftCase1() {
		try {
			String filename = "examples/aircraft-case1.json";
								
			ProblemSpecification problem = new JSONReader().loadProblemSpecification(filename);		
			ProblemSolution sol = CpsMetricAnalyser.solveWithTseitinAndDisplaySolution(problem, config);					
			
			Double expectedCost = Double.valueOf(0.00000625);
			Double actualCost = sol.getCut().getCost();
			
			Double roundedExpectedCost = Double.valueOf(GraphUtils.round(expectedCost.doubleValue(),CpsMetricAnalyser.DECIMAL_PLACES));
			Double roundedActualCost = Double.valueOf(GraphUtils.round(actualCost.doubleValue(),CpsMetricAnalyser.DECIMAL_PLACES));
			
			assertEquals(roundedExpectedCost, roundedActualCost);
		} catch (Exception e) {			
			e.printStackTrace();
			fail(e.getMessage());
		}		
	}
	
	@Test
	public void testAircraftCase2() {
		try {
			String filename = "examples/aircraft-case2.json";
								
			ProblemSpecification problem = new JSONReader().loadProblemSpecification(filename);		
			ProblemSolution sol = CpsMetricAnalyser.solveWithTseitinAndDisplaySolution(problem, config);
			
			Double expectedCost = Double.valueOf(0.0025);
			Double actualCost = sol.getCut().getCost();
			
			Double roundedExpectedCost = Double.valueOf(GraphUtils.round(expectedCost.doubleValue(),CpsMetricAnalyser.DECIMAL_PLACES));
			Double roundedActualCost = Double.valueOf(GraphUtils.round(actualCost.doubleValue(),CpsMetricAnalyser.DECIMAL_PLACES));
			
			assertEquals(roundedExpectedCost, roundedActualCost);
		} catch (Exception e) {			
			e.printStackTrace();
			fail(e.getMessage());
		}		
	}
	
	@Test
	public void testAircraftCase3() {
		try {
			String filename = "examples/aircraft-case3.json";
								
			ProblemSpecification problem = new JSONReader().loadProblemSpecification(filename);		
			ProblemSolution sol = CpsMetricAnalyser.solveWithTseitinAndDisplaySolution(problem, config);
						
			Double expectedCost = Double.valueOf(0.0025);
			Double actualCost = sol.getCut().getCost();
			
			Double roundedExpectedCost = Double.valueOf(GraphUtils.round(expectedCost.doubleValue(),CpsMetricAnalyser.DECIMAL_PLACES));
			Double roundedActualCost = Double.valueOf(GraphUtils.round(actualCost.doubleValue(),CpsMetricAnalyser.DECIMAL_PLACES));
			
			assertEquals(roundedExpectedCost, roundedActualCost);
		} catch (Exception e) {			
			e.printStackTrace();
			fail(e.getMessage());
		}		
	}
	
	@Test
	public void testAircraftCase4() {
		try {
			String filename = "examples/aircraft-case4.json";
								
			ProblemSpecification problem = new JSONReader().loadProblemSpecification(filename);		
			ProblemSolution sol = CpsMetricAnalyser.solveWithTseitinAndDisplaySolution(problem, config);
			
			int decimalPlaces = 2;
			Double expectedCost = Double.valueOf(0.05);
			Double actualCost = sol.getCut().getCost();
			
			Double roundedExpectedCost = Double.valueOf(GraphUtils.round(expectedCost.doubleValue(),decimalPlaces));
			Double roundedActualCost = Double.valueOf(GraphUtils.round(actualCost.doubleValue(),decimalPlaces));
			
			assertEquals(roundedExpectedCost, roundedActualCost);
		} catch (Exception e) {			
			e.printStackTrace();
			fail(e.getMessage());
		}		
	}
	
	@Test
	public void testAircraftCase5() {
		try {
			String filename = "examples/aircraft-case5.json";
								
			ProblemSpecification problem = new JSONReader().loadProblemSpecification(filename);		
			ProblemSolution sol = CpsMetricAnalyser.solveWithTseitinAndDisplaySolution(problem, config);
			
			int decimalPlaces = CpsMetricAnalyser.DECIMAL_PLACES;
			Double expectedCost = Double.valueOf(0.00000025);
			Double actualCost = sol.getCut().getCost();
			
			Double roundedExpectedCost = Double.valueOf(GraphUtils.round(expectedCost.doubleValue(),decimalPlaces));
			Double roundedActualCost = Double.valueOf(GraphUtils.round(actualCost.doubleValue(),decimalPlaces));
			
			assertEquals(roundedExpectedCost, roundedActualCost);
		} catch (Exception e) {			
			e.printStackTrace();
			fail(e.getMessage());
		}		
	}
	
	@Test
	public void testAircraftCase6() {
		try {
			String filename = "examples/aircraft-case6.json";
								
			ProblemSpecification problem = new JSONReader().loadProblemSpecification(filename);		
			ProblemSolution sol = CpsMetricAnalyser.solveWithTseitinAndDisplaySolution(problem, config);
			
			assertEquals(Double.valueOf(0.01), sol.getCut().getCost());
			//sol.getCut().display();
		} catch (Exception e) {			
			e.printStackTrace();
			fail(e.getMessage());
		}		
	}
	
	
	@Test
	public void testAircraftSimulation() {
		try {
			String filename = "examples/aircraft-simulation.json";
								
			ProblemSpecification problem = new JSONReader().loadProblemSpecification(filename);		
			ProblemSolution sol = CpsMetricAnalyser.solveWithTseitinAndDisplaySolution(problem, config);
			
			assertEquals(Double.valueOf(0.0016), sol.getCut().getCost());
			//sol.getCut().display();
		} catch (Exception e) {			
			e.printStackTrace();
			fail(e.getMessage());
		}		
	}
	
}

package uk.ac.imperial.isst.metric.tests;

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
import uk.ac.imperial.isst.metric.util.JSONReader;

/**
 * @author Martin Barrere <m.barrere@imperial.ac.uk>
 *
 */

public class SimpleExampleTest {
	private static ToolConfig config = null;
	
	@BeforeClass 
	public static void setUpClass() {		
		config = new ToolConfig(); 
		config.loadDefaultSetings();  
		config.tryLoadDefaultFromFile(CpsMetricAnalyser.CONFIG_FILE);
		//config.printProperties();				
		CpsMetricAnalyser.setupTool(config);
		//CpsMetricAnalyser.USE_OPTIM = true;
		//CpsMetricAnalyser.USE_MAX_SAT = false;
		CpsMetricAnalyser.OUTPUT_SOL = true;
		CpsMetricAnalyser.OUTPUT_WCNF = false;
		CpsMetricAnalyser.OUTPUT_TXT = false;
		CpsMetricAnalyser.DEBUG = false;
		CpsMetricAnalyser.FULL_DEBUG = false;
		CpsMetricAnalyser.WEIGHTS_AS_PROBABILITIES = true;
		CpsMetricAnalyser.MSCS = true;
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
			//String filename = "examples/example1b.json";							
			String filename = "examples/fps.json";
			//String filename = "examples/example-tank.json";
			//String filename = "examples/example-aircraft.json";
			ProblemSpecification problem = new JSONReader().loadProblemSpecification(filename);		
			ProblemSolution sol = CpsMetricAnalyser.solveWithTseitinAndDisplaySolution(problem, config);
			
			//assertEquals(Double.valueOf(4.0), sol.getCut().getCost());
			sol.getCut().display();
		} catch (Exception e) {			
			e.printStackTrace();
			fail(e.getMessage());
		}		
	}
	
	

}

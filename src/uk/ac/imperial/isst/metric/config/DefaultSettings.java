package uk.ac.imperial.isst.metric.config;

import java.util.Properties;

/**
 * @author Martin Barrere <m.barrere@imperial.ac.uk>
 *
 */

public class DefaultSettings {
		
	private static final String pythonPath = "/usr/local/bin/python3";
	private static final String pythonSolverPath = "python/optim.py";
	
	private static final String folderOutput = "output";   
	private static final String folderView = "view";
    
	public DefaultSettings() {
		
	}
	
	public void loadProperties(Properties prop) {
		prop.setProperty(ConfigKeys.useSat4jKey, String.valueOf(true));
		prop.setProperty(ConfigKeys.useOptimKey, String.valueOf(false));
		prop.setProperty(ConfigKeys.pythonPathKey, pythonPath);
		prop.setProperty(ConfigKeys.pythonSolverPathKey, pythonSolverPath);
		
		prop.setProperty(ConfigKeys.outputSolKey, String.valueOf(true));
		prop.setProperty(ConfigKeys.outputWcnfKey, String.valueOf(false));
		prop.setProperty(ConfigKeys.outputTxtKey, String.valueOf(false));
		
		prop.setProperty(ConfigKeys.outputFolder, folderOutput);
		prop.setProperty(ConfigKeys.outputViewFolder, folderView);		
		
		prop.setProperty(ConfigKeys.basicDebugKey, String.valueOf(false));
		prop.setProperty(ConfigKeys.fullDebugKey, String.valueOf(false));
				
		prop.setProperty(ConfigKeys.weightsAsProbabilities, String.valueOf(true));
	}

}

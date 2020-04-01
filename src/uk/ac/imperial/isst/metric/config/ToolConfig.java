package uk.ac.imperial.isst.metric.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * @author Martin Barrere <m.barrere@imperial.ac.uk>
 *
 */

public class ToolConfig {
	private Properties prop;
	
	public ToolConfig() {
		this.prop = new Properties();		
	}	
	
	public Properties getProperties() {
		return this.prop;
	}
	
	public void loadDefaultSetings() {
		new DefaultSettings().loadProperties(this.prop);
	}
	
	public void tryLoadDefaultFromFile(String filepath) {
		try (InputStream input = new FileInputStream(filepath)) {
             this.prop.load(input);
        } catch (IOException ex) {
            //System.out.println("Default config file not found");
        }
	}
	
	public void loadConfigFromFile(String filepath) throws IOException{
		try (InputStream input = new FileInputStream(filepath)) {			
            this.prop.load(input);            
        } catch (IOException ex) {            
        	throw ex;
        }
	}
	
	public void printProperties() {		
        for (Entry<Object,Object> e : this.prop.entrySet()) {
        	System.out.println(e.getKey() + "=" + e.getValue());
        }                                       
	}
}

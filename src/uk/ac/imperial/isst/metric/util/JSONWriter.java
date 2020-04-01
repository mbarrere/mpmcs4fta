/**
 * 
 */
package uk.ac.imperial.isst.metric.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import uk.ac.imperial.isst.metric.ProblemSolution;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Martin Barrere <m.barrere@imperial.ac.uk>
 *
 */
public class JSONWriter {

	public JSONWriter() {		
	}
	
	public void write(ProblemSolution solution, String outputFilepath) throws JsonProcessingException, FileNotFoundException, UnsupportedEncodingException {
		
		ObjectMapper mapper = new ObjectMapper();		 
		//String json = mapper.writeValueAsString(problem.getGraph());
		String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(solution); //rootNode.asText();				
		 
		File outputFile = new File(outputFilepath);			
		PrintWriter writer = new PrintWriter(outputFile, "UTF-8");		
		writer.println(json);						
		writer.close();		
	}

}

/**
 * Project Zelula
 *
 * Contextproject TI2800 
 * TU Delft - University of Technology
 *  
 * Authors: 
 * 	Felix Akkermans, Niels Doekemeijer, Thomas van Helden
 * 	Albert ten Napel, Jan Pieter Waagmeester
 * 
 * https://github.com/FelixAkk/synthbio
 */

package synthbio.servlets.test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

import org.json.JSONArray;
import org.json.JSONObject;

import synthbio.Util;

import com.gargoylesoftware.htmlunit.WebClient;

/**
 * Testing the Validate Circuit Servlet
 *
 */
public class TestSimulateCircuitServlet extends TestCircuitServlet{

	/**
	 * Server location
	 */
	public static final String url="http://localhost:8080/SimulateCircuit";


	@Test
	public void test_withValid() throws Exception {
		JSONObject circuit=Util.fileToJSONObject("data/synstore/example-with-inputs.syn");

		String page=this.getTestPage(this.url+"?circuit="+circuit.toString());

		JSONObject response=new JSONObject(page);
		
		assertTrue(response.getBoolean("success"));
	}
	
}

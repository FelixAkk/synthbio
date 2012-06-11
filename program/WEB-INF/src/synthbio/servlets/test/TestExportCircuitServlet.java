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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

import org.json.JSONObject;

import synthbio.Util;

/**
 * Testing the Validate Circuit Servlet
 *
 */
public class TestExportCircuitServlet extends TestCircuitServlet{

	/**
	 * Server location
	 */
	public static final String url="http://localhost:8080/ExportCircuit";


	/**
	 * Test if sbml is returned for a valid circuit
	 */
	@Test
	public void test_withValid() throws Exception {
		JSONObject circuit=Util.fileToJSONObject("data/synstore/handout-xor.syn");

		String page=this.getTestPage(this.url+"?circuit="+circuit.toString());

		assertThat(page, startsWith("<?xml"));
		assertThat(page, containsString("sbml"));
		
	}
}

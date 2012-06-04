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
public class TestValidateCircuitServlet extends TestCircuitServlet{

	/**
	 * Server location
	 */
	public static final String url="http://localhost:8080/ValidateCircuit";

	/**
	 * Test if the servlet returns a succes if a valid circuit is thrown
	 * in.
	 */
	@Test
	public void testValidate_withValid() throws Exception {
		JSONObject circuit=Util.fileToJSONObject("data/synstore/example-with-inputs.syn");

		String page=this.getTestPage(this.url+"?circuit="+circuit.toString());

		JSONObject response=new JSONObject(page);
		
		assertTrue(response.getBoolean("success"));
	}

	/**
	 * Test if the servlet returns an error if an incomplete circuit is
	 * thrown in. Only one invalid circuit is tested since the circuit
	 * validation is tested more thoroughly in CircuitFactory's tests.
	 */
	@Test
	public void testValidate_withoutInputs() throws Exception {
		JSONObject circuit=Util.fileToJSONObject("data/test/models/incompleteAndCircuit.json");

		String page=this.getTestPage(this.url+"?circuit="+circuit.toString());

		JSONObject response=new JSONObject(page);

		assertFalse(response.getBoolean("success"));
		assertThat(response.getString("message"), equalTo("Error in Circuit: At least one AND gate has only one input."));
		
	}
}

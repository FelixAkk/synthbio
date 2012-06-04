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
 * Testing the Load Circuits Servlet
 *
 */
public class TestLoadCircuitServlet extends TestCircuitServlet{

	/**
	 * Server location
	 */
	public static final String url="http://localhost:8080/LoadCircuit";

	/**
	 * Test the loadFile action
	 */
	@Test
	public void testLoad() throws Exception{
		String page=this.getTestPage(this.url+"?filename=example.syn");
		JSONObject response=new JSONObject(page);

		//check success and empty message string.
		assertTrue(response.getBoolean("success"));
		assertEquals("", response.getString("message"));

		JSONObject data=response.getJSONObject("data");
		JSONObject expect=Util.fileToJSONObject("data/synstore/example.syn");
		assertEquals(expect.getString("name"), data.getString("name"));
		assertEquals(expect.getString("description"), data.getString("description"));
	}

}

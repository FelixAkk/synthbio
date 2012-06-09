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
 * Testing the List Circuits Servlet
 *
 */
public class TestListCircuitsServlet extends TestCircuitServlet{

	/**
	 * Server location
	 */
	public static final String url="http://localhost:8080/ListCircuits";

	/**
	 * Check if
	 * - a the request is succesfull
	 * - a list of files is returned.
	 * - the list contains more than one entry.
	 * - every file ends with ".syn"
	 */
	@Test
	public void testList() throws Exception{
		String page=this.getTestPage(this.url+"?action=list&folderName=");

		JSONObject response=new JSONObject(page);

		//check success and empty message string.
		assertTrue(response.getBoolean("success"));
		assertEquals("", response.getString("message"));
		
		//check returned type.
		assertThat(response.getJSONArray("data"), is(JSONArray.class));

		JSONArray data=response.getJSONArray("data");
		
		//check number of files returned is at least one.
		assertThat(data.length(), is(greaterThan(0)));

		//check that each file name ends with .syn.
		for(int i=0; i<data.length(); i++){
			assertThat(data.getJSONObject(i).getString("filename"), endsWith(".syn"));
		}

		//check that the file list contains 'example.syn'
		assertThat(page, containsString("example.syn"));
	}
}

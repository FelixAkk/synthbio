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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

import org.json.JSONArray;
import org.json.JSONObject;

import synthbio.Util;

import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;

/**
 * Testing the Circuit Servlet
 *
 */
public class TestCircuitServlet{

	/**
	 * Server location
	 */
	public static final String url="http://localhost:8080/Circuit";

	public TextPage getTestPage(String url) throws Exception{
		final WebClient webClient = new WebClient();
		return webClient.getPage(url);
	}

	/**
	 * Check if an error message is returned if no action is supplied.
	 */
	@Test
	public void testNoAction() throws Exception{
		TextPage page=this.getTestPage(this.url);
		String expected="Parameter 'action' not set.";

		assertThat(page.getContent(), containsString(expected));
	}

	/**
	 * Check if
	 * - a the request is succesfull
	 * - a list of files is returned.
	 * - the list contains more than one entry.
	 * - every file ends with ".syn"
	 */
	@Test
	public void testList() throws Exception{
		TextPage page=this.getTestPage(this.url+"?action=list");

		JSONObject response=new JSONObject(page.getContent());

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
			assertThat(data.getString(i), endsWith(".syn"));
		}

		//check that the file list contains 'example.syn'
		assertThat(page.getContent(), containsString("example.syn"));
	}

	/**
	 * Test the loadFile action
	 */
	@Test
	public void testLoad() throws Exception{
		TextPage page=this.getTestPage(this.url+"?action=load&filename=example.syn");

		JSONObject response=new JSONObject(page.getContent());

		//check success and empty message string.
		assertTrue(response.getBoolean("success"));
		assertEquals("", response.getString("message"));

		JSONObject data=response.getJSONObject("data");
		JSONObject expect=Util.fileToJSONObject("data/synstore/example.syn");
		assertEquals(expect.getString("name"), data.getString("name"));
		assertEquals(expect.getString("description"), data.getString("description"));
		

	}
}
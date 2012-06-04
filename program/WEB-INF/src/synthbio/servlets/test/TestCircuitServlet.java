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
 * Testing the Circuit Servlet
 *
 */
public class TestCircuitServlet{

	/**
	 * Server location
	 */
	public static final String url="http://localhost:8080/Circuit";

	public String getTestPage(String url) throws Exception{
		final WebClient webClient = new WebClient();
		return webClient.getPage(url).getWebResponse().getContentAsString().trim();
	}

	/**
	 * Check if an error message is returned if no action is supplied.
	 */
	@Test
	public void testNoAction() throws Exception{
		String page=this.getTestPage(this.url);
		String expected="Parameter 'action' not set.";

		assertThat(page, containsString(expected));
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
		String page=this.getTestPage(this.url+"?action=list");

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

	/**
	 * Test the loadFile action
	 */
	@Test
	public void testLoad() throws Exception{
		String page=this.getTestPage(this.url+"?action=load&filename=example.syn");
		JSONObject response=new JSONObject(page);

		//check success and empty message string.
		assertTrue(response.getBoolean("success"));
		assertEquals("", response.getString("message"));

		JSONObject data=response.getJSONObject("data");
		JSONObject expect=Util.fileToJSONObject("data/synstore/example.syn");
		assertEquals(expect.getString("name"), data.getString("name"));
		assertEquals(expect.getString("description"), data.getString("description"));
	}

	/**
	 * Check if the save actions actually saves something and
	 * saves the correct file.
	 */
	@Test
	public void testSave() throws Exception{
		//unlink test.syn
		File test=new File("data/synstore/test.syn");
		if(test.isFile()){
			test.delete();
		}
		
		JSONObject circuit=Util.fileToJSONObject("data/synstore/example.syn");
		
		String page=this.getTestPage(this.url+"?action=save&filename=test.syn&circuit="+circuit.toString());

		JSONObject response=new JSONObject(page);

		assertTrue(response.getBoolean("success"));
		assertEquals("Saved succesfully", response.getString("message"));

		JSONObject actual=Util.fileToJSONObject("data/synstore/example.syn");

		// 'deep'-equal the json objects.
		assertEquals(circuit.getString("name"), actual.getString("name"));
		assertEquals(
			circuit.getString("description"),
			actual.getString("description")
		);
		assertEquals(
			circuit.getJSONArray("gates").toString(),
			actual.getJSONArray("gates").toString()
		);
		assertEquals(
			circuit.getJSONArray("signals").toString(),
			actual.getJSONArray("signals").toString()
		);
		if(test.isFile()){
			test.delete();
		}
	}

	/**
	 * Test if the server rejects a file with an extension which is not
	 * .syn.
	 */
	@Test
	public void testSave_notDotSyn() throws Exception {
		String content="abracadabra";
		String page=this.getTestPage(this.url+"?action=save&filename=test.foo&circuit="+content);

		JSONObject response=new JSONObject(page);

		assertFalse(response.getBoolean("success"));
		assertEquals("Filename should end with .syn.", response.getString("message"));
	}

	/**
	 * Test if the server complains about unset filename.
	 */
	@Test
	public void testSave_noFilename() throws Exception {
		String content="abracadabra";
		String page=this.getTestPage(this.url+"?action=save&circuit="+content);

		JSONObject response=new JSONObject(page);

		assertFalse(response.getBoolean("success"));
		assertEquals("Parameter 'filename' not set.", response.getString("message"));
	}
	
	/**
	 * Test if the server complains about unset circuit.
	 */
	@Test
	public void testSave_noCircuit() throws Exception {
		String page=this.getTestPage(this.url+"?action=save&filename=test.syn");

		JSONObject response=new JSONObject(page);

		assertFalse(response.getBoolean("success"));
		assertEquals("Parameter 'circuit' not set.", response.getString("message"));
	}

	/**
	 * Test if the servlet returns a succes if a valid circuit is thrown
	 * in.
	 */
	@Test
	public void testValidate_withValid() throws Exception {
		JSONObject circuit=Util.fileToJSONObject("data/synstore/example-with-inputs.syn");

		String page=this.getTestPage(this.url+"?action=validate&circuit="+circuit.toString());

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

		String page=this.getTestPage(this.url+"?action=validate&circuit="+circuit.toString());

		JSONObject response=new JSONObject(page);

		assertFalse(response.getBoolean("success"));
		assertThat(response.getString("message"), equalTo("Error in Circuit: At least one AND gate has only one input."));
		
	}
}

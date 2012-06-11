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

import org.junit.Test;

import org.json.JSONObject;

import synthbio.Util;

/**
 * Testing the Save Circuits Servlet
 *
 */
public class TestSaveCircuitServlet extends TestCircuitServlet{

	/**
	 * Server location
	 */
	public static final String url="http://localhost:8080/SaveCircuit";

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
		
		String page=this.getTestPage(this.url+"?filename=test.syn&circuit="+circuit.toString());

		JSONObject response=new JSONObject(page);

		assertTrue("Response is not successfull", response.getBoolean("success"));
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
		String filename="data/synstore/test.foo";
		String page=this.getTestPage(this.url+"?filename=test.foo&circuit="+content);

		JSONObject response=new JSONObject(page);

		assertFalse(response.getBoolean("success"));
		assertEquals("Filename should end with .syn.", response.getString("message"));

		File file=new File(filename);
		assertFalse("The test file " + filename + " should not be on the file system.", file.isFile());
	}

	/**
	 * Test if the server complains about unset filename.
	 */
	@Test
	public void testSave_noFilename() throws Exception {
		String content="abracadabra";
		String page=this.getTestPage(this.url+"?circuit="+content);

		JSONObject response=new JSONObject(page);

		assertFalse(response.getBoolean("success"));
		assertEquals("Parameter 'filename' not set.", response.getString("message"));
	}
	
	/**
	 * Test if the server complains about unset circuit.
	 */
	@Test
	public void testSave_noCircuit() throws Exception {
		String page=this.getTestPage(this.url+"?filename=test.syn");

		JSONObject response=new JSONObject(page);

		assertFalse(response.getBoolean("success"));
		assertEquals("Parameter 'circuit' not set.", response.getString("message"));
	}
}

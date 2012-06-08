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

package synthbio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.*;
import org.junit.Test;

import synthbio.Util;

import org.json.*;

/**
 * Testing CsvInputsToJSON 
 * @author Albert ten Napel
 */
public class TestCsvInputsToJSON {

	@Test
	public void test1() throws JSONException {
		String csvEx = "t,A,M\n0,1,1\n70,0,1\n140,1,0\n210,0,0";
		JSONObject j = Util.csvInputsToJSON(csvEx);

		String exp = "{\"highLevel\":200,\"length\":28,\"lowLevel\":0,\"tickWidth\":10,\"values\":{\"A\":\"HHHHHHHLLLLLLLHHHHHHHLLLLLLL\",\"M\":\"HHHHHHHHHHHHHHLLLLLLLLLLLLLL\"}}";
		assertEquals(exp, j.toString());	
	}
}


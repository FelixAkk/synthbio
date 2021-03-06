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

package synthbio.simulator.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.simulator.math.odes.MultiTable;
import org.junit.*;
import org.junit.Test;

import synthbio.models.Circuit;
import synthbio.models.CircuitException;
import synthbio.models.CircuitFactory;
import synthbio.simulator.CircuitConverter;
import synthbio.Util;

import org.json.JSONException;
import java.io.IOException;

/**
 * Testing the circuit converter.
 * @author Albert ten Napel
 */
public class TestCircuitConverter {
	private final String notCircuit = "data/test/simulator/notCircuit.syn";
	private final String andCircuit = "data/test/simulator/andCircuit.syn";
	private final String nandCircuit = "data/test/simulator/nandCircuit.syn";
	private final String inputCircuit = "data/test/simulator/inputCircuit.syn";

  /**
	 * Converts a .syn file to SBML.
	 * Useful method for the tests.
	 */
	public static String convertFromFile(String filename) throws CircuitException, JSONException, IOException {
		return CircuitConverter.convert((new CircuitFactory()).fromJSON(Util.fileToString(filename)));
	}

	/**
	 * Test if a simple Not-gate is properly converted to SBML.
	 */
	@Test
	public void testNotCircuit() throws CircuitException, JSONException, IOException {
		String sbmlResult = convertFromFile(notCircuit);
		//System.out.println(sbmlResult);	
		assertTrue(sbmlResult.contains("<species id=\"A\""));
		assertTrue(sbmlResult.contains("<species id=\"B\""));
		assertTrue(sbmlResult.contains("<species id=\"mB\""));
		assertTrue(sbmlResult.contains("<reaction id=\"Transcription_not_A__mB\""));
		assertTrue(sbmlResult.contains("<reaction id=\"Translation_not_mB__B\""));
	}
	
	/**
	 * Test if a simple And-gate is properly converted to SBML.
	 */
	@Test
	public void testAndCircuit() throws CircuitException, JSONException, IOException {
		String sbmlResult = convertFromFile(andCircuit);
		
		assertTrue(sbmlResult.contains("<species id=\"A\""));
		assertTrue(sbmlResult.contains("<species id=\"B\""));
		assertTrue(sbmlResult.contains("<species id=\"mC\""));
		assertTrue(sbmlResult.contains("<reaction id=\"Transcription_and_A_B__mC\""));
		assertTrue(sbmlResult.contains("<reaction id=\"Translation_and_mC__C\""));
	}
	
	/**
	 * Test if a more complex Nand-gate is properly converted to SBML.
	 */
	@Test
	public void testNandCircuit() throws CircuitException, JSONException, IOException {
		String sbmlResult = convertFromFile(nandCircuit);
		//System.out.println(sbmlResult);
		
		assertTrue(sbmlResult.contains("<species id=\"A\""));
		assertTrue(sbmlResult.contains("<species id=\"B\""));
		assertTrue(sbmlResult.contains("<species id=\"C\""));
		assertTrue(sbmlResult.contains("<species id=\"D\""));
		assertTrue(sbmlResult.contains("<species id=\"mC\""));
		assertTrue(sbmlResult.contains("<species id=\"mD\""));
		assertTrue(sbmlResult.contains("<reaction id=\"Transcription_and_A_B__mC\""));
		assertTrue(sbmlResult.contains("<reaction id=\"Translation_and_mC__C\""));
		assertTrue(sbmlResult.contains("<reaction id=\"Transcription_not_C__mD\""));
		assertTrue(sbmlResult.contains("<reaction id=\"Translation_not_mD__D\""));
	}
	
	/**
	 * Tests getInputs()
	 */
	@Test
	public void testGetInputs() throws IOException, CircuitException, JSONException {
		// result
		
		Circuit c = (new CircuitFactory()).fromJSON(Util.fileToString(inputCircuit));
		MultiTable m = c.getSimulationSetting().getInputMultiTable();
		
		// expected result
		//"A": "H",
    //"B": "LLLLLLLLLL LLLLLLLLLL H"
		double[] times = new double[40];
		for(int i = 0; i < 40; i++)
			times[i] = i*10;
		String[] names = new String[] { "A", "B" };
		double[][] data = new double[40][2];
		for(int time = 0; time < 40; time++) {
			data[time][0] = 200d;
			data[time][1] = (time < 20? 0d: 200d);
		}
		MultiTable exp = new MultiTable(times, data, names);
			
		assertEquals(exp.toString(), m.toString());
	}
}

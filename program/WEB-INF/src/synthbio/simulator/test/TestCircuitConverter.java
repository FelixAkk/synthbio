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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.*;
import org.junit.Test;

import synthbio.models.Circuit;
import synthbio.simulator.CircuitConverter;
import synthbio.Util;
import synthbio.models.CircuitException;
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
	
	/**
	 * Test if a simple Not-gate is properly converted to SBML.
	 */
	@Ignore
	@Test
	public void testNotCircuit() throws CircuitException, JSONException, IOException {
		CircuitConverter converter = new CircuitConverter();
		Circuit circuit = Circuit.fromJSON(Util.fileToJSONObject(notCircuit));
		String sbmlResult = converter.convert(circuit);
		
		assertTrue(sbmlResult.contains("<species id=\"A\""));
		assertTrue(sbmlResult.contains("<species id=\"B\""));
		assertTrue(sbmlResult.contains("<species id=\"mB\""));
		assertTrue(sbmlResult.contains("<reaction id=\"transcription_not_A_mB\""));
		assertTrue(sbmlResult.contains("<reaction id=\"translation_not_mB_B\""));
	}
	
	/**
	 * Test if a simple And-gate is properly converted to SBML.
	 */
	@Ignore
	@Test
	public void testAndCircuit() throws CircuitException, JSONException, IOException {
		CircuitConverter converter = new CircuitConverter();
		Circuit circuit = Circuit.fromJSON(Util.fileToJSONObject(andCircuit));
		String sbmlResult = converter.convert(circuit);
		
		assertTrue(sbmlResult.contains("<species id=\"A\""));
		assertTrue(sbmlResult.contains("<species id=\"B\""));
		assertTrue(sbmlResult.contains("<species id=\"mC\""));
		assertTrue(sbmlResult.contains("<reaction id=\"transcription_and_A_B_mC\""));
		assertTrue(sbmlResult.contains("<reaction id=\"translation_and_mC_C\""));
	}
	
	/**
	 * Test if a more complex Nand-gate is properly converted to SBML.
	 */
	@Ignore
	@Test
	public void testNandCircuit() throws CircuitException, JSONException, IOException {
		CircuitConverter converter = new CircuitConverter();
		Circuit circuit = Circuit.fromJSON(Util.fileToJSONObject(nandCircuit));
		String sbmlResult = converter.convert(circuit);
		
		assertTrue(sbmlResult.contains("<species id=\"A\""));
		assertTrue(sbmlResult.contains("<species id=\"B\""));
		assertTrue(sbmlResult.contains("<species id=\"C\""));
		assertTrue(sbmlResult.contains("<species id=\"D\""));
		assertTrue(sbmlResult.contains("<species id=\"mC\""));
		assertTrue(sbmlResult.contains("<species id=\"mD\""));
		assertTrue(sbmlResult.contains("<reaction id=\"transcription_and_A_B_mC\""));
		assertTrue(sbmlResult.contains("<reaction id=\"translation_and_mC_C\""));
		assertTrue(sbmlResult.contains("<reaction id=\"transcription_and_C_mD\""));
		assertTrue(sbmlResult.contains("<reaction id=\"translation_and_mD_D\""));
	}
}

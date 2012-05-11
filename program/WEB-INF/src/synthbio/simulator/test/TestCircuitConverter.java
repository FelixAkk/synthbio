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
	@Test
	public void testNotCircuit() throws CircuitException, JSONException, IOException {
		String sbmlResult = (new CircuitConverter()).convertFromFile(notCircuit);
		
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
		String sbmlResult = (new CircuitConverter()).convertFromFile(andCircuit);
		
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
		String sbmlResult = (new CircuitConverter()).convertFromFile(nandCircuit);
		
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
}

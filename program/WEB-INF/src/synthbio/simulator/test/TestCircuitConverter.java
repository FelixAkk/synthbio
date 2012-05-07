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

import synthbio.simulator.CircuitConverter;
import synthbio.Util;

/**
 * Testing the circuit converter.
 * @author Albert ten Napel
 */
public class TestCircuitConverter {
	private final String notCircuit = "data/test/simulator/notCircuit.syn";
	
	/**
	 * Test if notCircuit.syn is properly converted to SBML.
	 */
	@Test
	public void testNotCircuit() {
		CircuitConverter converter = new CircuitConverter();
		Circuit circuit = Circuit.fromJSON(Util.fileToJSONObject(notCircuit));
		String sbmlResult = converter.convert(circuit);
		
		assertTrue(sbmlResult.contains("<species id=\"A\""));
		assertTrue(sbmlResult.contains("<species id=\"B\""));
		assertTrue(sbmlResult.contains("<species id=\"mB\""));
		assertTrue(sbmlResult.contains("<reaction id=\"transcription_not_A_mB\""));
		assertTrue(sbmlResult.contains("<reaction id=\"translation_not_mB_B\""));
	}
	
}
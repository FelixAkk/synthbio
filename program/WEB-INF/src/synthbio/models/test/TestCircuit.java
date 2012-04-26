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

package synthbio.models.test;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.util.ArrayList;

import synthbio.Util;
import synthbio.models.Circuit;
import synthbio.models.Gate;

/**
 * Testing the Circuit
 * 
 * @author jieter
 */
public class TestCircuit{
	double delta=0.0001;
	
	@Test
	public void testConstructor(){
		Circuit c=new Circuit("Test");

		assertEquals("Test", c.getName());
	}

	@Test
	public void testConstructor2(){
		Circuit c=new Circuit("Test", "Description");

		assertEquals("Test", c.getName());
		assertEquals("Description", c.getDescription());
		assertEquals(new ArrayList<Gate>(), c.getGates());
	}

	@Test
	public void testFromJSON() throws Exception{
		String json=Util.fileToString("src/synthbio/models/test/exampleCircuit.json");
		
		Circuit c=Circuit.fromJSON(json);

		assertEquals("example.syn", c.getName());
		assertEquals("Logic for this circuit: D = ~(A^B)", c.getDescription());

		assertEquals(2, c.getGates().size());
		//assertEquals(4, c.collectSignals().size());

		assertEquals("[and(A,B)->C @(2.0,2.0)]", c.gateAt(0).toString());
		assertEquals("[not(C)->D @(2.0,4.0)]", c.gateAt(1).toString());

	}
	
}

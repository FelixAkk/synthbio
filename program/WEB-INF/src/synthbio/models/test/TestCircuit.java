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
import synthbio.models.CircuitException;
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

	/**
	 * Test some example circuits
	 */
	@Test
	public void testFromJSON1() throws Exception{
		String json=Util.fileToString("src/synthbio/models/test/exampleCircuit.json");
		
		Circuit c=Circuit.fromJSON(json);

		assertEquals("example.syn", c.getName());
		assertEquals("Logic for this circuit: D = ~(A^B)", c.getDescription());

		assertEquals(2, c.getGates().size());
		//assertEquals(4, c.collectSignals().size());

		assertEquals("[and(A,B)->C @(2.0,2.0)]", c.gateAt(0).toString());
		assertEquals("[not(C)->D @(2.0,4.0)]", c.gateAt(1).toString());
	}

	@Test
	public void testFromJSON2() throws Exception{
		String json=Util.fileToString("src/synthbio/models/test/exampleCircuit2.json");
		Circuit c=Circuit.fromJSON(json);

		assertEquals("example2.syn", c.getName());
		assertEquals("Another example G=(A^B^C^D)", c.getDescription());

		assertEquals(3, c.getGates().size());

		assertEquals("[and(A,B)->E @(2.0,2.0)]", c.gateAt(0).toString());
		assertEquals("[and(C,D)->F @(2.0,4.0)]", c.gateAt(1).toString());
		assertEquals("[and(E,F)->G @(4.0,3.0)]", c.gateAt(2).toString());
	}
	@Test
	public void testFromJSON3() throws Exception{
		String json=Util.fileToString("src/synthbio/models/test/exampleCircuit3.json");
		Circuit c=Circuit.fromJSON(json);

		assertEquals("example3.syn", c.getName());
		assertEquals("Another example H=(~(A^B))^(C^D)", c.getDescription());

		assertEquals(4, c.getGates().size());

		assertEquals("[and(A,B)->E @(2.0,2.0)]", c.gateAt(0).toString());
		assertEquals("[and(C,D)->F @(2.0,4.0)]", c.gateAt(1).toString());
		assertEquals("[and(F,G)->H @(4.0,3.0)]", c.gateAt(2).toString());
		assertEquals("[not(E)->G @(5.0,5.0)]", c.gateAt(3).toString());
	}

	/**
	 * An exception should be thrown if an AND gate has not exactly
	 * two input signals.
	 */
	@Test(expected=CircuitException.class)
	public void testFromJSON_incompleteAnd() throws Exception{
		String json=Util.fileToString("src/synthbio/models/test/incompleteAndCircuit.json");
		Circuit c=Circuit.fromJSON(json);

	}
}

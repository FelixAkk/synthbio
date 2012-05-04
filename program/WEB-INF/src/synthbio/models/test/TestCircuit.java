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
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

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
	 * Load a test JSON file from the file system and convert it to JSON
	 */
	private Circuit loadTestFile(String filename) throws Exception{
		String json=Util.fileToString("data/test/models/"+filename);
		
		return Circuit.fromJSON(json);
	}
	
	/**
	 * Test some valid example circuits.
	 */
	@Test
	public void testFromJSON_examplesyn() throws Exception{
		Circuit c=this.loadTestFile("exampleCircuit.json");

		assertEquals("example.syn", c.getName());
		assertEquals("Logic for this circuit: D = ~(A^B)", c.getDescription());

		assertEquals(2, c.getGates().size());

		assertEquals("[and(A,B)->C @(2.0,2.0)]", c.gateAt(0).toString());
		assertEquals("[not(C)->D @(2.0,4.0)]", c.gateAt(1).toString());

		//test inputs and outputs
		assertThat(c.getInputs(), hasItems("A", "B"));
		assertThat(c.getOutputs(), hasItems("D"));
	}

	@Test
	public void testFromJSON_example2syn() throws Exception{
		Circuit c=this.loadTestFile("exampleCircuit2.json");

		assertEquals("example2.syn", c.getName());
		assertEquals("Another example G=(A^B^C^D)", c.getDescription());

		assertEquals(3, c.getGates().size());

		assertEquals("[and(A,B)->E @(2.0,2.0)]", c.gateAt(0).toString());
		assertEquals("[and(C,D)->F @(2.0,4.0)]", c.gateAt(1).toString());
		assertEquals("[and(E,F)->G @(4.0,3.0)]", c.gateAt(2).toString());

		//test inputs and outputs
		assertThat(c.getInputs(), hasItems("A", "B", "C", "D"));
		assertThat(c.getOutputs(), hasItems("G"));
	}
	@Test
	public void testFromJSON_example3syn() throws Exception{
		Circuit c=this.loadTestFile("exampleCircuit3.json");
		
		assertEquals("example3.syn", c.getName());
		assertEquals("Another example H=(~(A^B))^(C^D)", c.getDescription());

		assertEquals(4, c.getGates().size());

		assertEquals("[and(A,B)->E @(2.0,2.0)]", c.gateAt(0).toString());
		assertEquals("[and(C,D)->F @(2.0,4.0)]", c.gateAt(1).toString());
		assertEquals("[and(F,G)->H @(4.0,3.0)]", c.gateAt(2).toString());
		assertEquals("[not(E)->G @(5.0,5.0)]", c.gateAt(3).toString());

		//test inputs and outputs
		assertThat(c.getInputs(), hasItems("A", "B", "C", "D"));
		assertThat(c.getOutputs(), hasItems("H"));
	}

	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	/**
	 * An exception should be thrown if an AND gate has not exactly
	 * two input signals.
	 */
	@Test
	public void testFromJSON_incompleteAnd() throws Exception{
		thrown.expect(CircuitException.class);
		thrown.expectMessage("At least one AND gate has only one input.");
	
		this.loadTestFile("incompleteAndCircuit.json");
	}
	
	/**
	 * A circuit with gates but without signals is not valid
	 */
	@Test
	public void testFromJSON_gatesButNoSignals() throws Exception{
		thrown.expect(CircuitException.class);
		thrown.expectMessage("Circuit has no signals.");

		this.loadTestFile("gatesButNoSignals.json");
	}

	/**
	 * Signal[to] pointing to non-existant gate
	 */
	@Test
	public void testFromJSON_signalToNullPointer() throws Exception{
		thrown.expect(CircuitException.class);
		thrown.expectMessage("Signal[to] points to non-existant Gate.");

		this.loadTestFile("signalToNullPointer.json");
	}
	
	/**
	 * Signal[from] pointing to non-existant gate
	 */
	@Test
	public void testFromJSON_signalFromNullPointer() throws Exception{
		thrown.expect(CircuitException.class);
		thrown.expectMessage("Signal[from] points to non-existant Gate.");

		this.loadTestFile("signalFromNullPointer.json");
	}

	/**
	 * Ambigious CDS for a gate.
	 */
	@Test
	public void testFromJSON_ambigiousCDS() throws Exception{
		thrown.expect(CircuitException.class);
		thrown.expectMessage("CDS for gate 1 is ambigious");

		this.loadTestFile("ambigiousCDS.json");
	}
}

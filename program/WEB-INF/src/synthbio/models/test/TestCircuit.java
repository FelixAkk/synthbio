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
import synthbio.models.*;

/**
 * Testing the Circuit
 * 
 * @author jieter
 */
public class TestCircuit{
	double delta=0.0001;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	/**
	 * Load a test JSON file from the file system and convert it to JSON
	 */
	private Circuit loadTestFile(String filename) throws Exception{
		String json=Util.fileToString("data/test/models/"+filename);
		
		return Circuit.fromJSON(json);
	}

	/**
	 * Return a gate (and(A,B)->C).
	 */
	private Gate getAndGate(){
		return new Gate(
			new AndPromotor("A", "B", 1, 2, 3),
			new CDS("C", 1, 2, 3),
			new Position()
		);
	}
	
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
	 * Check if no exceptions are thrown if a circuit with no gates is
	 * validated.
	 */
	@Test
	public void testValidity_empty() throws Exception{
		Circuit c=new Circuit("foo", "bar");

		c.validate();
	}
	
	@Test
	public void testValidity_invalidGate1()throws Exception{
		thrown.expect(CircuitException.class);
		thrown.expectMessage("Gate [and(?)->C @(0.0,0.0)] has no Promotor set.");
		
		Circuit c=new Circuit("foo", "bar");
		Gate g=this.getAndGate();
		g.setPromotor(null);
		c.addGate(g);
		
		c.addInput("A");
		c.addInput("B");
		c.addOutput("C");

		c.validate();
	}
	
	@Test
	public void testValidity_invalidGate2()throws Exception{
		thrown.expect(CircuitException.class);
		thrown.expectMessage("Gate [and(A,B)->? @(0.0,0.0)] has no CDS set.");
		
		Circuit c=new Circuit("foo", "bar");
		Gate g=this.getAndGate();
		g.setCDS(null);
		c.addGate(g);
		
		c.addInput("A");
		c.addInput("B");
		c.addOutput("C");

		c.validate();
	}

	
	@Test
	public void testValidity_oneGateNoInputs()throws Exception{
		thrown.expect(CircuitException.class);
		thrown.expectMessage("Circuit with one Gate should have at least one input.");
		
		Circuit c=new Circuit("foo", "bar");
		c.addGate(this.getAndGate());
		c.addOutput("C");

		c.validate();
	}
	
	@Test
	public void testValidity_oneGateNoOutputs()throws Exception{
		thrown.expect(CircuitException.class);
		thrown.expectMessage("Circuit with one Gate should have at least one output.");
		
		Circuit c=new Circuit("foo", "bar");
		c.addGate(this.getAndGate());
		c.addInput("A");
		c.addInput("B");

		c.validate();
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

	/**
	 * Incorrect signal definition: using string literals to define
	 * gate indices in a signal.
	 */
	@Test
	public void testFromJSON_StringIndexFrom() throws Exception{
		thrown.expect(CircuitException.class);
		thrown.expectMessage("Signal[from] should be either a integer index pointing to a gate or the string 'input'");

		this.loadTestFile("invalidGateIndex.json");
	}
}

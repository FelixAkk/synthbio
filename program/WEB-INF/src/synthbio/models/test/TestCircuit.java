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
import static org.junit.Assert.assertTrue;
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
	 * Return a gate (and(A,B)->C).
	 */
	private Gate getAndGate(){
		return new Gate(
			new AndPromotor("A", "B", 1, 2, 3),
			new CDS("C", 1, 2, 3),
			new Position()
		);
	}
	private Circuit getValidCircuit(){
		Circuit c=new Circuit("foo", "bar");
		c.addGate(this.getAndGate());
		c.addInput("A");
		c.addInput("B");

		c.setSimulationLength(40);
		c.addSimulationInput("A", "LLLH");
		c.addSimulationInput("B", "HLHL");
		
		return c;
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

	/**
	 * A Circuit with a gate with a null-Promotor should not validate.
	 */
	@Test
	public void testValidity_invalidGate1() throws Exception{
		thrown.expect(CircuitException.class);
		thrown.expectMessage("Gate [and(?)->C @(0.0,0.0)] has no Promotor set.");
		
		Circuit c=new Circuit("foo", "bar");
		Gate g=this.getAndGate();
		g.setPromotor(null);
		c.addGate(g);
		
		c.addInput("A");
		c.addInput("B");
		c.addOutput("C");

		//should throw the CircuitException.
		c.validate();
	}

	/**
	 * A Circuit with a gate with a null-CDS should not validate.
	 */
	@Test
	public void testValidity_invalidGate2() throws Exception{
		thrown.expect(CircuitException.class);
		thrown.expectMessage("Gate [and(A,B)->? @(0.0,0.0)] has no CDS set.");
		
		Circuit c=new Circuit("foo", "bar");
		Gate g=this.getAndGate();
		g.setCDS(null);
		c.addGate(g);
		
		c.addInput("A");
		c.addInput("B");
		c.addOutput("C");
		
		//should throw the CircuitException.
		c.validate();
	}


	/**
	 * The circuit should not validate if the inputs are not defined.
	 */
	@Test
	public void testValidity_oneGateNoInputs() throws Exception {
		thrown.expect(CircuitException.class);
		thrown.expectMessage("Circuit with one Gate should have at least one input.");
		
		Circuit c=new Circuit("foo", "bar");
		c.addGate(this.getAndGate());
		c.addOutput("C");

		//should throw the CircuitException.
		c.validate();
	}

	/**
	 * The circuit should not validate if the outputs are not defined.
	 */
	@Test
	public void testValidity_oneGateNoOutputs() throws Exception {
		thrown.expect(CircuitException.class);
		thrown.expectMessage("Circuit with one Gate should have at least one output.");
		
		Circuit c=new Circuit("foo", "bar");
		c.addGate(this.getAndGate());
		c.addInput("A");
		c.addInput("B");

		//should throw an CircuitException
		c.validate();
	}
	
	/**
	 * Test if addSimulationInput clears all but (H|L).
	 */
	@Test
	public void testAddSimulationInput() throws Exception {
		Circuit c=new Circuit("foo", "bar");
		c.addGate(this.getAndGate());
		c.addInput("A");
		c.addInput("B");

		c.setSimulationLength(40);
		
		c.addSimulationInput("A", "L /H LL");
		assertThat(c.getSimulationInput("A"), equalTo("LHLL"));

		c.addSimulationInput("B", "HHHhsblsLL");
		assertThat(c.getSimulationInput("B"), equalTo("HHHLL"));
	}
	
	/**
	 * Define some inputs and check if getSimulationInputAt() returns
	 * things we expect it to return...
	 */
	@Test
	public void testInputSignals_inputAt() throws Exception {
		Circuit c=this.getValidCircuit();

		assertThat(c.getSimulationInputAt("A", 0), equalTo("L"));
		assertThat(c.getSimulationInputAt("A", 1), equalTo("L"));
		assertThat(c.getSimulationInputAt("A", 2), equalTo("L"));
		assertThat(c.getSimulationInputAt("A", 3), equalTo("H"));
		assertThat(c.getSimulationInputAt("A", 30), equalTo("H"));
		
		//should tick 40 be included? Since 40 ticks starting at 0
		//yields 39 as upper boundary...
		//If we decide to change: change assertion in
		//Circuit::getSimulationInputAt()
		assertThat(c.getSimulationInputAt("A", 40), equalTo("H"));
		
		assertThat(c.getSimulationInputAt("B", 0), equalTo("H"));
		assertThat(c.getSimulationInputAt("B", 1), equalTo("L"));
		assertThat(c.getSimulationInputAt("B", 2), equalTo("H"));
		assertThat(c.getSimulationInputAt("B", 3), equalTo("L"));
		assertThat(c.getSimulationInputAt("B", 30), equalTo("L"));
		assertThat(c.getSimulationInputAt("B", 40), equalTo("L"));
	}

	/**
	 * Ask for the value of a tick outside the simulated length
	 */
	@Test
	public void testInputSignals_inputAt_outOfBounds() throws Exception {
		thrown.expect(AssertionError.class);
		thrown.expectMessage("Tick should not exceed simulation length.");

		Circuit c=this.getValidCircuit();

		c.getSimulationInputAt("A", 42);
	}
	
	/**
	 * Define an simulation input which is not an input in the circuit.
	 */
	@Test
	public void testInputSignals_invalidInput() throws Exception {
		thrown.expect(AssertionError.class);
		thrown.expectMessage("Circuit should have the input D.");

		Circuit c=new Circuit("foo", "bar");
		c.addGate(this.getAndGate());
		c.addInput("A");
		c.addInput("B");

		c.setSimulationLength(40);
		
		//this should throw an AssertionError
		c.addSimulationInput("D", "LLL");
	}


}

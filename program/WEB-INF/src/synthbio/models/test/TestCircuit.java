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

		c.getSimulationSetting().setLength(40);
		c.getSimulationSetting().addInput("A", "LLLH");
		c.getSimulationSetting().addInput("B", "HLHL");
		
		return c;
	}
	
	@Test
	public void testConstructor(){
		Circuit c=new Circuit("Test");

		assertThat(c.getName(), is(equalTo("Test")));
	}

	@Test
	public void testConstructor2(){
		Circuit c=new Circuit("Test", "Description");

		assertThat(c.getName(), is(equalTo("Test")));
		assertThat(c.getDescription(), is(equalTo("Description")));
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
}
